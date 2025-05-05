package com.inholland.bank.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;

@Configuration
public class SecurityConfig {

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
    return config.getAuthenticationManager();
  }
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .cors(cors -> {}) //  ENABLE CORS properly
        .csrf(AbstractHttpConfigurer::disable) // disable csrf if you don't need it
        .authorizeHttpRequests(auth -> auth
        .requestMatchers("/h2-console/**").permitAll() // Allow unrestricted access to H2 console
        .anyRequest().permitAll()
        )
        // Disable CSRF only for the H2 console (alternative to global disable above — useful if you want CSRF elsewhere)
        .csrf(csrf -> csrf
                    .ignoringRequestMatchers("/h2-console/**").disable()
         )
        // Allow H2 console to be displayed in a frame (needed because modern browsers block framing by default)
        .headers(headers -> headers
            .addHeaderWriter(new XFrameOptionsHeaderWriter(
                XFrameOptionsHeaderWriter.XFrameOptionsMode.SAMEORIGIN))
        );

    return http.build();
  }
}