package pinup.backend.recommendation.query;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pinup.backend.member.command.domain.Users;
import pinup.backend.member.command.repository.UserRepository;
import pinup.backend.recommendation.domain.RecommendRepository;
import pinup.backend.recommendation.domain.TourSpotRepository;
import pinup.backend.recommendation.infra.llm.OpenAiClient;
import pinup.backend.recommendation.domain.Recommend;
import pinup.backend.recommendation.util.SeasonUtil;

import java.time.LocalDate;
import java.time.Period;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommendQueryService {

    private final UserRepository userRepository;
    private final RecommendRepository recommendRepository;
    private final OpenAiClient OpenAiClient;  // 🔥 이걸로 교체
    private final TourSpotRepository tourSpotRepository;

    public RecommendationResponseDTO recommendForUser(Long userId) {

        // 1️⃣ 유저 정보 조회
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다. userId=" + userId));

        // 2️⃣ 나이 계산
        int age = Period.between(user.getBirthDate(), LocalDate.now()).getYears();

        // 3️⃣ 현재 계절 계산
        String currentSeason = SeasonUtil.getCurrentSeason(); // "봄", "여름", "가을", "겨울"

        // 3️⃣ 직전에 추천된 지역 조회 (없을 수도 있으니 Optional)
        String lastRegion = recommendRepository
                .findTopByUserUserIdOrderByRecommendAtDesc(userId)
                .map(Recommend::getRecommendSpot)
                .orElse(null);

        // 4️⃣ 프롬프트용 요청 DTO 만들기
        RecommendationPreferenceRequestDTO request = new RecommendationPreferenceRequestDTO();
        request.setAge(age);
        request.setGender(convertGender(user.getGender()));
        request.setPreferredSeason(String.valueOf(user.getPreferredSeason()));
        request.setPreferredCategory(String.valueOf(user.getPreferredCategory()));
        request.setCurrentSeason(currentSeason);
        request.setLastRegion(lastRegion);

        // 데이터 기반으로 spot 1개 선택
        TourSpot spot = pickBestSpot(request);

        //프롬프트 생성
        String prompt = buildPrompt(request, spot);
        String raw = OpenAiClient.generate(prompt);

        // 4️⃣ "title|||description" 파싱
        String title = "추천 제목";
        String description = raw;
        String targetLine = null;

        for (String line : raw.split("\\R")) {
            if (line.contains("|||")) {
                targetLine = line.trim();
                break;
            }
        }
        if (targetLine != null) {
            String[] parts = targetLine.split("\\|\\|\\|");
            if (parts.length >= 2) {
                title = parts[0].trim();
                description = parts[1].trim();
            }
        }
        RecommendationResponseDTO response = new RecommendationResponseDTO();
        response.setRegion(spot.getName());
        response.setTitle(title);
        response.setDescription(description);
        response.setRegionId(spot.getId());
        return response;
    }


    private String convertGender(Users.Gender gender) {
        return switch (gender) {
            case M -> "남성";
            case F -> "여성";
            case U -> "미지정";
        };
    }
    private TourSpot pickBestSpot(RecommendationPreferenceRequestDTO pref) {
        List<TourSpot> all = tourSpotRepository.findAll();

        String lastRegion = pref.getLastRegion();

        return all.stream()
                // 1) 직전 추천 지역은 제외
                .filter(spot -> lastRegion == null || !spot.getName().equals(lastRegion))
                // 2) 점수 높은 순으로 정렬
                .sorted(Comparator.comparingInt((TourSpot s) -> scoreByRule(s, pref)).reversed())
                // 3) 맨 위 하나만 선택
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("추천 가능한 관광지가 없습니다."));
    }

    private int scoreByRule(TourSpot spot, RecommendationPreferenceRequestDTO pref) {
        int score = 0;

        // 카테고리 일치
        if (spot.getCategory() != null &&
                spot.getCategory().contains(pref.getPreferredCategory())) {
            score += 50;
        }

        // 선호 계절 vs spot seasons
        if (spot.getSeasons() != null &&
                spot.getSeasons().contains(pref.getPreferredSeason())) {
            score += 30;
        }

        // 현재 계절 vs spot seasons (가중치는 조금 낮게)
        if (spot.getSeasons() != null &&
                spot.getSeasons().contains(pref.getCurrentSeason())) {
            score += 20;
        }
        int age = pref.getAge();
        String cat = spot.getCategory(); // 자연, 체험, 역사, 문화 등
        // 20-30대: 체험/자연 ↑
        if (age < 40) {
            if ("체험".equals(cat) || "자연".equals(cat)) {
                score += 15;   // 약간의 보정
            }
        }

        // 40대 이상: 문화/역사 ↑
        if (age >= 40) {
            if ("문화".equals(cat) || "역사".equals(cat)) {
                score += 15;
            }
        }


        return score;
    }
    private String buildPrompt(RecommendationPreferenceRequestDTO req, TourSpot spot) {
        StringBuilder sb = new StringBuilder();

        sb.append("시스템 역할: 당신은 한국 여행 ‘감성 큐레이터’입니다. ")
                .append("사용자의 취향과 지금 계절, 그리고 이미 선택된 여행지를 바탕으로,\n")
                .append("너라면 이런 분위기를 좋아할 것 같아 라는 느낌으로 감성적이고 설득력 있는 설명을 제공합니다.\n\n")

                .append("[중요 규칙]\n")
                .append("- 이미 추천할 장소는 정해져 있습니다. 장소를 바꾸지 말고, 아래 장소만 설명하세요.\n")
                .append("- 모든 출력은 반드시 한국어로 작성하세요.\n")
                .append("- 출력 형식을 반드시 지키세요.\n\n");

        sb.append("[사용자 정보]\n")
                .append("- 나이: ").append(req.getAge()).append("\n")
                .append("- 성별: ").append(req.getGender()).append("\n")
                .append("- 선호 계절: ").append(req.getPreferredSeason()).append("\n")
                .append("- 현재 계절: ").append(req.getCurrentSeason()).append("\n")
                .append("- 선호 카테고리: ").append(req.getPreferredCategory()).append("\n");

        if (req.getLastRegion() != null) {
            sb.append("- 직전 추천 지역: ").append(req.getLastRegion()).append("\n");
        }

        sb.append("\n[선택된 여행지]\n")
                .append("- 이름: ").append(spot.getName()).append("\n")
                .append("- 카테고리: ").append(spot.getCategory()).append("\n")
                .append("- 대표 계절: ").append(String.join(",", spot.getSeasons())).append("\n")
                .append("- 지역: ").append(spot.getRegion()).append("\n")
                .append("- 설명: ").append(spot.getDescription()).append("\n\n");

        sb.append("""
            [요구사항]
            1) 위 사용자와 여행지 정보를 바탕으로, 이 여행지가 지금 이 사용자에게 잘 맞는 이유를 3~5문장으로 써주세요.
            2) '너라면 이런 분위기를 좋아할 것 같다'는 느낌으로, 과장 없이 담백하게 설명하세요.
            3) 오전/오후/저녁 일정표는 쓰지 말고, 분위기와 경험 위주로 작성하세요.

            [출력 형식]
            title|||description

            - title: 한 줄 제목 (예: "조용한 강변 산책이 어울리는 봄날")
            - description: 추천 이유/설명 (3~5문장, 줄바꿈 없이 한 줄로, 한국어로 작성)

            형식을 반드시 지키세요.
            다른 텍스트, 설명, 따옴표, JSON, 마크다운, 줄바꿈은 절대 출력하지 마세요.
            """);

        return sb.toString();
    }



}
