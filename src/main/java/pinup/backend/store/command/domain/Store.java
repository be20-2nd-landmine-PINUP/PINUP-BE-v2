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

    // FK - Region
    @ManyToOne
    @JoinColumn(name = "region_id", nullable = true)
    private Region region;

    // FK - Admin
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

    @Column(name = "image_url", nullable = false, length = 255)
    private String imageUrl;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // 기존 update (절대 건들지 말 것)
    public void update(StoreRequestDto dto, Region region) {
        this.region = region;
        this.name = dto.getName();
        this.description = dto.getDescription();
        this.price = dto.getPrice();
        this.category = dto.getCategory();
        this.limitType = dto.getLimitType();
        this.imageUrl = dto.getImageUrl();
    }

    // 🔥 PATCH 수정 전용 (새로 추가)
    public void patch(StoreUpdateDto dto) {
        if (dto.getName() != null) this.name = dto.getName();
        if (dto.getDescription() != null) this.description = dto.getDescription();
        if (dto.getPrice() != null) this.price = dto.getPrice();
        if (dto.getCategory() != null) this.category = dto.getCategory();
        if (dto.getLimitType() != null) this.limitType = dto.getLimitType();
        if (dto.getImageUrl() != null) this.imageUrl = dto.getImageUrl();
        if (dto.getIsActive() != null) this.isActive = dto.getIsActive();
    }

    @PrePersist
    private void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
