package server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import server.domain.Users;
import java.util.Optional;


@Repository
public interface UsersRepository extends JpaRepository<Users, String> {
    boolean existsByEmail(String email);
    Optional<Users> findByEmail(String email);
}
