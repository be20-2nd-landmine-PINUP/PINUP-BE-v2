package pinup.backend.report.command.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@Builder
public class ReportRequest {
    private Long userId;
    private Long feedId;
    private String reason;
}
