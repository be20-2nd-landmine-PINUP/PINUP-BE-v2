package pinup.backend.notice.command.service;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import pinup.backend.auth.command.service.CustomAdminDetailsService;
import pinup.backend.member.command.domain.Admin;
import pinup.backend.member.command.repository.AdminRepository;
import pinup.backend.notice.command.dto.NoticePatchRequest;
import pinup.backend.notice.command.dto.NoticePostRequest;
import pinup.backend.notice.command.entity.Notice;
import pinup.backend.notice.command.repository.NoticeRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class NoticeCommandService {

    private final NoticeRepository noticeRepository;
    private final AdminRepository adminRepository;

    public Long postNotice(NoticePostRequest request, User user) {
        Admin admin = adminRepository.findByName(user.getUsername()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Admin not found")
        );

        return noticeRepository.save(Notice.builder()
                .noticeContent(request.getNoticeContent())
                .noticeTitle(request.getNoticeTitle())
                .admin(admin)
                .build()).getNoticeId();
    }

    public void patchNotice(NoticePatchRequest request, User user) {
        Admin admin = adminRepository.findByName(user.getUsername()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Admin not found")
        );
        Notice notice = noticeRepository.findById(request.getNoticeId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Notice not found")
        );

        notice.patchNotice(request);
        notice.patchAuthor(admin);
    }

    public void deleteNotice(Long id) {
        Notice notice = noticeRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Notice not found")
        );

        noticeRepository.delete(notice);
    }
}
