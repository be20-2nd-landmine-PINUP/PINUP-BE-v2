package pinup.backend.recommendation.query.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pinup.backend.recommendation.common.config.OllamaClient;

@RestController
@RequestMapping("/api/debug")
@RequiredArgsConstructor
public class DebugController {

    private final OllamaClient ollamaClient;

    @GetMapping("/ollama")
    public String testOllama() {
        System.out.println("[DEBUG] /api/debug/ollama 호출됨");  // 🔥 로그
        String prompt = "한국에서 겨울에 가기 좋은 여행지 한 곳만 말해줘.";
        String raw = ollamaClient.generate(prompt);
        System.out.println("[OLLAMA RAW]\n" + raw);
        return raw;
    }

    // 테스트용으로 추가
    @GetMapping("/ping")
    public String ping() {
        System.out.println("[DEBUG] /api/debug/ping 호출됨");
        return "pong";
    }
}
