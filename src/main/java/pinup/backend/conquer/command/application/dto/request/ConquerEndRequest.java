package pinup.backend.conquer.command.application.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ConquerEndRequest {
    private Long sessionId;
    private double latitude;
    private double longitude;
}
