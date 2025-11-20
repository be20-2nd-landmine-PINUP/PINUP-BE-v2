package pinup.backend.member.command.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import pinup.backend.member.command.domain.UpdateMemberRequest;
import pinup.backend.member.command.domain.Users;
import pinup.backend.member.command.service.MemberCommandService;

import java.time.LocalDate;

//회원 정보 수정
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class MemberCommandController {

    private final MemberCommandService memberCommandService;

    // 🔵 회원 정보 수정 (Vue → PATCH)
    @PatchMapping("/update")
    public ResponseEntity<?> updateMember(
            @AuthenticationPrincipal OAuth2User oAuth2User,
            @RequestBody UpdateMemberRequest request
    ) {
        if (oAuth2User == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다");
        }

        // OAuth 로그인 정보에서 이메일 가져오기
        String email = (String) oAuth2User.getAttributes().get("email");

        memberCommandService.updateMember(
                email,
                request.getNickname(),
                request.getGender(),
                request.getPreferredCategory(),
                request.getPreferredSeason(),
                request.getBirthDate()
        );

        return ResponseEntity.ok("updated");
    }

    // 🔵 회원 탈퇴
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteMember(@AuthenticationPrincipal OAuth2User oAuth2User) {
        if (oAuth2User == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다");
        }

        String email = oAuth2User.getAttribute("email");
        memberCommandService.deleteMember(email);

        return ResponseEntity.ok("deleted");
    }
}
