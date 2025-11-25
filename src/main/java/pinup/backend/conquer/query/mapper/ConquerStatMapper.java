package pinup.backend.conquer.query.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ConquerStatMapper {
    Long countUserConqueredRegions(@Param("userId") Long userId);

    Long countUserMonthlyConqueredRegions(@Param("userId") Long userId);
}
