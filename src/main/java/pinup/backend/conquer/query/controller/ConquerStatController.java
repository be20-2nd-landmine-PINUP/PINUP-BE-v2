package pinup.backend.conquer.query.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pinup.backend.conquer.query.service.ConquerStatService;

@RestController
@RequestMapping("/conquer/{userId}/stats")
@RequiredArgsConstructor
public class ConquerStatController {
    private final ConquerStatService conquerStatService;

    // 1. count user's conquered regions
    @GetMapping("/total")
    public Long userTotalConqueredRegion(@PathVariable Long userId) {
        return conquerStatService.getTotalConqueredRegions(userId);
    }

    // 2. count user's newly conquered region of this month
    @GetMapping("/monthly")
    public Long userMonthlyConqueredRegion(@PathVariable Long userId) {
        return conquerStatService.getMonthlyConqueredRegions(userId);
    }
}
