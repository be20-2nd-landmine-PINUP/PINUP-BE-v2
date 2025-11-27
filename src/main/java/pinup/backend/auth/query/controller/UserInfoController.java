package pinup.backend.auth.query.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import pinup.backend.auth.query.dto.UserInfoResponse;
import pinup.backend.auth.query.service.AuthQueryService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class UserInfoController {
    private final AuthQueryService authQueryService;

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

    @GetMapping("/api/admin/user/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public UserInfoResponse getUserInfo(@PathVariable("id") long id) {
        return authQueryService.getUserInfo(id);
    }
}
