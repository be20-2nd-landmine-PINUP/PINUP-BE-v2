package pinup.backend.recommendation.query;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class RecommendationResponseDTO {
    private String region;       // 추천된 최종 지역명
    private String title;        // 한 줄 제목
    private String description;  // 추천 이유/설명
   // private Long regionId;          // 매핑되면 저장
}