package com.lost2found.service;

import com.lost2found.common.exception.ForbiddenException;
import com.lost2found.common.exception.ResourceNotFoundException;
import com.lost2found.dto.ClaimItemRequest;
import com.lost2found.dto.ClaimResponse;
import com.lost2found.entity.Claim;
import com.lost2found.entity.ClaimStatus;
import com.lost2found.entity.FoundItem;
import com.lost2found.entity.ItemStatus;
import com.lost2found.entity.LostItem;
import com.lost2found.entity.NotificationType;
import com.lost2found.entity.RoleName;
import com.lost2found.repository.ClaimRepository;
import com.lost2found.repository.FoundItemRepository;
import com.lost2found.repository.LostItemRepository;
import com.lost2found.security.UserPrincipal;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service managing Claim submission and verification workflow.
 */
@Service
public class ClaimService {

    private final ClaimRepository claimRepository;
    private final LostItemRepository lostItemRepository;
    private final FoundItemRepository foundItemRepository;
    private final NotificationService notificationService;
    private final com.lost2found.repository.UserRepository userRepository;

    public ClaimService(ClaimRepository claimRepository,
                        LostItemRepository lostItemRepository,
                        FoundItemRepository foundItemRepository,
                        NotificationService notificationService,
                        com.lost2found.repository.UserRepository userRepository) {
        this.claimRepository = claimRepository;
        this.lostItemRepository = lostItemRepository;
        this.foundItemRepository = foundItemRepository;
        this.notificationService = notificationService;
        this.userRepository = userRepository;
    }

    public ClaimResponse submitClaim(ClaimItemRequest request, UserPrincipal currentUser) {
        LostItem lostItem = lostItemRepository.findById(request.getLostItemId())
                .orElseThrow(() -> new ResourceNotFoundException("LostItem", "id", request.getLostItemId()));

        FoundItem foundItem = foundItemRepository.findById(request.getFoundItemId())
                .orElseThrow(() -> new ResourceNotFoundException("FoundItem", "id", request.getFoundItemId()));

        Claim claim = new Claim(
                lostItem.getId(),
                foundItem.getId(),
                currentUser.getId(),
                currentUser.getName(),
                foundItem.getFinderId(),
                request.getVerificationAnswer(),
                request.getContactPhoneNumber()
        );

        Claim savedClaim = claimRepository.save(claim);

        // Send notification to the finder
        if (foundItem.getFinderId() != null) {
            notificationService.sendNotification(
                    foundItem.getFinderId(),
                    "New Ownership Claim Submitted",
                    currentUser.getName() + " has submitted a claim for found item: " + foundItem.getTitle(),
                    NotificationType.CLAIM_SUBMITTED,
                    foundItem.getId()
            );
        }

        return ClaimResponse.fromEntity(savedClaim);
    }

    public List<ClaimResponse> getMyClaims(UserPrincipal currentUser) {
        return claimRepository.findByClaimantUserId(currentUser.getId()).stream()
                .sorted(Comparator.comparing(Claim::getCreatedAt).reversed())
                .map(ClaimResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public List<ClaimResponse> getReceivedClaims(UserPrincipal currentUser) {
        return claimRepository.findByFinderUserId(currentUser.getId()).stream()
                .sorted(Comparator.comparing(Claim::getCreatedAt).reversed())
                .map(ClaimResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public ClaimResponse updateClaimStatus(String claimId, ClaimStatus newStatus, UserPrincipal currentUser) {
        Claim claim = claimRepository.findById(claimId)
                .orElseThrow(() -> new ResourceNotFoundException("Claim", "id", claimId));

        boolean isAdmin = currentUser.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(RoleName.ROLE_ADMIN.name()));

        if (!isAdmin && (claim.getFinderUserId() == null || !claim.getFinderUserId().equals(currentUser.getId()))) {
            throw new ForbiddenException("You do not have permission to approve or reject this claim");
        }

        claim.setStatus(newStatus);
        Claim updatedClaim = claimRepository.save(claim);

        // Update item statuses on approval and award 5 community points for reunite
        if (newStatus == ClaimStatus.APPROVED) {
            lostItemRepository.findById(claim.getLostItemId()).ifPresent(lost -> {
                lost.setStatus(ItemStatus.CLAIMED);
                lostItemRepository.save(lost);
            });

            foundItemRepository.findById(claim.getFoundItemId()).ifPresent(found -> {
                found.setStatus(ItemStatus.CLAIMED);
                foundItemRepository.save(found);
            });

            if (claim.getFinderUserId() != null) {
                userRepository.findById(claim.getFinderUserId()).ifPresent(u -> {
                    u.setCommunityPoints(u.getCommunityPoints() + 5);
                    userRepository.save(u);
                });
            }
            if (claim.getClaimantUserId() != null) {
                userRepository.findById(claim.getClaimantUserId()).ifPresent(u -> {
                    u.setCommunityPoints(u.getCommunityPoints() + 5);
                    userRepository.save(u);
                });
            }

            // Notify claimant
            notificationService.sendNotification(
                    claim.getClaimantUserId(),
                    "Claim Approved!",
                    "Your claim for item " + claim.getFoundItemId() + " has been approved by the finder.",
                    NotificationType.CLAIM_APPROVED,
                    claim.getFoundItemId()
            );
        } else if (newStatus == ClaimStatus.REJECTED) {
            notificationService.sendNotification(
                    claim.getClaimantUserId(),
                    "Claim Rejected",
                    "Your claim for item " + claim.getFoundItemId() + " was rejected by the finder.",
                    NotificationType.CLAIM_REJECTED,
                    claim.getFoundItemId()
            );
        }

        return ClaimResponse.fromEntity(updatedClaim);
    }
}
