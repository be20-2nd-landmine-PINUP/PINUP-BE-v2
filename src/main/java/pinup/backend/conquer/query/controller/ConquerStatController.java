package pinup.backend.conquer.query.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pinup.backend.auth.command.service.AuthenticatedUserService;
import pinup.backend.conquer.query.dto.response.UserConqueredTerritoryResponse;
import pinup.backend.conquer.query.service.ConquerStatService;

import java.util.List;

@RestController
@RequestMapping("/conquer/stats")
@RequiredArgsConstructor
public class ConquerStatController {
    private final ConquerStatService conquerStatService;
    private final AuthenticatedUserService authenticatedUserService;

    // 1. count user's conquered regions
    // 총 정복 지역 수
    @GetMapping("/total")
    public Long userTotalConqueredRegion(Authentication authentication) {
        Long userId = authenticatedUserService.getCurrentUserId(authentication);
        return conquerStatService.getTotalConqueredRegions(userId);
    }

    // 2. count user's newly conquered region of this month
    // 이번 달 신규 정복 수
    @GetMapping("/monthly")
    public Long userMonthlyConqueredRegion(Authentication authentication) {
        Long userId = authenticatedUserService.getCurrentUserId(authentication);
        return conquerStatService.getMonthlyConqueredRegions(userId);
    }

    // 3. load the user's territories in list
    @GetMapping("/api/conquer/my-regions")
    public List<UserConqueredTerritoryResponse> loadUserTerritoryList(Authentication authentication) {
        Long userId = authenticatedUserService.getCurrentUserId(authentication);

        return conquerStatService.loadUserTerritoryList(userId);
    }
}
