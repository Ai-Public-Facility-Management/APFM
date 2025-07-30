package server.config;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import server.domain.JwtUtil;
import server.service.CustomUserDetailsService;
import server.service.TokenBlacklistService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // .requestMatchers(
                        //         "/h2-console/**", "/api/auth/**",
                        //         "/css/**", "/js/**", "/images/**", "/webjars/**", "/api/admin/**"
                        // ).permitAll()
                        // .anyRequest().authenticated() 
                        .anyRequest() .permitAll()  // postman 테스트 할 때는 이 부분만 남기고 위는 주석처리. 끝나면 이 부분 주석하고 윗부분 주석해제.
                )
                .headers(headers -> headers
                        .frameOptions(frame -> frame.sameOrigin())  // H2 콘솔 iframe 허용
                )
                .formLogin(login -> login.disable()) // 🔒 Postman 무한 리다이렉트 방지
                .logout(logout -> logout
                        .logoutUrl("/api/auth/logout")
                        .logoutSuccessHandler((request, response, authentication) -> {
                            response.setContentType("text/plain;charset=UTF-8");
                            response.setStatus(HttpServletResponse.SC_OK);
                            response.getWriter().write("로그아웃 성공");
                        })
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(JwtUtil jwtUtil,
                                                           CustomUserDetailsService customUserDetailsService,
                                                           TokenBlacklistService tokenBlacklistService) {
        return new JwtAuthenticationFilter(jwtUtil, customUserDetailsService, tokenBlacklistService);
    }
}