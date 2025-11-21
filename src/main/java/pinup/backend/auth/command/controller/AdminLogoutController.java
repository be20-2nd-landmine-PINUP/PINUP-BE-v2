package pinup.backend.auth.command.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class AdminLogoutController {

    @PostMapping("/logout")
    public ResponseEntity<?> logoutAdmin() {
        return ResponseEntity.ok("Admin logged out successfully");
    }
}
