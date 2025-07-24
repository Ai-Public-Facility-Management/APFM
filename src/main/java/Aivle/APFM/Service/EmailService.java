package Aivle.APFM.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${custom.auth-code-expiration-millis:1800000}")
    private long authCodeExpirationMillis;

    // 인증 코드 저장소 (임시 Map 사용 / 실무에선 DB나 Redis 사용)
    private final Map<String, String> authCodeStore = new ConcurrentHashMap<>();

    public void sendAuthCode(String toEmail) {
        System.out.println("📨 인증코드 발송 시도 대상: " + toEmail);

        String authCode = generateAuthCode();
        authCodeStore.put(toEmail, authCode);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("APFM 회원가입 인증코드");
        message.setText("인증코드: " + authCode);

        System.out.println("📤 메일 전송 시작...");
        mailSender.send(message);
        System.out.println("✅ 메일 전송 성공!");
    }

    public boolean verifyAuthCode(String email, String inputCode) {
        return inputCode.equals(authCodeStore.get(email));
    }

    private String generateAuthCode() {
        return String.valueOf((int)(Math.random() * 900000 + 100000)); // 6자리 숫자
    }
}
