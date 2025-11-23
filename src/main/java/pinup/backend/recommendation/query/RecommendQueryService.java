package pinup.backend.recommendation.query;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pinup.backend.member.command.domain.Users;
import pinup.backend.member.command.repository.UserRepository;
import pinup.backend.recommendation.domain.RecommendRepository;
import pinup.backend.recommendation.infra.llm.OpenAiClient;
import pinup.backend.recommendation.domain.Recommend;
import pinup.backend.recommendation.util.SeasonUtil;

import java.time.LocalDate;
import java.time.Period;

@Service
@RequiredArgsConstructor
public class RecommendQueryService {

    private final UserRepository userRepository;
    private final RecommendRepository recommendRepository;
    private final OpenAiClient OpenAiClient;  // 🔥 이걸로 교체

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
        //프롬프트 생성
        String prompt = buildPrompt(request);
        System.out.println("[PROMPT]\n" + prompt);

        // 🔥 1) OpenAI 호출 → 한 줄짜리 문자열 받기
        String raw = OpenAiClient.generate(prompt);
        System.out.println("[OPENAI RAW]\n" + raw);


        // 6) "region|||title|||description|||regionId" 파싱
        return parseSimple(raw);
    }

    private RecommendationResponseDTO parseSimple(String raw) {
        // 여러 줄 올 수 있으니까, "|||" 들어있는 줄 하나 골라서 사용
        String targetLine = null;
        for (String line : raw.split("\\R")) { // \R = 모든 종류의 줄바꿈
            if (line.contains("|||")) {
                targetLine = line.trim();
                break;
            }
        }
        if (targetLine == null) {
            targetLine = raw.trim().replace("\n", " ");
        }

        String[] parts = targetLine.split("\\|\\|\\|");
        if (parts.length < 4) {
            RecommendationResponseDTO fallback = new RecommendationResponseDTO();
            fallback.setRegion("추천 생성 실패");
            fallback.setTitle("잠시 후 다시 시도해주세요");
            fallback.setDescription(targetLine);
            fallback.setRegionId(0L);
            return fallback;
        }

        RecommendationResponseDTO dto = new RecommendationResponseDTO();
        dto.setRegion(parts[0].trim());
        dto.setTitle(parts[1].trim());
        dto.setDescription(parts[2].trim());

        try {
            dto.setRegionId(Long.parseLong(parts[3].trim()));
        } catch (NumberFormatException e) {
            dto.setRegionId(0L);
        }

        return dto;
    }

    private String convertGender(Users.Gender gender) {
        return switch (gender) {
            case M -> "남성";
            case F -> "여성";
            case U -> "미지정";
        };
    }

    private String buildPrompt(RecommendationPreferenceRequestDTO req) {
        StringBuilder sb = new StringBuilder();

        sb.append("시스템 역할: 당신은 한국 여행 ‘감성 큐레이터’입니다. ")
                .append("사용자의 취향과 지금 계절을 함께 고려해서,\n")
                .append("너라면 이런 분위기를 좋아할 것 같아라는 방식으로 감성적이고 설득력 있는 추천을 제시합니다.\n\n")

                .append("[중요 규칙]\n")
                .append("- 모든 출력은 반드시 자연스러운 한국어로 작성하세요.\n")
                .append("- 추천은 반드시 \"현재 계절에 실제로 방문하기 좋은 장소와 활동\"이어야 합니다.\n")
                .append("- 사용자가 선호하는 계절은 설명과 분위기를 만들 때 참고하세요.\n")
                .append("  예를 들어, 사용자가 여름을 좋아하지만 지금은 겨울이라면,\n")
                .append("  여름의 활기찬 느낌을 떠올리게 하는 겨울 여행 분위기를 제안하세요.\n")
                .append("- 계절에 맞지 않는 활동(겨울에 물놀이, 한여름에 눈꽃축제 등)은 절대 추천하지 마세요.\n")
                .append("- 추천은 한 곳만 선택합니다.\n")
                .append("- 직전에 추천된 지역이 있다면, 그 지역은 이번에 다시 추천하지 마세요.\n")
                .append("- 감성적이되 과장·유치함·이모지 없이 담백한 톤으로 작성하세요.\n\n");

        sb.append("[사용자 정보]\n")
                .append("- 나이: ").append(req.getAge()).append("\n")
                .append("- 성별: ").append(req.getGender()).append("\n")
                .append("- 선호 계절: ").append(req.getPreferredSeason()).append("\n")
                .append("- 현재 계절: ").append(req.getCurrentSeason()).append("\n")
                .append("- 선호 카테고리: ").append(req.getPreferredCategory()).append("\n");

        if (req.getLastRegion() != null) {
            sb.append("- 직전 추천 지역: ").append(req.getLastRegion()).append("\n");
        }

        sb.append("""
                
                [요구사항]
                1) 위 정보를 바탕으로, 지금 계절에 실제로 가기 좋은 한국의 여행지를 1곳 추천하세요.
                2) 설명은 "너라면 이런 분위기를 좋아할 것 같다"는 느낌으로, 취향과 계절이 잘 맞는 이유를 3~5문장으로 작성하세요.
                3) 오전/오후/저녁 일정표는 쓰지 말고, 분위기와 경험 위주로 설명하세요.
                
                [출력 형식]
                아래 형식으로 한 줄만 출력하세요. 구분자는 문자열 "|||"(파이프 3개) 입니다.줄바꿈을 절대 넣지 마세요.
                
                region|||title|||description|||regionId
                
                - region: 추천할 지역명 (예: "북한산 둘레길")
                - title: 한 줄 제목
                - description: 추천 이유/설명 (3~5문장, 줄바꿈 없이 한 줄로, 한국어로 작성)
                - regionId: 숫자. 모르겠다면 0으로 적으세요.
                
                형식을 반드시 지키세요.
                다른 텍스트, 설명, 따옴표, JSON, 마크다운, 줄바꿈은 절대 출력하지 마세요.
                """);

        return sb.toString();
    }
}
