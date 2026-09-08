package com.lost2found.service;

import com.lost2found.common.api.PaginatedResponse;
import com.lost2found.common.exception.ForbiddenException;
import com.lost2found.common.exception.ResourceNotFoundException;
import com.lost2found.common.utils.DateTimeUtils;
import com.lost2found.dto.CreateFoundItemRequest;
import com.lost2found.dto.FoundItemResponse;
import com.lost2found.dto.UpdateFoundItemRequest;
import com.lost2found.entity.FoundItem;
import com.lost2found.entity.ItemCategory;
import com.lost2found.entity.ItemStatus;
import com.lost2found.entity.Location;
import com.lost2found.entity.RoleName;
import com.lost2found.entity.NotificationType;
import com.lost2found.repository.FoundItemRepository;
import com.lost2found.security.UserPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service managing Found Item operations.
 */
@Service
public class FoundItemService {

    private final FoundItemRepository foundItemRepository;
    private final ImageStorageService imageStorageService;
    private final NotificationService notificationService;

    public FoundItemService(FoundItemRepository foundItemRepository, ImageStorageService imageStorageService, NotificationService notificationService) {
        this.foundItemRepository = foundItemRepository;
        this.imageStorageService = imageStorageService;
        this.notificationService = notificationService;
    }

    public FoundItemResponse createFoundItem(CreateFoundItemRequest request, UserPrincipal currentUser) {
        LocalDateTime foundDate = DateTimeUtils.parse(request.getFoundDate());
        if (foundDate == null) {
            foundDate = LocalDateTime.now();
        }

        Location location = new Location(
                request.getVenueName(),
                request.getCity(),
                request.getLatitude(),
                request.getLongitude()
        );

        FoundItem foundItem = new FoundItem(
                request.getTitle(),
                request.getDescription(),
                request.getCategory(),
                foundDate,
                request.getStorageLocation(),
                location,
                request.getContactPhoneNumber(),
                currentUser.getId(),
                currentUser.getName()
        );

        if (request.getVerificationQuestions() != null) {
            foundItem.setVerificationQuestions(request.getVerificationQuestions());
        }

        if (request.getImageUrls() != null && !request.getImageUrls().isEmpty()) {
            foundItem.setImageUrls(request.getImageUrls());
        }

        FoundItem savedItem = foundItemRepository.save(foundItem);

        try {
            String venue = (location.getVenueName() != null && !location.getVenueName().trim().isEmpty()) ? location.getVenueName() : "Campus";
            String title = "New Found Item Reported: " + savedItem.getTitle();
            String message = currentUser.getName() + " reported a found " + (savedItem.getCategory() != null ? savedItem.getCategory().name() : "item") + " (" + savedItem.getTitle() + ") at " + venue + ".";
            notificationService.notifyAllUsers(title, message, NotificationType.NEW_FOUND_ITEM_REPORTED, savedItem.getId());
        } catch (Exception ignored) {
        }

        return FoundItemResponse.fromEntity(savedItem);
    }

    public FoundItemResponse getFoundItemById(String id) {
        FoundItem item = foundItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("FoundItem", "id", id));
        return FoundItemResponse.fromEntity(item);
    }

    public FoundItemResponse updateFoundItem(String id, UpdateFoundItemRequest request, UserPrincipal currentUser) {
        FoundItem item = foundItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("FoundItem", "id", id));

        verifyOwnershipOrAdmin(item, currentUser);

        if (request.getTitle() != null && !request.getTitle().trim().isEmpty()) {
            item.setTitle(request.getTitle());
        }
        if (request.getDescription() != null && !request.getDescription().trim().isEmpty()) {
            item.setDescription(request.getDescription());
        }
        if (request.getCategory() != null) {
            item.setCategory(request.getCategory());
        }
        if (request.getStatus() != null) {
            item.setStatus(request.getStatus());
        }
        if (request.getFoundDate() != null) {
            LocalDateTime parsedDate = DateTimeUtils.parse(request.getFoundDate());
            if (parsedDate != null) {
                item.setFoundDate(parsedDate);
            }
        }
        if (request.getStorageLocation() != null) {
            item.setStorageLocation(request.getStorageLocation());
        }
        if (request.getVenueName() != null || request.getCity() != null) {
            Location loc = item.getLocation() != null ? item.getLocation() : new Location();
            if (request.getVenueName() != null) loc.setVenueName(request.getVenueName());
            if (request.getCity() != null) loc.setCity(request.getCity());
            if (request.getLatitude() != null) loc.setLatitude(request.getLatitude());
            if (request.getLongitude() != null) loc.setLongitude(request.getLongitude());
            item.setLocation(loc);
        }
        if (request.getContactPhoneNumber() != null) {
            item.setContactPhoneNumber(request.getContactPhoneNumber());
        }
        if (request.getVerificationQuestions() != null) {
            item.setVerificationQuestions(request.getVerificationQuestions());
        }

        FoundItem updatedItem = foundItemRepository.save(item);
        return FoundItemResponse.fromEntity(updatedItem);
    }

    public FoundItemResponse updateStatus(String id, ItemStatus newStatus, UserPrincipal currentUser) {
        FoundItem item = foundItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("FoundItem", "id", id));

        verifyOwnershipOrAdmin(item, currentUser);

        item.setStatus(newStatus);
        FoundItem updatedItem = foundItemRepository.save(item);
        return FoundItemResponse.fromEntity(updatedItem);
    }

    public void deleteFoundItem(String id, UserPrincipal currentUser) {
        FoundItem item = foundItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("FoundItem", "id", id));

        verifyOwnershipOrAdmin(item, currentUser);

        foundItemRepository.deleteById(id);
    }

    public FoundItemResponse uploadItemImages(String id, List<MultipartFile> files, UserPrincipal currentUser) {
        FoundItem item = foundItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("FoundItem", "id", id));

        verifyOwnershipOrAdmin(item, currentUser);

        List<String> newUrls = imageStorageService.storeFiles(files);
        item.getImageUrls().addAll(newUrls);

        FoundItem updatedItem = foundItemRepository.save(item);
        return FoundItemResponse.fromEntity(updatedItem);
    }

    public PaginatedResponse<FoundItemResponse> searchFoundItems(
            String keyword,
            ItemCategory category,
            String city,
            ItemStatus status,
            int page,
            int size) {

        List<FoundItem> matchingItems = foundItemRepository.search(keyword, category, city, status);
        int totalElements = matchingItems.size();

        int start = Math.min(page * size, totalElements);
        int end = Math.min(start + size, totalElements);

        List<FoundItemResponse> pageContent = matchingItems.subList(start, end).stream()
                .map(FoundItemResponse::fromEntity)
                .toList();

        return PaginatedResponse.of(pageContent, page, size, totalElements);
    }

    private void verifyOwnershipOrAdmin(FoundItem item, UserPrincipal currentUser) {
        boolean isAdmin = currentUser.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(RoleName.ROLE_ADMIN.name()));

        if (!isAdmin && !item.getFinderId().equals(currentUser.getId())) {
            throw new ForbiddenException("You do not have permission to modify or delete this found item report");
        }
    }
}
