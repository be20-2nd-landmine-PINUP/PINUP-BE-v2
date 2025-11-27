package pinup.backend.conquer.command.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ConquerStartResponse {
    private Long sessionId;
    private String regionDepth1;
    private String regionDepth2;
    private String regionDepth3;
}
