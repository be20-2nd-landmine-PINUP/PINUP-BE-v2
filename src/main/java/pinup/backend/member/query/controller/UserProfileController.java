package pinup.backend.member.query.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import pinup.backend.member.query.dto.UserDto;
import pinup.backend.member.query.service.UserQueryService;

@RestController
@RequiredArgsConstructor
public class UserProfileController {

    private final UserQueryService userQueryService;

    @GetMapping("/users/{id}")
    public UserDto showUserProfile(@PathVariable Integer id) {
        UserDto targetUser = userQueryService.getUserById(id);

        if (targetUser == null) {
            throw new IllegalArgumentException("존재하지 않는 사용자입니다.");
        }

        return targetUser;
    }
}
