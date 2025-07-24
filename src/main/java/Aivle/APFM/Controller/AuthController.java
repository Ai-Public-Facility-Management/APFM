package Aivle.APFM.Controller;

import Aivle.APFM.Service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/auth")
public class AuthController {
    private final EmailService emailService;

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
