package pinup.backend.recommendation.domain;

import jakarta.persistence.*;
import lombok.*;
import pinup.backend.member.command.domain.Users;

import java.time.LocalDateTime;

@Entity
@Table(name = "recommend")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Recommend {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recommend_id")
    private Long recommendId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @Column(name = "recommend_spot", length = 500, nullable = false)
    private String recommendSpot;

    // ✅ 추천된 지역(또는 앵커 스팟)의 ID
    //@Column(name = "region_id")
    //private Long regionId;

    @Column(name = "reason", columnDefinition = "TEXT", nullable = false)
    private String reason;

    @Column(name = "recommend_at", nullable = false)
    private LocalDateTime recommendAt;

    @PrePersist
    public void onCreate() {
        this.recommendAt = LocalDateTime.now();
    }
}