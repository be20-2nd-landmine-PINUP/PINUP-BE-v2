package pinup.backend.report.query.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pinup.backend.report.query.projection.ReportInfoProjection;
import pinup.backend.report.query.domain.Report;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    Report findByReportId(Long id);

    @Query(value = """
        SELECT
            COUNT(*) AS total,
            COUNT(CASE WHEN status = 'ACTIVE' THEN 1 END) AS active,
            COUNT(CASE WHEN status = 'SUSPENDED' THEN 1 END) AS suspended
        FROM report;
    """, nativeQuery = true)
    ReportInfoProjection findReportInfo();
}
