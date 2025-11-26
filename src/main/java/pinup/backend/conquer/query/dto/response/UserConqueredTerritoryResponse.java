package pinup.backend.conquer.query.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import pinup.backend.conquer.command.domain.entity.Region;

@Getter
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserConqueredTerritoryResponse {

    private final String message;
    private final String regionName;
    private final String regionDepth1;
    private final String regionDepth2;
    private final String regionDepth3;

    public static pinup.backend.conquer.command.application.dto.response.ConquerEndResponse of(String status, String message, Region region) {
        return pinup.backend.conquer.command.application.dto.response.ConquerEndResponse.builder()
                .status(status)
                .message(message)
                .regionName(region.getRegionName())
                .regionDepth1(region.getRegionDepth1())
                .regionDepth2(region.getRegionDepth2())
                .regionDepth3(region.getRegionDepth3())
                .build();
    }

    public static pinup.backend.conquer.command.application.dto.response.ConquerEndResponse of(String status, String message) {
        return pinup.backend.conquer.command.application.dto.response.ConquerEndResponse.builder()
                .status(status)
                .message(message)
                .build();
    }
}
