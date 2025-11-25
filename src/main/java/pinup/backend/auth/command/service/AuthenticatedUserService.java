package pinup.backend.auth.command.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import pinup.backend.member.command.domain.Users;
import pinup.backend.member.command.repository.MemberCommandRepository;

@Service
@RequiredArgsConstructor
public class AuthenticatedUserService {

    private final MemberCommandRepository memberCommandRepository;

    public Long getCurrentUserId(Authentication authentication) {
        return getCurrentUser(authentication).getUserId();
    }

    public Users getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("로그인이 필요합니다.");
        }

        String email = authentication.getName();
        if (email == null) {
            throw new IllegalStateException("인증 정보에서 이메일을 찾을 수 없습니다.");
        }

        return memberCommandRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("회원 정보를 찾을 수 없습니다."));
    }
}
