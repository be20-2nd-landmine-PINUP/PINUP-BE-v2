package pinup.backend.deco.query.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryPageResponseDto {
    private String itemName;
    private String imageUrl;
}
