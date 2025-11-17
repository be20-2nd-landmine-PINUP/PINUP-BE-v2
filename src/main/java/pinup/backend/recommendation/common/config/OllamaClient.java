package pinup.backend.recommendation.common.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class OllamaClient {

    private final WebClient webClient = WebClient.builder()
            .baseUrl("http://localhost:11434") // Ollama 기본 주소
            .build(); // 로컬로만 돌리도록 여기에 url 작성함. 차후 yml파일로 옮김

    public String generate(String prompt) {
        Map<String, Object> body = Map.of(
                "model", "llama3:8b",  // 설치한 모델 이름 (ex: "llama3", "qwen2", ...)
                "prompt", prompt,
                "stream", false
        );

        // 🔥 상태코드가 4xx/5xx여도 일단 body를 문자열로 다 받아보자
        String json = webClient.post()
                .uri("/api/generate")
                .bodyValue(body)
                .exchangeToMono(res -> res.bodyToMono(String.class))
                .block();

        System.out.println("[OLLAMA RAW JSON]\n" + json);

        try {
            ObjectMapper om = new ObjectMapper();
            JsonNode root = om.readTree(json);
            return root.path("response").asText(); // 🔥 모델의 텍스트만 뽑기
        } catch (Exception e) {
            return json; // 파싱 실패하면 그냥 전체 JSON 반환
        }
    }
}