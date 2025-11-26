package pinup.backend.conquer.command.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "conquer_session")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class ConquerSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "session_id")
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "region_id", nullable = false)
    private Long regionId;

    @Column(name = "started_at", nullable = false)
    private Instant startedAt;

    @Column(name = "ended_at")
    private Instant endedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Status status = Status.RUNNING;

    @Column(name = "last_poll_at")
    private Instant lastPollAt;

    @Column(name = "last_location_at")
    private Instant lastLocationAt;

    public static ConquerSession start(Long userId, Long regionId, Instant startedAt) {
        return ConquerSession.builder()
                .userId(userId)
                .regionId(regionId)
                .startedAt(startedAt)
                .status(Status.RUNNING)
                .build();
    }

    public void cancel() {
        this.status = Status.CANCELED;
    }

    public void complete(Instant endedAt) {
        this.status = Status.COMPLETED;
        this.endedAt = endedAt;
    }

    public void adjustStartedAt(Instant startedAt) {
        this.startedAt = startedAt;
    }

    public enum Status {RUNNING, CANCELED, COMPLETED}
}
