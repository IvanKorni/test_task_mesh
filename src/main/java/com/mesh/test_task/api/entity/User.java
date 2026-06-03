package com.mesh.test_task.api.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", length = 500)
    private String name;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "password", length = 500)
    private String password;

    @OneToMany(mappedBy = "user")
    private Set<EmailData> emails = new LinkedHashSet<>();

    @OneToMany(mappedBy = "user")
    private Set<PhoneData> phones = new LinkedHashSet<>();
}
