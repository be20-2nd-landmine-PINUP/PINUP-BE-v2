package pinup.backend.member.query.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pinup.backend.member.query.dto.UserDto;
import pinup.backend.member.query.service.UserQueryService;

import java.util.List;

//회원 조회용
@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class AdminUserQueryController {

    private final UserQueryService userQueryService;

    // 전체 회원 조회
    @GetMapping
    public List<UserDto> getAllUsers() {
        return userQueryService.getAllUsers();
    }

    // 정지된 회원 조회
    @GetMapping("/suspended")
    public List<UserDto> getSuspendedUsers() {
        return userQueryService.getSuspendedUsers();
    }
}
