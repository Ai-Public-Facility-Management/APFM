package server.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import server.domain.JwtUtil;
import server.dto.LoginRequestDTO;
import org.springframework.http.ResponseEntity;
import server.service.LoginService;
import server.service.TokenBlacklistService;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class LoginController {

    private final LoginService authService;
    private final JwtUtil jwtUtil;
    private final TokenBlacklistService tokenBlacklistService;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequestDTO request) {
        String token = authService.login(request);  // 토큰 반환받기
        return ResponseEntity.ok("로그인 성공: " + token);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String authHeader) {
        String token = jwtUtil.resolveToken(authHeader);

        if (token != null && jwtUtil.validateToken(token)) {
            long remaining = jwtUtil.getRemainingExpiration(token);
            tokenBlacklistService.blacklistToken(token, remaining);
            return ResponseEntity.ok("로그아웃 성공 (토큰 블랙리스트 등록)");
        }

        return ResponseEntity.badRequest().body("유효하지 않은 토큰");
    }


}



