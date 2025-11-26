package pinup.backend.notice.command.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;
import pinup.backend.auth.command.service.CustomAdminDetailsService;
import pinup.backend.notice.command.dto.NoticePatchRequest;
import pinup.backend.notice.command.dto.NoticePostRequest;
import pinup.backend.notice.command.service.NoticeCommandService;

@RestController()
@RequestMapping("/notices")
@RequiredArgsConstructor
public class NoticeCommandController {

    private final NoticeCommandService noticeCommandService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Long> postNotice(@RequestBody NoticePostRequest request, @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(noticeCommandService.postNotice(request, user));
    }

    @PatchMapping
    @PreAuthorize("hasRole('ADMIN')")
    public void patchNotice(@RequestBody NoticePatchRequest request, @AuthenticationPrincipal User user) {
        noticeCommandService.patchNotice(request, user);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteNotice(@PathVariable Long id) {
        noticeCommandService.deleteNotice(id);
    }
}
