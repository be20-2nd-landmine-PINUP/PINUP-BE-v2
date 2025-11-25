package pinup.backend.conquer.query.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pinup.backend.conquer.query.mapper.ConquerStatMapper;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ConquerStatService {

    private final ConquerStatMapper conquerStatMapper;

    public Long getTotalConqueredRegions(Long userId) {
        return defaultZero(conquerStatMapper.countUserConqueredRegions(userId));
    }

    public Long getMonthlyConqueredRegions(Long userId) {
        return defaultZero(conquerStatMapper.countUserMonthlyConqueredRegions(userId));
    }

    private Long defaultZero(Long value) {
        return value == null ? 0L : value;
    }
}
