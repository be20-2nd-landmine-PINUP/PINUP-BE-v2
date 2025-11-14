package pinup.backend.store.query.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pinup.backend.store.command.dto.StoreDetailResponseDto;
import pinup.backend.store.command.dto.StoreSummaryResponseDto;
import pinup.backend.store.query.dto.StoreHighlightResponseDto;
import pinup.backend.store.query.dto.StorePageResponseDto;
import pinup.backend.store.query.mapper.StoreMapper;

import java.util.Collections;
import java.util.List;

import static org.springframework.beans.support.PagedListHolder.DEFAULT_PAGE_SIZE;

@Service
@RequiredArgsConstructor
public class StoreQueryService {

    private final StoreMapper storeMapper;

    // 전체 아이템 조회
    public List<StoreSummaryResponseDto> getActiveItems() {
        return storeMapper.findAllActiveItems();
    }

    // 특정 아이템 조회
    public StoreDetailResponseDto getItemById(Integer itemId) {
        return storeMapper.findItemById(itemId);
    }
}

    // 기간 한정 + 최신 아이템 조회
    public StoreHighlightResponseDto getHighlights(Integer limitedSize, Integer latestSize)
    {
        int limited = normalizeHighlightSize(limitedSize);
        int latest = normalizeHighlightSize(latestSize);

        return StoreHighlightResponseDto.builder()
                .limitedItems(storeMapper.findLimitedItems(limited))
                .latestItems(storeMapper.findLatestItems(latest))
                .build();
    }

    // 페이지네이션 조회
    public StorePageResponseDto getPagedItems(int page, int size) {
    if (page < 0) {
        throw new IllegalArgumentException("페이지 번호는 0 이상이어야 합니다.");
    }

    if (page >= MAX_PAGE_COUNT) {
        throw new IllegalArgumentException("페이지 번호는 최대 " + + (MAX_PAGE_COUNT - 1) + " 까지만 조회할 수 있습니다.");
    }

    int pageSize = normalizePageSize(size);
    Long totalItems = storeMapper.countActiveItems();
    int totalPages = calculateTotalPages(totalItems, pageSize);

        if (totalPages == 0) {
            return StorePageResponseDto.builder()
                    .page(page)
                    .size(pageSize)
                    .totalItems(totalItems)
                    .totalPages(0)
                    .items(Collections.emptyList())
                    .build();
        }

        if (page >= totalPages) {
            return StorePageResponseDto.builder()
                    .page(page)
                    .size(pageSize)
                    .totalItems(totalItems)
                    .totalPages(totalPages)
                    .items(Collections.emptyList())
                    .build();
        }

        int offset = page * pageSize;

        return StorePageResponseDto.builder()
                .page(page)
                .size(pageSize)
                .totalItems(totalItems)
                .totalPages(totalPages)
                .items(storeMapper.findPagedItems(offset, pageSize))
                .build();
    }

    private int normalizeHighlightSize(Integer size) {
    if (size == null || size <= 0) {
        return DEFAULT_HIGHLIGHT_SIZE;
    }
    return size;
    }

    private int normalizePageSize(int size) {
    if (size <= 0) {
        return DEFAULT_PAGE_SIZE;
    }
    return Math.min(size, DEFAULT_PAGE_SIZE);
    }

    private int calculateTotalPages(long totalItems, int pageSize) {
    if (totalItems == 0) {
        return 0;
    }
    int total = (int) Math.ceil((double) totalItems / pageSize);
    return Math.min(total, MAX_PAGE_COUNT);
    }
}