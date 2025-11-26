package pinup.backend.notice.command.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import pinup.backend.member.command.domain.Admin;
import pinup.backend.notice.command.dto.NoticePatchRequest;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Notice {
    @Id
    @Column(name = "notice_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long noticeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "admin_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_notice_admin_id")
    )
    private Admin admin;

    @Column(name = "notice_title")
    private String noticeTitle;

    @Column(name = "notice_content")
    private String noticeContent;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Builder
    public Notice(
            String noticeTitle,
            String noticeContent,
            Admin admin
    ) {
        this.noticeTitle = noticeTitle;
        this.noticeContent = noticeContent;
        this.admin = admin;
    }

    public void patchNotice(NoticePatchRequest request) {
        this.noticeTitle = request.getNoticeTitle() == null ? this.noticeTitle : request.getNoticeTitle();
        this.noticeContent = request.getNoticeContent() == null ? this.noticeContent : request.getNoticeContent();
    }

    public Long patchAuthor(Admin admin) {
        this.admin = admin;
        return this.admin.getId();
    }
}
