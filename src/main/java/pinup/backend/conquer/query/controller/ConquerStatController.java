package pinup.backend.conquer.query.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pinup.backend.auth.command.service.AuthenticatedUserService;
import pinup.backend.conquer.query.service.ConquerStatService;

@RestController
@RequestMapping("/conquer/stats")
@RequiredArgsConstructor
public class ConquerStatController {
    private final ConquerStatService conquerStatService;
    private final AuthenticatedUserService authenticatedUserService;

    // 1. count user's conquered regions
    @GetMapping("/total")
    public Long userTotalConqueredRegion(Authentication authentication) {
        Long userId = authenticatedUserService.getCurrentUserId(authentication);
        return conquerStatService.getTotalConqueredRegions(userId);
    }

    // 2. count user's newly conquered region of this month
    @GetMapping("/monthly")
    public Long userMonthlyConqueredRegion(Authentication authentication) {
        Long userId = authenticatedUserService.getCurrentUserId(authentication);
        return conquerStatService.getMonthlyConqueredRegions(userId);
    }
}
