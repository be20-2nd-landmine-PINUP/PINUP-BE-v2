package pinup.backend.recommendation.query;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationPreferenceRequestDTO {
    private int age;  // birthDate로 계산
    private String gender;  // "M", "F", "U" 대신 "남성", "여성" 이런 텍스트도 가능
    private String preferredSeason;    // "봄", "여름", ...
    private String preferredCategory;  // "자연", "체험", "역사", "문화"
    private String currentSeason;  // ✅ 오늘 기준 계절 (봄/여름/가을/겨울)
    // 🔥 추가: 직전에 추천된 지역명 (없으면 null)
    private String lastRegion;
}
