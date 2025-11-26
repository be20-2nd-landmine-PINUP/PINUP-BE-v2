package pinup.backend.recommendation.infra.llm;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;
import java.util.Map;

import static org.aspectj.weaver.tools.cache.SimpleCacheFactory.enabled;

@Service
public class OpenAiClient {

    @Value("${openai.api.key}")
    private String apiKey;   // application.yml / 환경변수에서 주입

    private final WebClient webClient = WebClient.builder()
            .baseUrl("https://api.openai.com/v1")
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build();

    public String generate(String prompt) {
        // ✅ 1) 먼저 플래그 확인: 비활성화 상태면 더미 응답 반환
        if (!enabled) {
            System.out.println("[OPENAI] disabled 모드, 더미 응답 반환");
            return "부산 바다 감성 하루 코스|||일정 요약: 오전 - 해운대 해수욕장과 동백섬 산책 / 오후 - 광안리 해변 카페에서 여유와 수변 산책 / 저녁 - 더베이101과 마린시티 야경 감상. 여행 전 체크포인트: 편한 운동화와 바닷바람 대비 겉옷을 챙기고, 대중교통과 주차 가능 구역을 미리 확인한다. 위험 요소: 여름 성수기에는 해변 인파가 많아 이동 시간이 지연될 수 있고, 해변 인근 도로와 방파제가 젖어 미끄러울 수 있으니 야간에는 특히 주의한다. 여행 후 체크포인트: 찍어 둔 사진을 바로 백업하고, 마음에 들었던 카페와 산책 코스를 정리해 다음 방문 계획에 참고하며 동행자와 간단한 후기도 공유한다.\n";
        }
        //  여기 아래는 실제 OpenAI 호출 로직 (enabled=true일 때만 실행)
        Map<String, Object> body = Map.of(
                "model", "o3-mini",   // 💸 저렴한 모델
                "messages", List.of(
                        // 필요하면 여기 system 역할 따로 빼도 됨
                        Map.of("role", "user", "content", prompt)
                ),
                "max_tokens", 512
        );

        try {
            String json = webClient.post()
                    .uri("/chat/completions")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            System.out.println("[OPENAI RAW JSON]\n" + json);

            // choices[0].message.content 꺼내기
            ObjectMapper om = new ObjectMapper();
            JsonNode root = om.readTree(json);
            JsonNode choices = root.path("choices");
            if (!choices.isArray() || choices.isEmpty()) {
                return json; // 형식이 이상하면 그냥 원본 리턴
            }
            String text = choices.get(0).path("message").path("content").asText();
            return (text == null || text.isEmpty()) ? json : text;

        } catch (WebClientResponseException e) {
            // ✅ 여기서 429 처리
            if (e.getStatusCode().value() == 429) {
                System.out.println("[OPENAI] 429 Too Many Requests: " + e.getResponseBodyAsString());
                // 프롬프트에 그대로 쓰일 문장으로 리턴 (혹은 null/예외 등으로 바꿔도 됨)
                return "현재 추천 요청이 많아 잠시 후 다시 시도해주세요.";
            }

            // 그 외 4xx, 5xx 에러 로깅
            System.out.println("[OPENAI] error " + e.getStatusCode() + ": " + e.getResponseBodyAsString());
            throw e; // 필요하면 커스텀 예외로 감싸도 됨

        } catch (Exception e) {
            e.printStackTrace();
            return "여행 추천을 생성하는 중 오류가 발생했습니다. 잠시 후 다시 시도해 주세요.";
        }
    }
}

