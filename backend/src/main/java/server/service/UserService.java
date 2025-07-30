package server.service;

import server.domain.Users;
import server.repository.UsersRepository;
import server.domain.ApprovalStatus;
import server.domain.Department;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import server.dto.SignupRequestDTO;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UsersRepository userRepository;
    private final EmailService emailService;
    private final BCryptPasswordEncoder passwordEncoder;

    public void signup(SignupRequestDTO request) {
        if (!emailService.isEmailVerified(request.getEmail())) {
            throw new RuntimeException("이메일 인증이 완료되지 않았습니다.");
        }

        if (userRepository.existsById(request.getEmail())) {
            throw new RuntimeException("이미 가입된 사용자입니다.");
        }

        if (!isValidPassword(request.getPassword())) {
            throw new IllegalArgumentException("비밀번호는 10자 이상이며 특수문자 1개 이상을 포함해야 합니다.");
        }

        Users user = new Users();
        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setApprovalStatus(ApprovalStatus.PENDING);
        user.setDepartment(Department.valueOf(String.valueOf(request.getDepartment())));



        userRepository.save(user);
    }

    // 비밀번호 조건
    public boolean isValidPassword(String password) {
        // 정규식: 최소 10자, 특수문자 1개 이상 포함
        String pattern = "^(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{10,}$";
        return password != null && password.matches(pattern);
    }

}