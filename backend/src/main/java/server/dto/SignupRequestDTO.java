package server.dto;


import server.domain.Department;
import lombok.Data;

@Data
public class SignupRequestDTO {
    private String email;
    private String password;
    private String username;
    private Department department;
}
