// [기능 요약] SecurityContext에서 현재 사용자 이메일을 얻고 권한(작성자/관리자) 확인
package server.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUserResolver {

    public String currentUserEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) throw new IllegalStateException("인증 정보가 없습니다.");
        return auth.getName(); // principal = email (프로젝트 정책)
    }

    public void ensureOwnerOrAdmin(String ownerEmail) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) throw new IllegalStateException("인증 정보가 없습니다.");
        boolean isAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (isAdmin) return;
        if (!auth.getName().equals(ownerEmail)) throw new SecurityException("권한이 없습니다.");
    }
}
