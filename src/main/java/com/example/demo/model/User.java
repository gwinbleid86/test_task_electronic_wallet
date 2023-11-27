package com.example.demo.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Collection;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_table")
public class User {
    @Id
    private String email;

    private String username;

    private String password;

    private boolean enabled;

    private Double balance;

    @ManyToMany(fetch = FetchType.EAGER, mappedBy = "users", cascade = CascadeType.ALL)
    private Collection<Role> roles;

    @OneToMany(mappedBy = "user", cascade = CascadeType.PERSIST)
    private Collection<Secret> secrets;

    public void addRole(Role role) {
        this.roles.add(role);
        role.getUsers().add(this);
    }

    public void removeRole(long roleId) {
        Role role = this.roles.stream().filter(t -> t.getId() == roleId).findFirst().orElse(null);
        if (role != null) {
            this.roles.remove(role);
            role.getUsers().remove(this);
        }
    }
}
