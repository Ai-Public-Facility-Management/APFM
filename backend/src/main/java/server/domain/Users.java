package server.domain;

import javax.persistence.*;
import lombok.Data;
import server.BackendApplication;


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

    @Enumerated(EnumType.STRING)
    private UserType type;

    private Long level;

    // public static UsersRepository repository() {
    //     UsersRepository usersRepository = BackendApplication.applicationContext.getBean(
    //         UsersRepository.class
    //     );
    //     return usersRepository;
    // }
}
//>>> DDD / Aggregate Root
