package pinup.backend.deco.query.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pinup.backend.deco.query.service.DecorateQueryService;
import pinup.backend.deco.query.dto.response.InventoryPageResponseDto;

@RestController
@RequestMapping("/decorate/{userId}")
@RequiredArgsConstructor
public class DecoQueryController {

    private final DecorateQueryService decorateQueryService;

    // 특정 시/도의 특정 아이템을 Pageable로 불러오기
    @GetMapping("/regions/{sidoName}/items")
    public ResponseEntity<Page<InventoryPageResponseDto>> getSidoItemPage(
            @PathVariable Long userId,
            @PathVariable String sidoName,
            Pageable pageable
    ) {
        return ResponseEntity.ok(
                decorateQueryService.getUserSidoInventory(userId, sidoName, pageable)
        );
    }
}
