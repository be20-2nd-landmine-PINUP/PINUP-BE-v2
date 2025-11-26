package pinup.backend.recommendation.query;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pinup.backend.member.command.domain.Users;
import pinup.backend.member.command.repository.UserRepository;
import pinup.backend.recommendation.domain.TourSpotRepository;
import pinup.backend.recommendation.infra.llm.OpenAiClient;
import pinup.backend.recommendation.util.SeasonUtil;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDate;
import java.time.Period;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommendQueryService {

    private final UserRepository userRepository;
    private final OpenAiClient openAiClient;  // 🔥 이걸로 교체
    private final TourSpotRepository tourSpotRepository;

    @Value("${openai.enabled:true}")   // 💡 기본값은 true로
    private boolean openAiEnabled;

    // 🔹 디버그용 취향 만들기 (userId랑 무관)
    private RecommendationPreferenceRequestDTO buildDebugPref() {
        RecommendationPreferenceRequestDTO pref = new RecommendationPreferenceRequestDTO();
        pref.setAge(27);
        pref.setGender("남성");
        pref.setPreferredSeason("봄");
        pref.setPreferredCategory("자연");
        pref.setCurrentSeason("봄");
        return pref;
    }

    // gpt를 끄고 연결할 때 사용하는 매서드(하드코딩 되어있음)
    // public RecommendationResponseDTO recommendScheduleForUser(Long userId)에서 연결된 경우
    // 1. 유저정보 조회부터 시작된다.
    public RecommendationResponseDTO recommendScheduleForPreference(RecommendationPreferenceRequestDTO request) {

        // 1) 관광지 선택
        List<TourSpot> spots = pickItinerarySpots(request, 3);

        // 2) 프롬프트 생성
        String prompt = buildItineraryPrompt(request, spots);

        // 3) 🔥 OpenAI 호출 (=> 여기서 연결/키/모델 다 테스트됨)
        String raw = openAiClient.generate(prompt);

        // 4) title|||description 파싱
        String title = "추천 일정";
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

        // 5) 응답 DTO 구성
        RecommendationResponseDTO response = new RecommendationResponseDTO();
        response.setRegion(spots.get(0).getRegion());
        response.setTitle(title);
        response.setDescription(description);

        return response;
    }

    public RecommendationResponseDTO recommendScheduleForUser(Long userId) {

        // 0단계: GPT 비활성화면 디버그(pref) 경로로 바로 우회
        if (!openAiEnabled) {
            RecommendationPreferenceRequestDTO debugPref = buildDebugPref();
            return recommendScheduleForPreference(debugPref);
        }
        // gpt 활성화면 -> 기존 유저 기반 로직 그대로

        // 1️⃣ 유저 정보 조회
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다. userId=" + userId));

        // 2️⃣ 나이 계산
        int age = Period.between(user.getBirthDate(), LocalDate.now()).getYears();

        // 3️⃣ 현재 계절 계산
        String currentSeason = SeasonUtil.getCurrentSeason(); // "봄", "여름", "가을", "겨울"


        // 5️⃣ 프롬프트용 요청 DTO
        RecommendationPreferenceRequestDTO request = new RecommendationPreferenceRequestDTO();
        request.setAge(age);
        request.setGender(convertGender(user.getGender()));
        request.setPreferredSeason(String.valueOf(user.getPreferredSeason()));
        request.setPreferredCategory(String.valueOf(user.getPreferredCategory()));
        request.setCurrentSeason(currentSeason);

        // ✅ 여러 개 spot 선택 (예: 3개)
        List<TourSpot> spots = pickItinerarySpots(request, 3);

        // 프롬프트 생성
        String prompt = buildItineraryPrompt(request, spots);
        String raw = openAiClient.generate(prompt);

        // "title|||description" 파싱은 기존 로직 그대로 사용
        String title = "추천 일정";
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

        // 응답 DTO 구성
        RecommendationResponseDTO response = new RecommendationResponseDTO();
        response.setTitle(title);
        response.setDescription(description);
        return response;
    }

    private String convertGender(Users.Gender gender) {
        return switch (gender) {
            case M -> "남성";
            case F -> "여성";
            case U -> "미지정";
        };
    }
    private List<TourSpot> pickItinerarySpots(RecommendationPreferenceRequestDTO pref, int maxCount) {
        List<TourSpot> all = tourSpotRepository.findAll();

        // 1) 점수 순으로 정렬
        List<TourSpot> sorted = all.stream()
                .sorted(Comparator.comparingInt((TourSpot s) -> scoreByRule(s, pref)).reversed())
                .toList();

        if (sorted.isEmpty()) {
            throw new IllegalStateException("추천 가능한 관광지가 없습니다.");
        }

        // 2) 첫 번째 스팟의 region(시/도)을 anchor로 사용
        TourSpot anchor = sorted.get(0);
        String anchorRegion = anchor.getRegion(); // 예: "부산광역시"

        // 3) 같은 region인 것만 골라서 maxCount개까지 일정에 포함
        List<TourSpot> sameRegion = sorted.stream()
                .filter(s -> anchorRegion.equals(s.getRegion()))
                .limit(maxCount)
                .toList();

        if (sameRegion.isEmpty()) {
            throw new IllegalStateException("같은 지역으로 묶을 수 있는 관광지가 없습니다.");
        }

        return sameRegion;
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

        // 20-30대: 체험/자연/문화ㅣ ↑
        if (age < 40) {
            if ("체험".equals(cat) || "자연".equals(cat) || "문화".equals(cat)) {
                score += 15;   // 약간의 보정
            }
        }

        // 40대 이상: 문화/역사/자연 ↑
        if (age >= 40) {
            if ("문화".equals(cat) || "역사".equals(cat) || "자연".equals(cat)) {
                score += 15;
            }
        }

        return score;
    }

    private String buildItineraryPrompt(RecommendationPreferenceRequestDTO req, List<TourSpot> spots) {
        StringBuilder sb = new StringBuilder();

        sb.append("시스템 역할: 당신은 한국 여행 1일 코스를 설계하는 여행 일정 플래너입니다.\n")
                .append("사용자의 취향과 지금 계절, 그리고 아래에 주어진 여러 여행지를 바탕으로,\n")
                .append("하루짜리 여행 일정을 짜고, 여행 전/후 체크포인트와 위험 요소 코멘트까지 함께 제공합니다.\n\n")

                .append("[중요 규칙]\n")
                .append("- 주어진 여행지들만 사용해서 일정을 구성하세요. 새로운 장소를 추가하지 마세요.\n")
                .append("- 모든 출력은 반드시 한국어로 작성하세요.\n")
                .append("- 출력 형식을 반드시 지키세요.\n\n");

        sb.append("[사용자 정보]\n")
                .append("- 나이: ").append(req.getAge()).append("\n")
                .append("- 성별: ").append(req.getGender()).append("\n")
                .append("- 선호 계절: ").append(req.getPreferredSeason()).append("\n")
                .append("- 현재 계절: ").append(req.getCurrentSeason()).append("\n")
                .append("- 선호 카테고리: ").append(req.getPreferredCategory()).append("\n");

        sb.append("\n[선택된 여행지 목록]\n");
        for (int i = 0; i < spots.size(); i++) {
            TourSpot s = spots.get(i);
            sb.append(i + 1).append(". 이름: ").append(s.getName()).append("\n")
                    .append("   - 카테고리: ").append(s.getCategory()).append("\n")
                    .append("   - 대표 계절: ").append(String.join(",", s.getSeasons())).append("\n")
                    .append("   - 지역: ").append(s.getRegion()).append("\n")
                    .append("   - 설명: ").append(s.getDescription()).append("\n\n");
        }

        sb.append("""
        [요구사항]
        1) 위 여행지들을 모두 포함해서 1일 여행 일정을 설계해 주세요.
        2) 오전 - 오후 - 저녁 순서로 어떤 장소를 방문하면 좋을지, 각 시간대의 활동을 간단히 요약해 주세요.
        3) 여행 전 체크포인트(준비물, 교통, 시간 관련)는 2~4개의 핵심 포인트로 요약해 주세요.
        4) 위험 요소/주의사항은 계절과 지역 특성을 고려해서 2~3개 정도로 요약해 주세요.
        5) 여행 후 체크포인트(사진 정리, 후기, 다음 방문 참고점 등)는 2~3개 정도로 요약해 주세요.
        6) description은 한 줄 안에 다음 내용을 순서대로 자연스럽게 이어서 작성하세요:
           - '일정 요약: ...'
           - '여행 전 체크포인트: ...'
           - '위험 요소: ...'
           - '여행 후 체크포인트: ...'
           예: '일정 요약: 오전 - OO / 오후 - OO / 저녁 - OO. 여행 전 체크포인트: OO, OO. 위험 요소: OO, OO. 여행 후 체크포인트: OO, OO.'
        7) description에는 줄바꿈(개행)을 넣지 말고, 하나의 문장 블록으로만 작성하세요.

        [출력 형식]
        title|||description

        - title: 한 줄 제목 (예: "부산 바다 감성 하루 코스")
        - description: 위 요구사항 6번을 모두 포함하는 한 줄짜리 한국어 문장

        [출력 예시]
        부산 바다 감성 하루 코스|||일정 요약: 오전 - 해운대 산책과 브런치 / 오후 - 광안리 해변 산책과 카페 / 저녁 - 더베이101 야경 감상. 여행 전 체크포인트: 바닷바람을 대비해 겉옷을 챙기고, 대중교통 시간을 미리 확인한다. 위험 요소: 야간에는 해변 인근 도로가 미끄러울 수 있어 편한 운동화를 신는다. 여행 후 체크포인트: 사진을 바로 백업하고, 마음에 드는 장소를 메모해 둔다.

        형식을 반드시 지키세요.
        다른 텍스트, 설명, JSON, 마크다운, 여분의 줄바꿈은 절대 출력하지 마세요.
        """);

        return sb.toString();
    }


}
