package pinup.backend.report.command.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pinup.backend.report.command.dto.request.ReportHandleRequest;
import pinup.backend.report.command.dto.request.ReportRequest;
import pinup.backend.report.command.service.ReportCommandService;

@RestController
@RequestMapping("/report")
@RequiredArgsConstructor
public class ReportCommandController {
    private final ReportCommandService reportCommandService;

    @PostMapping
    public void doReport(@RequestBody ReportRequest reportRequest) {
        reportCommandService.issueReport(reportRequest);
    }

    @PatchMapping
    public void handleReport(@RequestBody ReportHandleRequest reportHandleRequest) {
        reportCommandService.handleReport(reportHandleRequest);
    }
}
