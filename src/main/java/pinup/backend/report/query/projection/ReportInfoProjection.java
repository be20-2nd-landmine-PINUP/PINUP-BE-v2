package pinup.backend.report.query.projection;

public interface ReportInfoProjection {
    Long getTotal();
    Long getActive();
    Long getSuspended();
}
