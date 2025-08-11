package server.service;

import server.domain.JwtUtil;
import server.domain.Users;
import server.repository.UsersRepository;
import server.domain.ApprovalStatus;
import server.dto.LoginRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import server.service.LoginAttemptService;
import server.domain.UserType;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final UsersRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final LoginAttemptService loginAttemptService;

    public String login(LoginRequestDTO request) {
        if (request == null || request.getEmail() == null || request.getPassword() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이메일/비밀번호를 입력하세요.");
        }

        // findByEmail로 일관화(= findById도 동작하지만 가독성 측면)
        Users user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "가입되지 않은 이메일입니다."));

        if (user.getApprovalStatus() != ApprovalStatus.APPROVED) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "관리자의 승인이 필요합니다.");
        }

        if (loginAttemptService.isBlocked(request.getEmail())) {
            throw new ResponseStatusException(
                    HttpStatus.TOO_MANY_REQUESTS, "로그인 시도 횟수를 초과했습니다. 5분 후 다시 시도해주세요.");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            loginAttemptService.loginFailed(request.getEmail());
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "비밀번호가 일치하지 않습니다.");
        }

        // 성공 시 실패 기록 제거
        loginAttemptService.loginSucceeded(request.getEmail());

        System.out.println("✅ 로그인 성공: " + user.getUsername());

        return jwtUtil.generateToken(user.getEmail());
    }
    public Users getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자 정보를 찾을 수 없습니다."));
    }
}
