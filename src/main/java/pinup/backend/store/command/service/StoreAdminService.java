package pinup.backend.store.command.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pinup.backend.conquer.command.domain.entity.Region;
import pinup.backend.member.command.domain.Admin;
import pinup.backend.store.command.domain.Store;
import pinup.backend.store.command.dto.StoreRequestDto;
import pinup.backend.store.command.dto.StoreUpdateDto;
import pinup.backend.store.command.repository.StoreRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class StoreAdminService {

    private final StoreRepository storeRepository;

    // 등록
    public Store registerItem(StoreRequestDto dto) {

        Store item = Store.builder()
                .admin(Admin.of(dto.getAdminId()))
                .region(dto.getRegionId() != null ? Region.of(dto.getRegionId()) : null)
                .name(dto.getName())
                .description(dto.getDescription())
                .price(dto.getPrice())
                .category(dto.getCategory())
                .limitType(dto.getLimitType())
                .imageUrl(dto.getImageUrl())
                .isActive(true)
                .build();

        return storeRepository.save(item);
    }

    // PATCH 수정
    public Store updateItem(Integer itemId, StoreUpdateDto dto) {

        Store item = storeRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("아이템 없음"));

        // ⭐ 핵심
        item.patch(dto);

        return item; // @Transactional 이므로 save 불필요 (JPA Dirty Checking)
    }

    // 삭제
    public void deleteItem(Integer itemId) {
        storeRepository.deleteById(itemId);
    }
}
