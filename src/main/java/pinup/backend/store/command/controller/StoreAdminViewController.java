package pinup.backend.store.command.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import pinup.backend.store.query.service.StoreQueryService;

@Controller
@RequiredArgsConstructor
public class StoreAdminViewController {

    private final StoreQueryService storeQueryService;

    // 아이템 목록 페이지
    @GetMapping("/admin/store")
    public String storeList(Model model) {
        model.addAttribute("itemList", storeQueryService.getActiveItems());
        return "store/store-admin";
    }


}
