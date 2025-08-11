package server.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import server.domain.*;


import server.domain.Users;
import server.repository.UsersRepository;
import server.dto.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class UsersService {

    private final UsersRepository usersRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Value("${my.admin_mail}")
    private String admin_email;

    @Value("${my.admin_password}")
    private String admin_password;

    @PostConstruct
    public void init() {
        if (!usersRepository.existsById(admin_email)) {
            Users user = new Users(admin_email, passwordEncoder.encode(admin_password));
            usersRepository.save(user);
        }
    }

    // 회원가입 처리
    public void signup(SignupRequestDTO request) {
        if (!emailService.isEmailVerified(request.getEmail())) {
            throw new RuntimeException("이메일 인증이 완료되지 않았습니다.");
        }

        if (usersRepository.existsById(request.getEmail())) {
            throw new RuntimeException("이미 가입된 사용자입니다.");
        }

        isValidPassword(request.getPassword());

        Users user = new Users();
        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setApprovalStatus(ApprovalStatus.PENDING);
        user.setDepartment(Department.valueOf(String.valueOf(request.getDepartment())));
        user.setType(UserType.INSPECTOR);

        usersRepository.save(user);
    }

    /* ================ 비밀번호 재설정(코드 방식) ================ */

    /** 1) 재설정 코드 요청 */
    public void requestPasswordReset(String email) {
        // 프라이버시 보호: 존재 여부 노출 X. 존재하면만 전송, 없으면 조용히 종료.
        if (usersRepository.existsByEmail(email)) {
            emailService.sendResetCode(email);
        }
    }

    /** 2) (선택) 코드 검증만 */
    public boolean verifyResetCode(String email, String code) {
        return emailService.verifyResetCode(email, code);
    }

    /** 3) 이메일 + 코드 + 새비번으로 최종 변경 */
    public String resetPasswordWithCode(String email, String code, String newPassword) {
        if (!usersRepository.existsByEmail(email)) {
            // 존재 안 해도 같은 메시지(프라이버시 보호)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "코드가 유효하지 않거나 만료되었습니다.");
        }
        boolean ok = emailService.verifyResetCode(email, code);
        if (!ok) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "코드가 유효하지 않거나 만료되었습니다.");
        }

        isValidPassword(newPassword);

        Users user = usersRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        user.setPassword(passwordEncoder.encode(newPassword));
        usersRepository.save(user);

        emailService.consumeResetCode(email);
        return "비밀번호 변경 완료";
    }

    /* ================ 기타 공통 ================ */

    public Users createUser(Users user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return usersRepository.save(user);
    }

    public void isValidPassword(String password) {
        String pattern = "^(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{10,}$";
        if (!(password != null && password.matches(pattern)))
            throw new IllegalArgumentException("비밀번호는 10자 이상이며 특수문자 1개 이상을 포함해야 합니다.");
    }

    public Optional<Users> getUser(String email) {
        return usersRepository.findById(email);
    }

    public List<Users> getAllUsers() {
        return (List<Users>) usersRepository.findAll();
    }

    public void deleteUser(String email) {
        usersRepository.deleteById(email);
    }

    public boolean isEmailDuplicated(String email) {
        return usersRepository.existsByEmail(email);
    }
}
