package pinup.backend.deco.query.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Pageable;
import pinup.backend.deco.query.dto.response.InventoryPageResponseDto;

import java.util.List;

@Mapper
public interface DecoMapper {
    List<InventoryPageResponseDto> findAllSidoInventoryItems(
            @Param("userId") Long userId,
            @Param("sidoName") String sidoName,
            @Param("pageable") Pageable pageable
    );

    long countSidoInventoryItems(
            @Param("userId") Long userId,
            @Param("sidoName") String sidoName
    );
}
