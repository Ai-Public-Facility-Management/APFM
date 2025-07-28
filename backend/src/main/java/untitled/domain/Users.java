package untitled.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.persistence.*;
import lombok.Data;
import untitled.BackendApplication;
import untitled.domain.UserType;

@Entity
@Table(name = "Users_table")
@Data
//<<< DDD / Aggregate Root
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    private String password;

    private UserType type;

    private Long level;

    public static UsersRepository repository() {
        UsersRepository usersRepository = BackendApplication.applicationContext.getBean(
            UsersRepository.class
        );
        return usersRepository;
    }
}
//>>> DDD / Aggregate Root
