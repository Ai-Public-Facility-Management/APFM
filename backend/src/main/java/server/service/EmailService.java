package server.service;


import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Base64;
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

    public void resetPW(String email) {
        String token = Base64.getEncoder().encodeToString(UUID.randomUUID().toString().getBytes());

        authCodeStore.put(token, new AuthCodeEntry(email, System.currentTimeMillis()));

        String resetUrl = "https://localhost:8082/reset-password?token=" + token;
        String content = "<h1>비밀번호 재설정</h1><p>아래 링크를 클릭해 비밀번호를 재설정하세요.</p>" +
                "<a href=\"" + resetUrl + "\">비밀번호 재설정하기</a>";

        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(email);
            helper.setSubject("APFM 비밀번호 초기화");
            helper.setText(content, true);
            System.out.println("📤 메일 전송 시작...");
            mailSender.send(message);
            System.out.println("✅ 메일 전송 성공!");
        } catch (MessagingException e) {
            throw new RuntimeException("이메일 전송 실패", e);
        }

    }

    //비밀번호 변경 토큰이 유효한지 확인
    public String verifyResetToken(String token) {
        AuthCodeEntry entry = authCodeStore.get(token);
        if (entry != null) {
            long now = System.currentTimeMillis();
            //
            if (now - entry.createdAt <= resetCodeExpirationMillis) {
                return entry.code;
            }
            else{
                authCodeStore.remove(token);
                return null;
            }
        }
        return null;
    }

    public void removeToken(String token) {
        authCodeStore.remove(token);
    }


    public boolean isEmailVerified(String email) {
        return verifiedEmails.contains(email);
    }

    private String generateAuthCode() {
        return String.valueOf((int)(Math.random() * 900000 + 100000)); // 6자리 숫자
    }
}
