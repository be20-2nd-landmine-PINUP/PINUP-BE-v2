package pinup.backend.notification.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import pinup.backend.member.command.domain.Users;
import pinup.backend.member.command.repository.UserRepository;
import pinup.backend.notification.dto.NotificationRequest;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final Map<Integer, SseEmitter> sseEmitter;
    private final UserRepository userRepository;

    public SseEmitter establishConnect(@AuthenticationPrincipal OAuth2User user) {
        Long connectionTimeout = 60 * 100000L;
        Users userEntity = userRepository.findByEmail(user.getAttribute("email")).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")
        );

        Integer clientIdInt = userEntity.getUserId().intValue();

        SseEmitter emitter = new SseEmitter(connectionTimeout);

        sseEmitter.put(clientIdInt, emitter);

        emitter.onTimeout(() -> {
            try {
                emitter.send(SseEmitter.event()
                        .name("timeout")
                        .data("연결 유지 시간 초과"));
            } catch (IOException ignored) {}
            emitter.complete();  // 이 호출로 onCompletion() 자동 실행됨
        });

        emitter.onCompletion(() -> sseEmitter.remove(clientIdInt));
        emitter.onError(e -> sseEmitter.remove(clientIdInt));

        try {
            emitter.send(SseEmitter.event().name("connect").data("연결 성공"));
        } catch (IOException e) {
            emitter.completeWithError(e);
        }

        return emitter;
    }

    public void sendNotification(Long clientId, NotificationRequest notificationRequest) {
        Integer clientIdInt = clientId.intValue();
        SseEmitter emitter = sseEmitter.get(clientIdInt);

        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                        .name("new notification")
                        .data(notificationRequest));
            }
            catch (IOException e) {
                sseEmitter.remove(clientIdInt);
                emitter.completeWithError(e);
            }
        }
    }
}
