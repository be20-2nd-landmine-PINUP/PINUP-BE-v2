package pinup.backend.auth.query.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import pinup.backend.auth.query.dto.UserInfoResponse;
import pinup.backend.member.command.domain.Users;
import pinup.backend.member.command.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class AuthQueryService {

    private final UserRepository userRepository;

    public UserInfoResponse getUserInfo(long id) {
        Users user = userRepository.findById(id).orElseThrow(IllegalArgumentException::new);

        return UserInfoResponse.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .loginType(user.getLoginType())
                .name(user.getName())
                .nickname(user.getNickname())
                .build();
    }
}
