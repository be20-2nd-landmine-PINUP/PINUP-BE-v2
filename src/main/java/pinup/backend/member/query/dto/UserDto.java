package pinup.backend.member.query.dto;

import lombok.Data;

@Data
public class UserDto {
    private Long userId;
    private String name;
    private String nickname;
    private String email;
    private String status;
}
