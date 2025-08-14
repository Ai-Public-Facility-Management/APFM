package server.domain;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Users")
@Data
@NoArgsConstructor
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
    private UserType type = UserType.INSPECTOR;

    private Long level;

    public Users(String email,String paasword){
        this.email = email;
        this.password = paasword;
        this.username = "Admin";
        this.department = Department.DEVELOPMENT;
        this.approvalStatus = ApprovalStatus.APPROVED;
        this.type = UserType.ADMIN;
        this.level = 0L;
    }
}

