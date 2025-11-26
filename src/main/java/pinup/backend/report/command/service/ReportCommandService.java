package pinup.backend.report.command.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import pinup.backend.feed.command.entity.Feed;
import pinup.backend.feed.command.repository.FeedRepository;
import pinup.backend.member.command.domain.Users;
import pinup.backend.member.command.repository.UserRepository;
import pinup.backend.report.command.dto.request.ReportHandleRequest;
import pinup.backend.report.command.dto.request.ReportRequest;
import pinup.backend.report.query.domain.Report;
import pinup.backend.report.query.repository.ReportRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class ReportCommandService {
    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final FeedRepository feedRepository;

    public void issueReport(ReportRequest reportRequest) {
        Users user = userRepository.findById(reportRequest.getUserId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")
        );
        Feed feed = feedRepository.findById(reportRequest.getFeedId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Feed not found")
        );

        reportRepository.save(Report.builder()
                        .user(user)
                        .feed(feed)
                        .reason(reportRequest.getReason())
                .build()
        );
    }

    public void handleReport(ReportHandleRequest reportHandleRequest) {
        Report report =  reportRepository.findById(reportHandleRequest.getReportId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Report not found")
        );
        report.handleReport(reportHandleRequest);
    }
}
