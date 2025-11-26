package pinup.backend.conquer.command.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pinup.backend.auth.command.service.AuthenticatedUserService;
import pinup.backend.conquer.command.application.dto.ConquerEndRequest;
import pinup.backend.conquer.command.application.dto.ConquerEndResponse;
import pinup.backend.conquer.command.application.dto.ConquerStartRequest;
import pinup.backend.conquer.command.application.dto.ConquerStartResponse;
import pinup.backend.conquer.command.application.service.ConquerSessionService;

@RestController
@RequestMapping("/conquer")
@RequiredArgsConstructor
public class ConquerController {

    private final ConquerSessionService conquerSessionService;
    private final AuthenticatedUserService authenticatedUserService;

    @PostMapping("/start")
    public ResponseEntity<ConquerStartResponse> startConquering(
            Authentication authentication,
            @RequestBody ConquerStartRequest request) {
        Long userId = authenticatedUserService.getCurrentUserId(authentication);
        ConquerStartResponse response = conquerSessionService.startConquering(userId, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/end")
    public ResponseEntity<ConquerEndResponse> endConquering(
            Authentication authentication,
            @RequestBody ConquerEndRequest request) {
        Long userId = authenticatedUserService.getCurrentUserId(authentication);
        ConquerEndResponse response = conquerSessionService.endConquering(userId, request);
        return ResponseEntity.ok(response);
    }
}
