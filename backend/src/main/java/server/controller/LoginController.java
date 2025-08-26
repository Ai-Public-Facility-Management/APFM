package server.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import server.domain.JwtUtil;
import server.dto.LoginRequestDTO;
import org.springframework.http.ResponseEntity;
import server.service.LoginService;
import server.service.TokenBlacklistService;
import server.domain.UserType;
import server.domain.Users;
import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class LoginController {

    private final LoginService authService;
    private final JwtUtil jwtUtil;
    private final TokenBlacklistService tokenBlacklistService;

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequestDTO request) {
        String token = authService.login(request);
        Users user = authService.getUserByEmail(request.getEmail());

        String message = user.getType() == UserType.ADMIN ? "관리자로 로그인되었습니다." : "로그인 성공.";
        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        response.put("userType", user.getType().name());
        response.put("message", message);
        response.put("username", user.getUsername());

        return ResponseEntity.ok(response);
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



