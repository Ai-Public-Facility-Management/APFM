package untitled.infra;


import untitled.infra.ApprovalStatus;
import untitled.infra.Users;
import untitled.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class UserAdminController {

    private final UserRepository userRepository;

    @PostMapping("/approve/{email}")
    public ResponseEntity<String> approveUser(@PathVariable String email) {
        Users user = userRepository.findById(email)
                .orElseThrow(() -> new RuntimeException("사용자 없음"));

        user.setApprovalStatus(ApprovalStatus.APPROVED);
        userRepository.save(user);

        return ResponseEntity.ok("사용자 승인 완료");
    }
}
