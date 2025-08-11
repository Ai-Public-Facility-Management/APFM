package server.service;


import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
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

    @Value("${custom.auth-code-expiration-millis:10800000}")
    private long resetCodeExpirationMillis;

    private final Map<String, AuthCodeEntry> authCodeStore = new ConcurrentHashMap<>();
    private final Set<String> verifiedEmails = ConcurrentHashMap.newKeySet();

    // 비번재설정용: email -> code  (링크/토큰 X, 코드 방식)
    private final Map<String, AuthCodeEntry> resetCodeStore = new ConcurrentHashMap<>();

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

    /* ================= 비밀번호 재설정 코드 ================= */
    public void sendResetCode(String email) {
        String code = generateAuthCode();
        resetCodeStore.put(email, new AuthCodeEntry(code, System.currentTimeMillis()));

        // HTML 메일로 깔끔하게
        String content = "<h2>비밀번호 재설정 코드</h2>"
                + "<p>아래 6자리 코드를 비밀번호 재설정 페이지에 입력하세요.</p>"
                + "<div style='font-size:22px;font-weight:bold;letter-spacing:3px;'>" + code + "</div>"
                + "<p style='color:#888'>코드 유효기간: " + (resetCodeExpirationMillis/60000) + "분</p>";

        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(email);
            helper.setSubject("APFM 비밀번호 재설정 코드");
            helper.setText(content, true);
            System.out.println("📤 메일 전송 시작...");
            mailSender.send(message);
            System.out.println("✅ 메일 전송 성공!");
        } catch (MessagingException e) {
            throw new RuntimeException("메일 전송 실패", e);
        }
    }

    /** 코드 검증만 (선택 단계) */
    public boolean verifyResetCode(String email, String inputCode) {
        AuthCodeEntry entry = resetCodeStore.get(email);
        if (entry == null) return false;
        long now = System.currentTimeMillis();
        if (now - entry.createdAt > resetCodeExpirationMillis) {
            resetCodeStore.remove(email);
            return false;
        }
        return inputCode.equals(entry.code);
    }

    /** 비밀번호 변경 성공 시 코드 폐기 */
    public void consumeResetCode(String email) {
        resetCodeStore.remove(email);
    }

    public boolean isEmailVerified(String email) {
        return verifiedEmails.contains(email);
    }

    private String generateAuthCode() {
        return String.valueOf((int)(Math.random() * 900000 + 100000)); // 6자리 숫자
    }
}
