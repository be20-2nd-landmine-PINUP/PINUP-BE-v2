package pinup.backend.feed.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT) // 409
public class DuplicateLikeException extends RuntimeException {
    public DuplicateLikeException(Long feedId, Long userId) {
        super("이미 좋아요한 피드입니다. feedId=" + feedId + ", userId=" + userId);
    }
}
