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
        Users user = new Users(admin_email,passwordEncoder.encode(admin_password));
        usersRepository.save(user);
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

        usersRepository.save(user);
    }

    //이메일에 비밀번호 초기화 링크 전송
    public void resetEmail(String email) {
        if (!isEmailDuplicated(email)){
            System.out.println("일치하는 이메일이 없습니다");
            return;
        }
        System.out.println("일치하는 이메일이 있습니다");
        emailService.resetPW(email);
    }

    @Transactional
    public String resetPassword(Map<String, String> payload) {
        String email = emailService.verifyResetToken(payload.get("token"));
        if (email == null) {
            return "유효하지 않거나 만료된 인증코드입니다.";
        }
        Users user = usersRepository.findByEmail(email).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        isValidPassword(payload.get("password"));
        user.setPassword(passwordEncoder.encode(payload.get("password")));
        usersRepository.save(user);
        emailService.removeToken(payload.get("token"));
        return "비밀번호 변경 완료";
    }

    // 기타 기본 기능 유지
    public Users createUser(Users user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return usersRepository.save(user);
    }

    public void isValidPassword(String password) {
        // 정규식: 최소 10자, 특수문자 1개 이상 포함
        String pattern = "^(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{10,}$";
        if(!(password != null && password.matches(pattern)))
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
