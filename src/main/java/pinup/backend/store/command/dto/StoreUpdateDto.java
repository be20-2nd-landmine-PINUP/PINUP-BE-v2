package pinup.backend.store.command.dto;

import lombok.*;
import pinup.backend.store.command.domain.StoreItemCategory;
import pinup.backend.store.command.domain.StoreLimitType;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoreUpdateDto {

    private String name;
    private String description;
    private Integer price;
    private StoreItemCategory category;
    private StoreLimitType limitType;
    private String imageUrl;
    private Boolean isActive; // 판매중/중지 상태
}
