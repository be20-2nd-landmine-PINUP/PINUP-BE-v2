package pinup.backend.member.query.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pinup.backend.member.query.dto.AdminDashboardResponse;
import pinup.backend.member.query.dto.UserDto;
import pinup.backend.member.query.mapper.AdminMapper;

import java.util.List;

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

    // 최근 가입한 회원 조회
    public List<UserDto> getRecentUsers() {
        return adminMapper.findRecentUsers();
    }

}
