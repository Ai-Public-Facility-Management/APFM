package server.controller;

import server.domain.Users;
import server.domain.UsersRepository;
import server.domain.ApprovalStatus;
import server.domain.Department;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UsersRepository usersRepository;
    private final EmailService emailService;
    private final BCryptPasswordEncoder passwordEncoder;

    public void signup(SignupRequestDTO request) {
        if (!emailService.isEmailVerified(request.getEmail())) {
            throw new RuntimeException("이메일 인증이 완료되지 않았습니다.");
        }

        if (usersRepository.existsById(request.getEmail())) {
            throw new RuntimeException("이미 가입된 사용자입니다.");
        }

        Users user = new Users();
        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setApprovalStatus(ApprovalStatus.PENDING);
        user.setDepartment(Department.valueOf(String.valueOf(request.getDepartment())));



        usersRepository.save(user);
    }

}

