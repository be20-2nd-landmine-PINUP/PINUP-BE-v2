package pinup.backend.report.query.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pinup.backend.report.query.domain.Report;
import pinup.backend.report.query.dto.ReportInfoResponse;
import pinup.backend.report.query.dto.ReportListResponse;
import pinup.backend.report.query.dto.ReportSpecificResponse;
import pinup.backend.report.query.projection.ReportInfoProjection;
import pinup.backend.report.query.repository.ReportRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final ReportRepository reportRepository;

    public List<ReportListResponse> getAllReport() {
        List<Report> reports = reportRepository.findAllByOrderByCreatedAtDesc();

        return reports.stream().map(report -> ReportListResponse.builder()
                .reportId(report.getReportId())
                .reason(report.getReason())
                .status(report.getStatus().toString())
                .createdAt(report.getCreatedAt())
                .build()).toList();
    }

    public ReportSpecificResponse getSpecificReport(Long reportId) {
        Report report = reportRepository.findByReportId(reportId);

        return ReportSpecificResponse.builder()
                .reportId(report.getReportId())
                .userId(report.getUser() != null? report.getUser().getUserId() : null)
                .feedId(report.getFeed() != null? report.getFeed().getFeedId() : null)
                .reason(report.getReason())
                .adminStatement(report.getAdminStatement())
                .status(report.getStatus().toString())
                .createdAt(report.getCreatedAt())
                .adminId(report.getAdmin() != null ? report.getAdmin().getId() : null)
                .updatedAt(report.getUpdatedAt())
                .createdAt(report.getCreatedAt())
                .build();
    }

    public ReportInfoResponse getReportInfo() {
        ReportInfoProjection projection = reportRepository.findReportInfo();

        return ReportInfoResponse.builder()
                .total(projection.getTotal())
                .active(projection.getActive())
                .suspended(projection.getSuspended())
                .build();
    }
}
