package pinup.backend.recommendation.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pinup.backend.recommendation.commend.RecommendCommandService;
import pinup.backend.recommendation.query.RecommendationResponseDTO;
import pinup.backend.recommendation.query.RecommendQueryService;

@RestController
@RequestMapping("/api/recommend")
@RequiredArgsConstructor
public class RecommendController {
    private final RecommendQueryService recommendQueryService;
    private final RecommendCommandService recommendCommandService;

    @PostMapping("/{userId}")
    public ResponseEntity<RecommendationResponseDTO> recommend(@PathVariable Long userId) {

        // 1️⃣ ChatGPT로 추천 생성
        RecommendationResponseDTO response = recommendQueryService.recommendScheduleForUser(userId);

        // 2️⃣ 결과 저장
        recommendCommandService.saveRecommendation(userId, response);

        // 3️⃣ 결과 반환
        return ResponseEntity.ok(response);
    }

}
