package server.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import server.domain.ApprovalStatus;
import server.domain.Users;


@Repository
public interface UsersRepository extends JpaRepository<Users, String> {
    boolean existsByEmail(String email);

    Optional<Users> findByEmail(String email);

    // 🔹 점검 대상 사용자만 조회 (예: 승인된 사용자만)
    List<Users> findAllByApprovalStatus(ApprovalStatus status);
}
