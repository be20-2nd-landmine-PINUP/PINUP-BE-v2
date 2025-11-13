package pinup.backend.home.query.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import pinup.backend.member.query.service.MemberQueryService;
import pinup.backend.member.query.service.UserQueryService;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private UserQueryService userQueryService;
    private final MemberQueryService memberQueryService;

    // "/" 접근 시 홈으로 리다이렉트
    @GetMapping("/")
    public String redirectToHome() {
        return "redirect:/home";
    }

    @GetMapping("/home")
    public String home(@AuthenticationPrincipal OAuth2User oAuth2User, Model model) {
        if (oAuth2User != null) {
            model.addAttribute("user", oAuth2User.getAttributes());
        } else {
            model.addAttribute("user", null);
        }
        return "home";
    }

    // index test
    @GetMapping("/index")
    public String indexHome() {
        return "index";
    }

    // store test
    @GetMapping("/store")
    public String storePage() {
        return "store";
    }

    @GetMapping("/store/all")
    public String storeAllPage() {
        return "storeall";
    }

}
