package Aivle.APFM.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Users {

    @Id
    private String email;

    private String password;

    private String username;

    @Enumerated(EnumType.STRING)
    private Department department;



}
