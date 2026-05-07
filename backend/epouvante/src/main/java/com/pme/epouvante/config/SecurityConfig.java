package com.pme.epouvante.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Value("${app.security.user1.password}")
    private String user1PasswordHash;

    @Value("${app.security.user2.password}")
    private String user2PasswordHash;

    @Value("${app.security.admin.password}")
    private String adminPasswordHash;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> auth

                        // Pages publiques
                        .requestMatchers(
                                "/",
                                "/login",
                                "/css/**",
                                "/images/**",
                                "/uploads/**",
                                "/web/**"
                        ).permitAll()

                        // Lecture publique des annonces
                        .requestMatchers(HttpMethod.GET, "/annonces/**")
                        .permitAll()

                        // Tout le reste nécessite une connexion
                        .anyRequest()
                        .authenticated()
                )

                .formLogin(form -> form
                        .defaultSuccessUrl("/web/annonces", true)
                        .permitAll()
                )

                .logout(logout -> logout
                        .logoutSuccessUrl("/")
                        .permitAll()
                )

                .httpBasic(httpBasic -> {});

        return http.build();
    }

    @Bean
    public InMemoryUserDetailsManager users() {

        UserDetails user1 = User.builder()
                .username("user1")
                .password(user1PasswordHash)
                .roles("USER")
                .build();

        UserDetails user2 = User.builder()
                .username("user2")
                .password(user2PasswordHash)
                .roles("USER")
                .build();

        UserDetails admin = User.builder()
                .username("admin")
                .password(adminPasswordHash)
                .roles("ADMIN")
                .build();

        return new InMemoryUserDetailsManager(
                user1,
                user2,
                admin
        );
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}