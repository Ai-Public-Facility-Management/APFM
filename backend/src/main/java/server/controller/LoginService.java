package server.controller;

import server.domain.Users;
import server.domain.UsersRepository;
import server.domain.ApprovalStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class LoginService {

    private final UsersRepository usersRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public void login(LoginRequestDTO request) {

        System.err.println("🔥 passwordEncoder null 여부: " + (passwordEncoder == null));
        System.err.println("🔥 matches('1234', '$2a$10$7zY3O0s/...') = " + passwordEncoder.matches("1234", "$2a$10$7zY3O0s/6NeUwldmL36c0OqYhVp2tQJk4P8LB6yL3Xe9u3Cvzwr8K"));


        Users user = usersRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("가입되지 않은 이메일입니다."));

        if (user.getApprovalStatus() != ApprovalStatus.APPROVED) {
            throw new RuntimeException("관리자의 승인이 필요합니다.");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        System.out.println("입력된 비밀번호: " + request.getPassword());
        System.out.println("DB 비밀번호: " + user.getPassword());
        System.out.println("비교 결과: " + passwordEncoder.matches(request.getPassword(), user.getPassword()));
        System.err.println("🔥 passwordEncoder 클래스: " + passwordEncoder.getClass().getName());
        System.err.println("🔥 매칭 결과: " + passwordEncoder.matches("1234", "$2a$10$7zY3O0s/6NeUwldmL36c0OqYhVp2tQJk4P8LB6yL3Xe9u3Cvzwr8K"));



        // 로그인 성공 후 로직 (예: 토큰 발급, 세션 설정 등)
        System.out.println("✅ 로그인 성공: " + user.getUsername());
    }
    public void init() {
    System.out.println("🔥 passwordEncoder 존재 여부: " + (passwordEncoder != null));
    }
}

