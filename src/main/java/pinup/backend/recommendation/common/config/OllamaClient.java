package pinup.backend.recommendation.common.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class OllamaClient {


    private final WebClient webClient = WebClient.builder()
            .baseUrl("http://localhost:11434") // 🔥 다시 하드코딩
            .build();

    public String generate(String prompt) {
        Map<String, Object> body = Map.of(
                "model", "exaone3.5:7.8b",  // 설치한 모델 이름
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

        // JSON 안에서 "response" 필드만 꺼내기
        try {
            com.fasterxml.jackson.databind.ObjectMapper om = new com.fasterxml.jackson.databind.ObjectMapper();
            com.fasterxml.jackson.databind.JsonNode root = om.readTree(json);
            String text = root.path("response").asText();
            if (text == null || text.isEmpty()) {
                return json; // 혹시 response가 없으면 전체 JSON 반환
            }
            return text;
        } catch (Exception e) {
            // 파싱 실패하면 전체 json 반환 (나중에 로그 보고 다듬자)
            e.printStackTrace();
            return json;
        }
    }
}