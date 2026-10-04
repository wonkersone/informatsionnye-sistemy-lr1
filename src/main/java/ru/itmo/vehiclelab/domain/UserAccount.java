package ru.itmo.vehiclelab.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.Check;

@Entity
@Table(name = "app_users")
@Check(constraints = "char_length(username) between 3 and 32 and char_length(password_hash) = 60")
public class UserAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Pattern(regexp = "[a-z0-9_]{3,32}")
    @Column(nullable = false, unique = true, length = 32)
    private String username;

    @NotBlank
    @Size(min = 60, max = 60)
    @Column(name = "password_hash", nullable = false, length = 60)
    private String passwordHash;

    protected UserAccount() {}

    public UserAccount(String username, String passwordHash) {
        this.username = username;
        this.passwordHash = passwordHash;
    }

    public String getUsername() { return username; }
    public String getPasswordHash() { return passwordHash; }
}
