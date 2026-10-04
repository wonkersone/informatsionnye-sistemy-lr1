package ru.itmo.vehiclelab.service;

import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.itmo.vehiclelab.domain.UserAccount;
import ru.itmo.vehiclelab.repository.UserAccountRepository;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

@Service
public class UserService implements UserDetailsService {
    private final UserAccountRepository users;
    private final PasswordEncoder passwords;

    public UserService(UserAccountRepository users, PasswordEncoder passwords) {
        this.users = users;
        this.passwords = passwords;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) {
        UserAccount account = users.findByUsername(normalize(username))
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));
        return User.withUsername(account.getUsername()).password(account.getPasswordHash()).roles("USER").build();
    }

    @Transactional
    public void register(String username, String password) {
        String login = normalize(username);
        if (!login.matches("[a-z0-9_]{3,32}")) {
            throw new IllegalArgumentException("Логин: от 3 до 32 латинских букв, цифр или знаков _");
        }
        if (password == null || password.length() < 8 || password.getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new IllegalArgumentException("Пароль: от 8 символов, не более 72 байт");
        }
        if (users.existsByUsername(login)) {
            throw new IllegalArgumentException("Этот логин уже занят");
        }
        users.saveAndFlush(new UserAccount(login, passwords.encode(password)));
    }

    @Transactional
    public void ensureUser(String username, String password) {
        if (!users.existsByUsername(normalize(username))) register(username, password);
    }

    private String normalize(String username) {
        return username == null ? "" : username.trim().toLowerCase(Locale.ROOT);
    }
}
