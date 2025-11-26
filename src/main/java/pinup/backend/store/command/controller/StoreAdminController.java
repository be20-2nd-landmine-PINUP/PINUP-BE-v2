package pinup.backend.store.command.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import pinup.backend.store.command.domain.Store;
import pinup.backend.store.command.dto.StoreRequestDto;
import pinup.backend.store.command.dto.StoreUpdateDto;
import pinup.backend.store.command.service.StoreAdminService;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/store/admin")
public class StoreAdminController {

    private final StoreAdminService service;

    // 등록
    @PostMapping("/items")
    public ResponseEntity<?> register(@RequestBody StoreRequestDto dto) {
        Store created = service.registerItem(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("itemId", created.getItemId(), "message", "등록 완료"));
    }

    // 수정
    @PatchMapping("/items/{itemId}")
    public ResponseEntity<?> update(
            @PathVariable Integer itemId,
            @RequestBody StoreUpdateDto dto
    ) {
        Store updated = service.updateItem(itemId, dto);
        return ResponseEntity.ok(Map.of("itemId", updated.getItemId(), "message", "수정 완료"));
    }

    // 삭제
    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<?> delete(@PathVariable Integer itemId) {
        service.deleteItem(itemId);
        return ResponseEntity.ok(Map.of("message", "삭제 완료"));
    }
}
