package com.training.thymeleafmid.entities;

import com.training.thymeleafmid.admin.Role;
import jakarta.persistence.*;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;
    private String name;
    private String password;
    private Set<Role> roles = new HashSet<>();
}
