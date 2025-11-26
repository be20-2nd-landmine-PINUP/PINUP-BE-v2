package pinup.backend.conquer.query.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pinup.backend.conquer.query.dto.response.UserConqueredTerritoryResponse;
import pinup.backend.conquer.query.mapper.ConquerStatMapper;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ConquerStatService {

    private final ConquerStatMapper conquerStatMapper;

    // 1. Count 총 점령지 수
    public Long getTotalConqueredRegions(Long userId) {
        return defaultZero(conquerStatMapper.countUserConqueredRegions(userId));
    }

    // 2. Count 월별 점령지 수
    public Long getMonthlyConqueredRegions(Long userId) {
        return defaultZero(conquerStatMapper.countUserMonthlyConqueredRegions(userId));
    }

    // 3. Load 사용자의 점령지 리스트
    public List<UserConqueredTerritoryResponse> loadUserTerritoryList(Long userId) {
        return conquerStatMapper.loadUserTerritoryList(userId);
    }


    // 그냥 default 값 출력
    private Long defaultZero(Long value) {
        return value == null ? 0L : value;
    }

}
