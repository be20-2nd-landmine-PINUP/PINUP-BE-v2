package pinup.backend.store.command.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import pinup.backend.store.command.domain.Store;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoreSummaryResponseDto {

    @Schema(description = "아이템 ID", example = "1")
    private Integer itemId;

    @Schema(description = "아이템 이름", example = "서울 특별 마커")
    private String name;

    @Schema(description = "아이템 가격", example = "5000")
    private Integer price;

    @Schema(description = "아이템 카테고리", example = "MARKER")
    private String category;

    @Schema(description = "아이템 이미지 URL")
    private String imageUrl;

    @Schema(description = "판매 정책", example = "LIMITED")
    private String limitType;

    @Schema(description = "판매 여부", example = "true")
    private Boolean isActive;

    @Schema(description = "등록일")
    private LocalDateTime createdAt;


    /* ============================================================
       👇 여기! 이게 네가 찾는 from() 메서드
       Store → StoreSummaryResponseDto 변환
    ============================================================ */
    public static StoreSummaryResponseDto from(Store store) {
        return StoreSummaryResponseDto.builder()
                .itemId(store.getItemId())
                .name(store.getName())
                .price(store.getPrice())
                .category(store.getCategory().name())
                .imageUrl(store.getImageUrl())
                .limitType(store.getLimitType().name())   //
                .isActive(store.isActive())
                .createdAt(store.getCreatedAt())
                .build();
    }
}
