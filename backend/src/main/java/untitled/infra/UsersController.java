package untitled.infra;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;
import untitled.domain.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@Transactional
public class UsersController {

    private final UsersService usersService;

    public UsersController(UsersService usersService) {
        this.usersService = usersService;
    }

    @PostMapping
    public Users create(@RequestBody Users user) {
        return usersService.createUser(user);
    }

    @GetMapping("/{email}")
    public ResponseEntity<Users> getUser(@PathVariable String email) {
        return usersService.getUser(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<Users> list() {
        return usersService.getAllUsers();
    }

    @DeleteMapping("/{email}")
    public void delete(@PathVariable String email) {
        usersService.deleteUser(email);
    }

    @GetMapping("/check-email")
    public ResponseEntity<Boolean> checkEmail(@RequestParam String email) {
        boolean isDuplicated = usersService.isEmailDuplicated(email);
        return ResponseEntity.ok(isDuplicated);
    } //email 중복 확인 api
}
