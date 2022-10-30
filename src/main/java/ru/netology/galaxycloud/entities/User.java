package ru.netology.galaxycloud.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "users")
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String login;

    private String password;

    private LocalDateTime created;

    private LocalDateTime updated;

    @ElementCollection
    private Set<UserRole> roles;

    @OneToMany(mappedBy = "user",
            cascade = CascadeType.ALL)
    private List<File> fileList;

}
