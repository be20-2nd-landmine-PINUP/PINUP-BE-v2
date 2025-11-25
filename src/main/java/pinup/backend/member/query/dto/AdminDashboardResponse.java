package pinup.backend.member.query.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdminDashboardResponse {
    private int userCount;
    private int newUsersToday;
    private int feedCount;
    private int reportPending;
}
