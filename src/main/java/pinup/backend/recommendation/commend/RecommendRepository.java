package pinup.backend.recommendation.commend;

import org.springframework.data.jpa.repository.JpaRepository;
import pinup.backend.recommendation.entity.Recommend;

import java.util.Optional;

public interface RecommendRepository extends JpaRepository<Recommend, Long> {
    // userId 기준으로 "가장 최근" 추천 1개
    Optional<Recommend> findTopByUserUserIdOrderByRecommendAtDesc(Long userId);

}
