package pinup.backend.member.query.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface AdminMapper {
    int countUsers();
    int countNewUsersToday();
    int countFeeds();
    int countPendingReports();
}
