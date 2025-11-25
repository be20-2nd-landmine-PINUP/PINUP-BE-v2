package pinup.backend.report.command.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import pinup.backend.report.query.domain.ReportStatus;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class ReportHandleRequest {
    private Long reportId;
    private ReportStatus reportStatus;
    private String adminStatement;
}
