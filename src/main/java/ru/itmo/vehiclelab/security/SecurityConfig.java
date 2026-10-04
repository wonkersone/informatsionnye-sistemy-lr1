package ru.itmo.vehiclelab.security;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
@Configuration
public class SecurityConfig {
    @Bean
    SecurityFilterChain security(HttpSecurity http) throws Exception {
        return http.authorizeHttpRequests(a -> a.requestMatchers("/login","/register","/css/**","/fonts/**","/js/**","/favicon.svg","/error").permitAll().anyRequest().authenticated())
            .headers(h -> h.frameOptions(f -> f.sameOrigin()))
            .formLogin(f -> f.loginPage("/login").defaultSuccessUrl("/vehicles",true).permitAll())
            .logout(l -> l.logoutSuccessUrl("/login?logout").permitAll())
            .build();
    }
    @Bean
    PasswordEncoder passwords() { return new BCryptPasswordEncoder(); }
}
