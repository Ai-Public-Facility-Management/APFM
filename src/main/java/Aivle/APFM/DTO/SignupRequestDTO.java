package Aivle.APFM.DTO;

import Aivle.APFM.Entity.Department;
import lombok.Data;

@Data
public class SignupRequestDTO {
    private String email;
    private String password;
    private String username;
    private Department department;
}
