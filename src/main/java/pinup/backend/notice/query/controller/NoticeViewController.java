package pinup.backend.notice.query.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import pinup.backend.notice.query.service.NoticeQueryService;

@Controller
@RequiredArgsConstructor
public class NoticeViewController {

    private final NoticeQueryService noticeQueryService;

    /** 사용자 공지 목록 페이지 */
    @GetMapping("/notices")
    public String getUserNoticeList(Model model) {
        model.addAttribute("noticeList", noticeQueryService.getAllNotices());
        model.addAttribute("isAdminPage", false);
        return "notices";
    }

    /** 관리자 공지 목록 페이지 */
    @GetMapping("/admin/notices")
    public String getAdminNoticeList(Model model) {
        model.addAttribute("noticeList", noticeQueryService.getAllNotices());
        model.addAttribute("isAdminPage", true);
        return "notices";
    }
}
