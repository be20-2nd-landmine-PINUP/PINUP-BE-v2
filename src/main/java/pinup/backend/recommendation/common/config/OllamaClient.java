package pinup.backend.recommendation.common.config;

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
        Map<String, Object> requestBody = Map.of(
                "model", "llama3",  // 설치한 모델 이름 (ex: "llama3", "qwen2", ...)
                "prompt", prompt,
                "stream", false
        );

        OllamaResponse response = webClient.post()
                .uri("/api/generate")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(OllamaResponse.class)
                .block(); // 일단 동기 호출

        if (response == null || response.getResponse() == null) {
            throw new IllegalStateException("Ollama 응답이 비어 있습니다.");
        }

        return response.getResponse();
    }

    // 응답 JSON 매핑용 내부 클래스
    @lombok.Data
    public static class OllamaResponse {
        private String model;
        private String created_at;
        private String response; // 여기에 모델 답변이 들어옴
        // 다른 필드는 필요하면 나중에 추가
    }
}
