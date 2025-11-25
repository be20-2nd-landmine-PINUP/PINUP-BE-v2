package pinup.backend.deco.query.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pinup.backend.deco.query.dto.response.InventoryPageResponseDto;
import pinup.backend.deco.query.mapper.DecoMapper;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DecorateQueryService {

    private final DecoMapper decoMapper;

    // 시도별 유저 인벤토리 조회 (Pageable)
    public Page<InventoryPageResponseDto> getUserSidoInventory(Long userId, String sidoName, Pageable pageable) {
        List<InventoryPageResponseDto> items = decoMapper.findAllSidoInventoryItems(userId, sidoName, pageable);
        long total = decoMapper.countSidoInventoryItems(userId, sidoName);

        return new PageImpl<>(items, pageable, total);
    }
}
