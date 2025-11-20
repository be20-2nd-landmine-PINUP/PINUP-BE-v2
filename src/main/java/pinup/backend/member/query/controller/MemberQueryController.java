package pinup.backend.member.query.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pinup.backend.member.command.domain.Users;
import pinup.backend.member.query.dto.MemberResponse;
import pinup.backend.member.query.service.MemberQueryService;
import pinup.backend.point.command.service.PointService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class MemberQueryController {

    private final MemberQueryService memberQueryService;
    private final PointService pointService;

    @GetMapping("/mypage")
    public ResponseEntity<?> myPage(@AuthenticationPrincipal OAuth2User oAuth2User) {

        if (oAuth2User == null) {
            return ResponseEntity.status(401).body("로그인 필요");
        }

        String email = (String) oAuth2User.getAttributes().get("email");

        // 회원 정보
        MemberResponse user = memberQueryService.getMemberInfoByEmail(email);

        // 포인트
        int totalPoints = pointService.getUserTotalPoint(user.getUserId());

        // 프론트에 보낼 JSON
        Map<String, Object> result = new HashMap<>();
        result.put("user", user);
        result.put("totalPoints", totalPoints);

        return ResponseEntity.ok(result);
    }
}