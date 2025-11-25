package pinup.backend.member.query.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pinup.backend.member.query.dto.AdminDashboardResponse;
import pinup.backend.member.query.mapper.AdminMapper;

@Service
@RequiredArgsConstructor
public class AdminQueryService {

    private final AdminMapper adminMapper;

    public AdminDashboardResponse getStats() {

        return AdminDashboardResponse.builder()
                .userCount(adminMapper.countUsers())
                .newUsersToday(adminMapper.countNewUsersToday())
                .feedCount(adminMapper.countFeeds())
                .reportPending(adminMapper.countPendingReports())
                .build();
    }
}
