package pinup.backend.deco.command.controller;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pinup.backend.auth.command.service.AuthenticatedUserService;
import pinup.backend.deco.command.service.DecoHandlingService;

import java.util.Map;

@RestController
@RequestMapping("/decorate")
@RequiredArgsConstructor
public class DecoCommandController {

    private final DecoHandlingService decoHandlingService;
    private final AuthenticatedUserService authenticatedUserService;

    // 시/도별로 있는 지도 꾸미기 방에서 아이템 장착
    @PatchMapping("/{itemId}/add")
    public ResponseEntity<Map<String, String>> addItem(
            Authentication authentication,
            @PathVariable Integer itemId,
            @RequestBody DecoItemCoordinateRequest request
    ) {
        Long userId = authenticatedUserService.getCurrentUserId(authentication);
        decoHandlingService.equipItem(userId, itemId, request.getX(), request.getY());
        return ResponseEntity.ok(Map.of(
                "message", "아이템 장착 완료",
                "userId", String.valueOf(userId),
                "itemId", String.valueOf(itemId)
        ));
    }

    // 시/도별로 있는 지도 꾸미기 방에서 아이템 해제
    @PatchMapping("/{itemId}/delete")
    public ResponseEntity<Map<String, String>> removeItem(
            Authentication authentication,
            @PathVariable Integer itemId
    ){
        Long userId = authenticatedUserService.getCurrentUserId(authentication);
        decoHandlingService.removeItem(userId, itemId);
        return ResponseEntity.ok(Map.of(
                "message", "아이템 해제 완료",
                "userId", String.valueOf(userId),
                "itemId", String.valueOf(itemId)
        ));
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    private static class DecoItemCoordinateRequest {
        private double x;
        private double y;
    }
}
