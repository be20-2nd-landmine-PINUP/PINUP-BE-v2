package pinup.backend.conquer.query.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import pinup.backend.conquer.query.dto.response.UserConqueredTerritoryResponse;

import java.util.List;

@Mapper
public interface ConquerStatMapper {
    Long countUserConqueredRegions(@Param("userId") Long userId);

    Long countUserMonthlyConqueredRegions(@Param("userId") Long userId);

    List<UserConqueredTerritoryResponse> loadUserTerritoryList(@Param("userId") Long userId);
}
