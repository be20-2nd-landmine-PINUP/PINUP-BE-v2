package pinup.backend.store.query.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import pinup.backend.store.query.service.StoreQueryService;

@Controller
@RequiredArgsConstructor
public class StoreUserViewController {

    private final StoreQueryService storeQueryService;

    // 일반 사용자 스토어 메인 페이지
    @GetMapping("/store")
    public String storeMain(Model model) {
        model.addAttribute("items", storeQueryService.getActiveItems());
        return "store/store-main";
    }

    // 아이템 전체보기
    @GetMapping("/store/store-all")
    public String storeAll(Model model) {
        model.addAttribute("items", storeQueryService.getActiveItems());
        return "store/store-all";  // templates/store/store-all.html
    }
}
