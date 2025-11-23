package pinup.backend.recommendation.infra.llm;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OpenAiClient {

    @Value("${openai.api.key}")
    private String apiKey;   // application.yml / 환경변수에서 주입

    private final WebClient webClient = WebClient.builder()
            .baseUrl("https://api.openai.com/v1")
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build();

    public String generate(String prompt) {
        // Chat Completions 요청 바디
        Map<String, Object> body = Map.of(
                "model", "gpt-4.1-mini",   // 💸 저렴한 모델
                "messages", List.of(
                        // 필요하면 여기 system 역할 따로 빼도 됨
                        Map.of("role", "user", "content", prompt)
                ),
                "max_tokens", 512
        );

        String json = webClient.post()
                .uri("/chat/completions")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        System.out.println("[OPENAI RAW JSON]\n" + json);

        // choices[0].message.content 꺼내기
        try {
            ObjectMapper om = new ObjectMapper();
            JsonNode root = om.readTree(json);
            JsonNode choices = root.path("choices");
            if (!choices.isArray() || choices.isEmpty()) {
                return json;
            }
            String text = choices.get(0).path("message").path("content").asText();
            return (text == null || text.isEmpty()) ? json : text;
        } catch (Exception e) {
            e.printStackTrace();
            return json;
        }
    }
}
