package server.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;
import server.domain.*;
import server.service.UsersService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Transactional
public class UsersController {

    private final UsersService usersService;

    public UsersController(UsersService usersService) {
        this.usersService = usersService;
    }

    @PostMapping
    public Users create(@RequestBody Users user) {
        return usersService.createUser(user);
    }

    @GetMapping("/{email}")
    public ResponseEntity<Users> getUser(@PathVariable String email) {
        return usersService.getUser(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<Users> list() {
        return usersService.getAllUsers();
    }

    @DeleteMapping("/{email}")
    public void delete(@PathVariable String email) {
        usersService.deleteUser(email);
    }

    @GetMapping("/check-email")
    public ResponseEntity<Boolean> checkEmail(@RequestParam String email) {
        boolean isDuplicated = usersService.isEmailDuplicated(email);
        return ResponseEntity.ok(isDuplicated);
    } //email 중복 확인 api

    /* ===== 비밀번호 재설정(코드) ===== */

    // (1) 코드 요청
    @PostMapping("/reset-code")
    public ResponseEntity<Void> requestResetCode(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        usersService.sendResetCodeToMember(email);
        return ResponseEntity.noContent().build();
    }

    // (2) (선택) 코드 검증만
    @PostMapping("/reset-code/verify")
    public ResponseEntity<Boolean> verifyResetCode(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        String code  = payload.get("code");
        return ResponseEntity.ok(usersService.verifyResetCode(email, code));
    }

    // (3) 최종 비밀번호 변경
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        String code = payload.get("code");
        String newPassword = payload.get("password");
        return ResponseEntity.ok(usersService.resetPasswordWithCode(email, code, newPassword));
    }
}
