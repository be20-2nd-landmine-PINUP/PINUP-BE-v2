package pinup.backend.auth.query.dto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import pinup.backend.member.command.domain.Users;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class UserInfoResponse {
    private Long userId;
    private Users.LoginType loginType; // GOOGLE, KAKAO, NAVER
    private String name;
    private String email; // 소셜 로그인용 이메일
    private String nickname;
}
