package com.example.trancs.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Отключить защиту CSRF для упрощения работы с API
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers("/static/**", "/index.html").permitAll()  // Разрешить доступ к публичным ресурсам, включая index.html
                        .anyRequest().authenticated()               // Все остальные запросы требуют аутентификации
                )
                .formLogin(form -> form.permitAll())           // Разрешить доступ к форме входа
                .logout(logout -> logout.permitAll());         // Разрешить всем пользователям выходить
        return http.build();
    }
    @Bean
    public UserDetailsService userDetailsService() {
        PasswordEncoder passwordEncoder = passwordEncoder();
        UserDetails user = User.withUsername("Dizastars")
                .password(passwordEncoder.encode("Dizastars"))
                .roles("USER")
                .build();

        return new InMemoryUserDetailsManager(user);
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

