package com.lost2found.controller;

import com.lost2found.common.api.ApiResponse;
import com.lost2found.dto.DashboardStatsResponse;
import com.lost2found.entity.FoundItem;
import com.lost2found.entity.ItemStatus;
import com.lost2found.entity.LostItem;
import com.lost2found.repository.FoundItemRepository;
import com.lost2found.repository.LostItemRepository;
import com.lost2found.repository.UserRepository;
import com.lost2found.security.UserPrincipal;
import com.lost2found.service.SmartMatchService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST Controller for Dashboard Stats and Metrics.
 */
@RestController
@RequestMapping("/api/v1/stats")
public class DashboardController {

    private final LostItemRepository lostItemRepository;
    private final FoundItemRepository foundItemRepository;
    private final UserRepository userRepository;
    private final SmartMatchService smartMatchService;

    public DashboardController(
            LostItemRepository lostItemRepository,
            FoundItemRepository foundItemRepository,
            UserRepository userRepository,
            SmartMatchService smartMatchService) {
        this.lostItemRepository = lostItemRepository;
        this.foundItemRepository = foundItemRepository;
        this.userRepository = userRepository;
        this.smartMatchService = smartMatchService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<DashboardStatsResponse>> getStats(@AuthenticationPrincipal UserPrincipal currentUser) {
        List<LostItem> allLost = lostItemRepository.findAll();
        List<FoundItem> allFound = foundItemRepository.findAll();
        long userCount = userRepository.countUsers();

        long myReportsCount = 0;
        if (currentUser != null && currentUser.getId() != null) {
            String userId = currentUser.getId();
            String userName = currentUser.getName();
            myReportsCount = allLost.stream().filter(i -> userId.equals(i.getReporterId()) || (userName != null && userName.equalsIgnoreCase(i.getReporterName()))).count()
                    + allFound.stream().filter(i -> userId.equals(i.getFinderId()) || (userName != null && userName.equalsIgnoreCase(i.getFinderName()))).count();
        } else {
            myReportsCount = allLost.size() + allFound.size();
        }

        long matchesCount = 0;
        try {
            matchesCount = smartMatchService.findAllMatches().size();
        } catch (Exception ignored) {
        }

        long reunitedCount = allLost.stream().filter(i -> i.getStatus() == ItemStatus.RESOLVED || i.getStatus() == ItemStatus.MATCHED || i.getStatus() == ItemStatus.CLAIMED).count()
                + allFound.stream().filter(i -> i.getStatus() == ItemStatus.RESOLVED || i.getStatus() == ItemStatus.MATCHED || i.getStatus() == ItemStatus.CLAIMED).count();

        DashboardStatsResponse stats = new DashboardStatsResponse(
                allLost.size() + allFound.size(),
                myReportsCount,
                matchesCount,
                reunitedCount,
                userCount
        );

        return ResponseEntity.ok(ApiResponse.success("Dashboard metrics retrieved successfully", stats));
    }
}
