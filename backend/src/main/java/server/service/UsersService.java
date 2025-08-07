package server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import server.domain.*;
import server.controller.*;

import server.domain.Users;
import server.repository.UsersRepository;
import server.dto.*;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class UsersService {

    private final UsersRepository usersRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final EmailService emailService;

    // 회원가입 처리
    public void signup(SignupRequestDTO request) {
        if (!emailService.isEmailVerified(request.getEmail())) {
            throw new RuntimeException("이메일 인증이 완료되지 않았습니다.");
        }

        if (usersRepository.existsById(request.getEmail())) {
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

        usersRepository.save(user);
    }

    // 비밀번호 조건
    public boolean isValidPassword(String password) {
        // 정규식: 최소 10자, 특수문자 1개 이상 포함
        String pattern = "^(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{10,}$";
        return password != null && password.matches(pattern);
    }

    // 기타 기본 기능 유지
    public Users createUser(Users user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return usersRepository.save(user);
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
