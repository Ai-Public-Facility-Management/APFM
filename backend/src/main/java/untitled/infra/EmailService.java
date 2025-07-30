package untitled.infra;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class EmailService {
    private static class AuthCodeEntry {
        String code;
        long createdAt;

        AuthCodeEntry(String code, long createdAt) {
            this.code = code;
            this.createdAt = createdAt;
        }
    }

    private final JavaMailSender mailSender;

    @Value("${custom.auth-code-expiration-millis:1800000}")
    private long authCodeExpirationMillis;

    private final Map<String, AuthCodeEntry> authCodeStore = new ConcurrentHashMap<>();
    private final Set<String> verifiedEmails = ConcurrentHashMap.newKeySet();

    public void sendAuthCode(String toEmail) {
        System.out.println("📨 인증코드 발송 시도 대상: " + toEmail);

        String authCode = generateAuthCode();
        authCodeStore.put(toEmail, new AuthCodeEntry(authCode, System.currentTimeMillis()));

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("APFM 회원가입 인증코드");
        message.setText("인증코드: " + authCode);

        System.out.println("📤 메일 전송 시작...");
        mailSender.send(message);
        System.out.println("✅ 메일 전송 성공!");
    }

    public boolean verifyAuthCode(String email, String inputCode) {
        AuthCodeEntry entry = authCodeStore.get(email);
        if (entry != null && inputCode.equals(entry.code)) {
            long now = System.currentTimeMillis();
            if (now - entry.createdAt <= authCodeExpirationMillis) {
                verifiedEmails.add(email);
                authCodeStore.remove(email); // optional: 인증 완료 후 제거
                return true;
            }
        }
        return false;
    }

    public boolean isEmailVerified(String email) {
        return verifiedEmails.contains(email);
    }

    private String generateAuthCode() {
        return String.valueOf((int)(Math.random() * 900000 + 100000)); // 6자리 숫자
    }
}
