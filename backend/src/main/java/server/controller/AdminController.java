package server.controller;

import server.domain.ApprovalStatus;
import server.domain.Users;
import server.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UsersRepository userRepository;

    @PostMapping("/approve/{email}")
    public ResponseEntity<String> approveUser(@PathVariable String email) {
        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일의 사용자를 찾을 수 없습니다."));

        if (user.getApprovalStatus() != ApprovalStatus.PENDING) {
            return ResponseEntity.badRequest().body("이미 승인된 사용자이거나 승인할 수 없는 상태입니다.");
        }

        user.setApprovalStatus(ApprovalStatus.APPROVED);
        userRepository.save(user);

        return ResponseEntity.ok("회원가입이 승인되었습니다: " + email);
    }
}