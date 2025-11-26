package pinup.backend.feed.command.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import pinup.backend.member.command.domain.Users;

import java.time.LocalDateTime;

@Entity
@Table(name = "feed_like")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FeedLike {

    @EmbeddedId
    private FeedLikeId feedLikeId;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @ManyToOne
    @MapsId("feedId")
    @JoinColumn(name = "feed_id", nullable = false)
    private Feed feed;

    @CreationTimestamp      //생성 시 자동 기입 예정
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Builder
    public FeedLike(FeedLikeId feedLikeId, Users user, Feed feed, LocalDateTime createdAt) {
        this.feedLikeId = feedLikeId;
        this.user = user;
        this.feed = feed;
        this.createdAt = createdAt;
    }
}
