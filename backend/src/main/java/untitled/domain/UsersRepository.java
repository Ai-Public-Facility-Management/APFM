package untitled.domain;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import untitled.domain.*;

//<<< PoEAA / Repository
@RepositoryRestResource(collectionResourceRel = "users", path = "users")
public interface UsersRepository
    extends PagingAndSortingRepository<Users, String> {
    boolean existsByEmail(String email);
    Optional<Users> findByEmail(String email);
}
