package com.example.project_module6.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String username;
    private String password;
    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;
    private String name;
    private String email;
    private boolean status = true;
    @Column(name = "created_at")
    private LocalDate createAt;
    @Column(name = "soft_delete")
    private boolean softDelete=false;

    public enum Role {
        ADMIN,
        USER
    }


}
