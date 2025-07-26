package Aivle.APFM.Service;


import Aivle.APFM.DTO.LoginRequestDTO;
import Aivle.APFM.Entity.ApprovalStatus;
import Aivle.APFM.Entity.Users;
import Aivle.APFM.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public void login(LoginRequestDTO request) {
        Users user = userRepository.findById(request.getEmail())
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


        // 로그인 성공 후 로직 (예: 토큰 발급, 세션 설정 등)
        System.out.println("✅ 로그인 성공: " + user.getUsername());
    }
}

