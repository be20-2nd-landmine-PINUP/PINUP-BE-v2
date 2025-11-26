package pinup.backend.feed.user;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CurrentUserResponse {
    private final Long id;
    private final String username;
    private final String nickname;
    private final String profileImageUrl;
}
