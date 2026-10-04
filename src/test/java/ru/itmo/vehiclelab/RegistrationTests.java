package ru.itmo.vehiclelab;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.itmo.vehiclelab.repository.UserAccountRepository;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class RegistrationTests {
    @Autowired MockMvc mvc;
    @Autowired UserAccountRepository users;
    @Autowired PasswordEncoder passwords;

    @Test
    void registersAndAuthenticatesWithStoredHash() throws Exception {
        users.findByUsername("new_student").ifPresent(users::delete);
        mvc.perform(get("/register")).andExpect(status().isOk());
        mvc.perform(post("/register").with(csrf()).param("username", "New_Student")
                .param("password", "password123").param("confirmation", "password123"))
                .andExpect(redirectedUrl("/login?registered"));
        var account = users.findByUsername("new_student").orElseThrow();
        assertThat(account.getPasswordHash()).isNotEqualTo("password123");
        assertThat(passwords.matches("password123", account.getPasswordHash())).isTrue();
        mvc.perform(post("/login").with(csrf()).param("username", "NEW_STUDENT").param("password", "password123"))
                .andExpect(redirectedUrl("/vehicles"));
        mvc.perform(post("/register").with(csrf()).param("username", "NEW_STUDENT")
                .param("password", "password123").param("confirmation", "password123"))
                .andExpect(view().name("register")).andExpect(model().attributeHasFieldErrors("registrationForm", "username"));
        users.delete(account);
    }

    @Test
    void rejectsBadFieldsAndMissingCsrf() throws Exception {
        mvc.perform(post("/register").param("username", "valid_user").param("password", "password123")
                .param("confirmation", "password123")).andExpect(status().isForbidden());
        mvc.perform(post("/register").with(csrf()).param("username", "я")
                .param("password", "123").param("confirmation", "456"))
                .andExpect(view().name("register"))
                .andExpect(model().attributeHasFieldErrors("registrationForm", "username", "password", "passwordsMatch"));
        String longPassword = "я".repeat(40);
        mvc.perform(post("/register").with(csrf()).param("username", "valid_user")
                .param("password", longPassword).param("confirmation", longPassword))
                .andExpect(model().attributeHasFieldErrors("registrationForm", "passwordLengthValid"));
    }
}
