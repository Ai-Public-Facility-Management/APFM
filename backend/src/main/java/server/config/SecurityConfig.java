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
            .csrf().disable()
            .cors().disable()
        //     .formLogin().disable() // 기본 HTML 로그인 폼 비활성화
            .httpBasic().disable()
            .authorizeRequests()
                // .antMatchers(
                //     "/h2-console/**", "/api/auth/**","/api/users/check-email", "/api/admin/**",
                //     "/css/**", "/js/**", "/images/**", "/webjars/**", "/api/issues/**")
                // .permitAll()
                // .anyRequest().authenticated()
                .anyRequest().permitAll()
            .and()
            .headers().frameOptions().sameOrigin();
        //     .and()
        //     .formLogin()
        //     .and()
        //     .logout()
        //         .logoutUrl("/api/auth/logout")
        //         .logoutSuccessUrl("/login")
        //         .invalidateHttpSession(true)
        //         .deleteCookies("JSESSIONID");
        return http.build();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(JwtUtil jwtUtil,
                                                           CustomUserDetailsService customUserDetailsService,
                                                           TokenBlacklistService tokenBlacklistService) {
        return new JwtAuthenticationFilter(jwtUtil, customUserDetailsService, tokenBlacklistService);
    }
}