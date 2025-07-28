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

@Entity
@Table(name = "Users_table")
@Data
//<<< DDD / Aggregate Root
public class Users {

    @Id
    private String email;

    private String password;

    private String username;

    @Enumerated(EnumType.STRING)
    private Department department;

    @Enumerated(EnumType.STRING)
    private ApprovalStatus approvalStatus = ApprovalStatus.PENDING; // 기본값: 대기중

    private type type;

    private Long level;

    public static UsersRepository repository() {
        UsersRepository usersRepository = BackendApplication.applicationContext.getBean(
            UsersRepository.class
        );
        return usersRepository;
    }
}
//>>> DDD / Aggregate Root
