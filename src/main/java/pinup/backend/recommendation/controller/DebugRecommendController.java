package pinup.backend.recommendation.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pinup.backend.recommendation.query.RecommendQueryService;
import pinup.backend.recommendation.query.RecommendationPreferenceRequestDTO;
import pinup.backend.recommendation.query.RecommendationResponseDTO;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/debug")
public class DebugRecommendController {

    private final RecommendQueryService recommendQueryService;

    @GetMapping("/recommend")
    public RecommendationResponseDTO debugRecommend() {
        // 가상 유저 취향 (하드코딩)
        RecommendationPreferenceRequestDTO pref = new RecommendationPreferenceRequestDTO();
        pref.setAge(27);
        pref.setGender("남성");
        pref.setPreferredSeason("봄");
        pref.setPreferredCategory("자연");
        pref.setCurrentSeason("봄");

        return recommendQueryService.recommendScheduleForPreference(pref);
    }
}
