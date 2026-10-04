package ru.itmo.vehiclelab.security;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
@Configuration
public class SecurityConfig {
    @Bean
    SecurityFilterChain security(HttpSecurity http) throws Exception {
        return http.authorizeHttpRequests(a -> a.requestMatchers("/login","/css/**","/fonts/**","/js/**","/favicon.svg","/error").permitAll().anyRequest().authenticated())
            .headers(h -> h.frameOptions(f -> f.sameOrigin()))
            .formLogin(f -> f.loginPage("/login").defaultSuccessUrl("/vehicles",true).permitAll())
            .logout(l -> l.logoutSuccessUrl("/login?logout").permitAll())
            .build();
    }
    @Bean
    PasswordEncoder passwords() { return new BCryptPasswordEncoder(); }
    @Bean
    UserDetailsService users(PasswordEncoder encoder, Environment env) {
        return new InMemoryUserDetailsManager(
            User.withUsername(env.getProperty("LAB_USER","student")).password(encoder.encode(env.getProperty("LAB_PASSWORD","student123"))).roles("USER").build(),
            User.withUsername(env.getProperty("LAB_SECOND_USER","teacher")).password(encoder.encode(env.getProperty("LAB_SECOND_PASSWORD","teacher123"))).roles("USER").build());
    }
}
