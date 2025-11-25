package pinup.backend.deco.query.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pinup.backend.auth.command.service.AuthenticatedUserService;
import pinup.backend.deco.query.dto.response.InventoryPageResponseDto;
import pinup.backend.deco.query.service.DecorateQueryService;

@RestController
@RequestMapping("/decorate")
@RequiredArgsConstructor
public class DecoQueryController {

    private final DecorateQueryService decorateQueryService;
    private final AuthenticatedUserService authenticatedUserService;

    // 특정 시/도의 특정 아이템을 Pageable로 불러오기
    @GetMapping("/regions/{sidoName}/items")
    public ResponseEntity<Page<InventoryPageResponseDto>> getSidoItemPage(
            Authentication authentication,
            @PathVariable String sidoName,
            Pageable pageable
    ) {
        Long userId = authenticatedUserService.getCurrentUserId(authentication);
        return ResponseEntity.ok(
                decorateQueryService.getUserSidoInventory(userId, sidoName, pageable)
        );
    }
}
