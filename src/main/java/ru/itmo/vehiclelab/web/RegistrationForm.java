package ru.itmo.vehiclelab.web;

import jakarta.validation.constraints.*;
import java.nio.charset.StandardCharsets;

public class RegistrationForm {
    @NotBlank(message = "Введите логин")
    @Pattern(regexp = "[A-Za-z0-9_]{3,32}", message = "Логин: от 3 до 32 латинских букв, цифр или знаков _")
    private String username;

    @NotBlank(message = "Введите пароль")
    @Size(min = 8, max = 72, message = "Пароль должен содержать от 8 до 72 символов")
    private String password;

    @NotBlank(message = "Повторите пароль")
    private String confirmation;

    @AssertTrue(message = "Пароли не совпадают")
    public boolean isPasswordsMatch() { return password == null || password.equals(confirmation); }

    @AssertTrue(message = "Пароль слишком длинный: допускается не более 72 байт")
    public boolean isPasswordLengthValid() {
        return password == null || password.getBytes(StandardCharsets.UTF_8).length <= 72;
    }

    public String getUsername() { return username; }
    public void setUsername(String value) { username = value; }
    public String getPassword() { return password; }
    public void setPassword(String value) { password = value; }
    public String getConfirmation() { return confirmation; }
    public void setConfirmation(String value) { confirmation = value; }
}
