package pinup.backend.member.query.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import pinup.backend.member.query.dto.AdminDashboardResponse;
import pinup.backend.member.query.service.AdminQueryService;

@RestController
@RequiredArgsConstructor
public class AdminQueryController {

    private final AdminQueryService adminQueryService;

    @GetMapping("/admin/dashboard/stats")
    public AdminDashboardResponse getStats() {
        return adminQueryService.getStats();
    }
}
