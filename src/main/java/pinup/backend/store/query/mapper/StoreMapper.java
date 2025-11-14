package pinup.backend.store.query.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import pinup.backend.store.command.dto.StoreDetailResponseDto;
import pinup.backend.store.command.dto.StoreSummaryResponseDto;

import java.util.List;

@Mapper
public interface StoreMapper {

    // 전체 아이템 조회
    List<StoreSummaryResponseDto> findAllActiveItems();

    // 특정 아이템 상세 조회
    StoreDetailResponseDto findItemById(Integer itemId);

    // 기간 한정 아이템 조회
    List<StoreSummaryResponseDto> findLimitedItems(@Param("limit") Integer limit);

    // 최신 아이템 조회
    List<StoreSummaryResponseDto> findLatestItems(@Param("limit") Integer limit);

    // 페이지네이션 아이템 조회
    List<StoreSummaryResponseDto> findPagedItems(@Param("offset") int offset, @Param("pageSize") int pageSize);

    // 전체 활성 아이템 수
    Long countActiveItems();

}
