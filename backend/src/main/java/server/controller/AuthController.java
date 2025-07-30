package server.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.dto.SignupRequestDTO;
import server.service.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/auth")
public class AuthController {
    private final EmailService emailService;
    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequestDTO request) {
        try {
            userService.signup(request);
            return ResponseEntity.ok("회원가입 성공");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @PostMapping("/send-code")
    public ResponseEntity<?> sendCode(@RequestParam String email) {
        emailService.sendAuthCode(email);
        return ResponseEntity.ok("인증 코드 전송 완료");
    }

    @PostMapping("/verify-code")
    public ResponseEntity<?> verifyCode(@RequestParam String email, @RequestParam String code) {
        boolean result = emailService.verifyAuthCode(email, code);
        return result ? ResponseEntity.ok("인증 성공") : ResponseEntity.badRequest().body("인증 실패");
    }


}
