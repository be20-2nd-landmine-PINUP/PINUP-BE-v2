package pinup.backend.store.query.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pinup.backend.store.command.dto.StoreDetailResponseDto;
import pinup.backend.store.command.dto.StoreSummaryResponseDto;
import pinup.backend.store.query.dto.StoreHighlightResponseDto;
import pinup.backend.store.query.dto.StorePageResponseDto;
import pinup.backend.store.query.service.StoreQueryService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/store")
@Tag(name = "store query", description = "스토어 조회 API")
public class StoreQueryController {

    private final StoreQueryService storeQueryService;

    // 전체 아이템 목록 조회
    @GetMapping("/items")
    @Operation(summary = "스토어 아이템 목록 조회", description = "판매 중인 스토어 아이템을 조회합니다.")
    public List<StoreSummaryResponseDto> getActivceItems() {
        return storeQueryService.getActiveItems();
    }

    // 특정 아이템 상세 조회
    @GetMapping("/items/{itemId}")
    @Operation(summary = "스토어 아이템 상세 조회", description = "특정 스토어 아이템의 상제 정보를 조회합니다.")
    public StoreDetailResponseDto getItemById(@PathVariable Integer itemId) {
        return storeQueryService.getItemById(itemId);
    }

    //기간 한정 + 최신 아이템 조회
    @GetMapping("/items/highlights")
    @Operation(summary = "기간 한정 및 최신 아이템 조회", description = "기간 한정 아이템과 최신 등록 아이템을 조회합니다.")
    public StoreHighlightResponseDto getHighlights(
            @RequestParam(name = "limitedSize", required = false) Integer limitedSize,
            @RequestParam(name = "latestSize", required = false) Integer latestSize) {
        return storeQueryService.getHighlights(limitedSize, latestSize);
    }

    // 페이징 아이템 조회
    @GetMapping("/items/page")
    @Operation(summary = "스토어 아이템 페이지 조회", description = "스토어 아이템을 페이지 단위로 조회합니다.")
    public StorePageResponseDto getPagedItems(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "6") int size) {
        return storeQueryService.getPagedItems(page, size);
    }

}

