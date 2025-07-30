package server.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import server.domain.Users;
import server.repository.UsersRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UsersService {

    private final UsersRepository usersRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UsersService(UsersRepository usersRepository, BCryptPasswordEncoder passwordEncoder) {
        this.usersRepository = usersRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Users createUser(Users user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return usersRepository.save(user);
    }

    public Optional<Users> getUser(String email) {
        return usersRepository.findById(email);
    }

    public List<Users> getAllUsers() {
        return (List<Users>) usersRepository.findAll();
    }

    public void deleteUser(String email) {
        usersRepository.deleteById(email);
    }

    public boolean isEmailDuplicated(String email) {
        return usersRepository.existsByEmail(email);
    }
}
