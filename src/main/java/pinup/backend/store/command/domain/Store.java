package pinup.backend.store.command.domain;

import jakarta.persistence.*;
import lombok.*;
import pinup.backend.conquer.command.domain.entity.Region;
import pinup.backend.member.command.domain.Admin;
import pinup.backend.store.command.dto.StoreRequestDto;
import pinup.backend.store.command.dto.StoreUpdateDto;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "store")
@Builder
public class Store {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Integer itemId;

    // FK: Region
    @ManyToOne
    @JoinColumn(name = "region_id", nullable = true)
    private Region region;

    // FK: Admin
    @ManyToOne
    @JoinColumn(name = "admin_id", nullable = false)
    private Admin admin;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, length = 100)
    private String description;

    @Column(nullable = false)
    private int price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private StoreItemCategory category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StoreLimitType limitType;

    @Column(name = "image_url", nullable = true, length = 255)
    private String imageUrl;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /* ============================================================
       기존 update() — “등록 DTO 기반 전체 업데이트”
       (절대 삭제 금지 — 다른 기능이 사용하는 중)
    ============================================================ */
    public void update(StoreRequestDto dto, Region region) {
        this.region = region;
        this.name = dto.getName();
        this.description = dto.getDescription();
        this.price = dto.getPrice();
        this.category = dto.getCategory();
        this.limitType = dto.getLimitType();
        this.imageUrl = dto.getImageUrl();
    }

    /* ============================================================
       신규 patch() — “부분 수정(PATCH DTO)” 전용
       (프론트의 수정/토글에 대응)
    ============================================================ */
    public void patch(StoreUpdateDto dto) {
        if (dto.getName() != null) this.name = dto.getName();
        if (dto.getDescription() != null) this.description = dto.getDescription();
        if (dto.getPrice() != null) this.price = dto.getPrice();
        if (dto.getCategory() != null) this.category = dto.getCategory();
        if (dto.getLimitType() != null) this.limitType = dto.getLimitType();
        if (dto.getImageUrl() != null) this.imageUrl = dto.getImageUrl();

        // ⭐ 판매 상태 토글 (중지/판매중)
        if (dto.getIsActive() != null) this.isActive = dto.getIsActive();
    }

    /* ============================================================
       자동 생성일
    ============================================================ */
    @PrePersist
    private void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
