package pinup.backend.member.query.mapper;

import org.apache.ibatis.annotations.Mapper;
import pinup.backend.member.query.dto.UserDto;

import java.util.List;

@Mapper
public interface AdminMapper {
    int countUsers();
    int countNewUsersToday();
    int countFeeds();
    int countPendingReports();
    List<UserDto> findRecentUsers();
}
