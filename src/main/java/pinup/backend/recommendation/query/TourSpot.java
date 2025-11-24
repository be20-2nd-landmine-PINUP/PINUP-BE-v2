package pinup.backend.recommendation.query;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TourSpot {
    private Long id;
    private String name;          // 관광지명
    private String category;      // category_main (자연/문화/체험/기타 등)
    private List<String> seasons; // ["봄","가을"] 이런 식으로 들어감
    private String region;        // 시/도 (예: "부산광역시")
    private String description;   // 관광지소개
}