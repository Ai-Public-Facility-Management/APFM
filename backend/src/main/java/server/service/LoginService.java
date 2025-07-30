package server.controller;

import server.domain.JwtUtil;
import server.domain.Users;
import server.repository.UsersRepository;
import server.domain.ApprovalStatus;
import server.dto.LoginRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import server.service.LoginAttemptService;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final UsersRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final LoginAttemptService loginAttemptService;

    public String login(LoginRequestDTO request) {
        Users user = userRepository.findById(request.getEmail())
                .orElseThrow(() -> new RuntimeException("가입되지 않은 이메일입니다."));

        if (user.getApprovalStatus() != ApprovalStatus.APPROVED) {
            throw new RuntimeException("관리자의 승인이 필요합니다.");
        }

        if (loginAttemptService.isBlocked(request.getEmail())) {
            throw new RuntimeException("로그인 시도 횟수를 초과했습니다. 5분 후 다시 시도해주세요.");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            // 실패 기록 추가
            loginAttemptService.loginFailed(request.getEmail());
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        // 로그인 성공 시 실패 기록 제거
        loginAttemptService.loginSucceeded(request.getEmail());

        System.out.println("✅ 로그인 성공: " + user.getUsername());

        return jwtUtil.generateToken(user.getEmail());
    }
}