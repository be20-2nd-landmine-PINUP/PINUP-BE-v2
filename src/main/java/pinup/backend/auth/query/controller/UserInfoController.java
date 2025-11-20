package pinup.backend.auth.query.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class UserInfoController {

    @GetMapping("/api/user/me")
    public Map<String, Object> getCurrentUser(@AuthenticationPrincipal OAuth2User user) {
        Map<String, Object> map = new HashMap<>();

        if (user == null) {
            map.put("authenticated", false);
            return map;
        }

        map.put("authenticated", true);
        map.put("name", user.getAttribute("name"));
        map.put("email", user.getAttribute("email"));
        map.put("picture", user.getAttribute("picture"));

        return map;
    }
}
