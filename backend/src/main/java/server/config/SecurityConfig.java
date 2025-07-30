package server.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
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
    }

}

