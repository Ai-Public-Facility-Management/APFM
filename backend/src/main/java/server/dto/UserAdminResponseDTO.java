package server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import server.domain.Users;

@Data
@AllArgsConstructor
public class UserAdminResponseDTO {
    private String email;
    private String username;
    private String department;

    public UserAdminResponseDTO(Users user) {
        this.email = user.getEmail();
        this.username = user.getUsername();
        this.department = user.getDepartment().toString();
    }
}
