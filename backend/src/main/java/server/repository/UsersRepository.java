package server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import server.domain.Users;
import server.repository.UsersRepository;
import server.domain.ApprovalStatus;

import java.util.Optional;
import java.util.List;


@Repository
public interface UsersRepository extends JpaRepository<Users, String> {
    boolean existsByEmail(String email);

    Optional<Users> findByEmail(String email);

    // 🔹 점검 대상 사용자만 조회 (예: 승인된 사용자만)
    List<Users> findAllByApprovalStatus(ApprovalStatus status);
}
