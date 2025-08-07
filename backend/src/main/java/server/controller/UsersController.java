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

    //비밀번호 초기화 요청
    @GetMapping("/reset")
    public ResponseEntity<Boolean> reqestReset(@RequestParam String email) {

        usersService.resetEmail(email);
        return ResponseEntity.ok(true);
    }

    //비밀번호 초기화
    @PostMapping("/reset-confime")
    public ResponseEntity<String> resetPassword(@RequestBody Map<String, String> payload) {
        return ResponseEntity.ok(usersService.resetPassword(payload));
    }

}
