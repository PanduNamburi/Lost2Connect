package com.lost2found.service;

import com.lost2found.common.api.PaginatedResponse;
import com.lost2found.common.exception.ForbiddenException;
import com.lost2found.common.exception.ResourceNotFoundException;
import com.lost2found.common.utils.DateTimeUtils;
import com.lost2found.dto.CreateLostItemRequest;
import com.lost2found.dto.LostItemResponse;
import com.lost2found.dto.UpdateLostItemRequest;
import com.lost2found.entity.ItemCategory;
import com.lost2found.entity.ItemStatus;
import com.lost2found.entity.Location;
import com.lost2found.entity.LostItem;
import com.lost2found.entity.RoleName;
import com.lost2found.entity.NotificationType;
import com.lost2found.repository.LostItemRepository;
import com.lost2found.security.UserPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service managing Lost Item operations.
 */
@Service
public class LostItemService {

    private final LostItemRepository lostItemRepository;
    private final ImageStorageService imageStorageService;
    private final NotificationService notificationService;

    public LostItemService(LostItemRepository lostItemRepository, ImageStorageService imageStorageService, NotificationService notificationService) {
        this.lostItemRepository = lostItemRepository;
        this.imageStorageService = imageStorageService;
        this.notificationService = notificationService;
    }

    public LostItemResponse createLostItem(CreateLostItemRequest request, UserPrincipal currentUser) {
        LocalDateTime lostDate = DateTimeUtils.parse(request.getLostDate());
        if (lostDate == null) {
            lostDate = LocalDateTime.now();
        }

        Location location = new Location(
                request.getVenueName(),
                request.getCity(),
                request.getLatitude(),
                request.getLongitude()
        );

        LostItem lostItem = new LostItem(
                request.getTitle(),
                request.getDescription(),
                request.getCategory(),
                lostDate,
                location,
                request.getRewardOffered(),
                request.getContactPhoneNumber(),
                currentUser.getId(),
                currentUser.getName()
        );

        if (request.getImageUrls() != null && !request.getImageUrls().isEmpty()) {
            lostItem.setImageUrls(request.getImageUrls());
        }

        LostItem savedItem = lostItemRepository.save(lostItem);

        try {
            String venue = (location.getVenueName() != null && !location.getVenueName().trim().isEmpty()) ? location.getVenueName() : "Campus";
            String title = "New Lost Item Reported: " + savedItem.getTitle();
            String message = currentUser.getName() + " reported a lost " + (savedItem.getCategory() != null ? savedItem.getCategory().name() : "item") + " (" + savedItem.getTitle() + ") at " + venue + ".";
            notificationService.notifyAllUsers(title, message, NotificationType.NEW_LOST_ITEM_REPORTED, savedItem.getId());
        } catch (Exception ignored) {
        }

        return LostItemResponse.fromEntity(savedItem);
    }

    public LostItemResponse getLostItemById(String id) {
        LostItem item = lostItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("LostItem", "id", id));
        return LostItemResponse.fromEntity(item);
    }

    public LostItemResponse updateLostItem(String id, UpdateLostItemRequest request, UserPrincipal currentUser) {
        LostItem item = lostItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("LostItem", "id", id));

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
        if (request.getLostDate() != null) {
            LocalDateTime parsedDate = DateTimeUtils.parse(request.getLostDate());
            if (parsedDate != null) {
                item.setLostDate(parsedDate);
            }
        }
        if (request.getVenueName() != null || request.getCity() != null) {
            Location loc = item.getLocation() != null ? item.getLocation() : new Location();
            if (request.getVenueName() != null) loc.setVenueName(request.getVenueName());
            if (request.getCity() != null) loc.setCity(request.getCity());
            if (request.getLatitude() != null) loc.setLatitude(request.getLatitude());
            if (request.getLongitude() != null) loc.setLongitude(request.getLongitude());
            item.setLocation(loc);
        }
        if (request.getRewardOffered() != null) {
            item.setRewardOffered(request.getRewardOffered());
        }
        if (request.getContactPhoneNumber() != null) {
            item.setContactPhoneNumber(request.getContactPhoneNumber());
        }

        LostItem updatedItem = lostItemRepository.save(item);
        return LostItemResponse.fromEntity(updatedItem);
    }

    public void deleteLostItem(String id, UserPrincipal currentUser) {
        LostItem item = lostItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("LostItem", "id", id));

        verifyOwnershipOrAdmin(item, currentUser);

        lostItemRepository.deleteById(id);
    }

    public LostItemResponse uploadItemImages(String id, List<MultipartFile> files, UserPrincipal currentUser) {
        LostItem item = lostItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("LostItem", "id", id));

        verifyOwnershipOrAdmin(item, currentUser);

        List<String> newUrls = imageStorageService.storeFiles(files);
        item.getImageUrls().addAll(newUrls);

        LostItem updatedItem = lostItemRepository.save(item);
        return LostItemResponse.fromEntity(updatedItem);
    }

    public PaginatedResponse<LostItemResponse> searchLostItems(
            String keyword,
            ItemCategory category,
            String city,
            ItemStatus status,
            int page,
            int size) {

        List<LostItem> matchingItems = lostItemRepository.search(keyword, category, city, status);
        int totalElements = matchingItems.size();

        int start = Math.min(page * size, totalElements);
        int end = Math.min(start + size, totalElements);

        List<LostItemResponse> pageContent = matchingItems.subList(start, end).stream()
                .map(LostItemResponse::fromEntity)
                .toList();

        return PaginatedResponse.of(pageContent, page, size, totalElements);
    }

    private void verifyOwnershipOrAdmin(LostItem item, UserPrincipal currentUser) {
        boolean isAdmin = currentUser.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(RoleName.ROLE_ADMIN.name()));

        if (!isAdmin && !item.getReporterId().equals(currentUser.getId())) {
            throw new ForbiddenException("You do not have permission to modify or delete this item report");
        }
    }
}
