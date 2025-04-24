package com.inholland.bank.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity

public class SecurityConfig {
  
  //  private JwtAuthenticationFilter jwtFilter;

  // public SecurityConfig(JwtAuthenticationFilter jwtFilter) {
  //   this.jwtFilter = jwtFilter;
  // }


  // @Bean
  // public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
  //   httpSecurity.csrf(csrf -> csrf.disable());
  //   httpSecurity.sessionManagement(session ->
  //       session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

    // httpSecurity.authorizeHttpRequests(
    //     requests ->
    //         requests.requestMatchers("/login").permitAll()); //TODO: change

    // httpSecurity.authorizeHttpRequests(
    //     requests ->
    //         requests.requestMatchers("/users").permitAll()); //TODO: change
    // httpSecurity.authorizeHttpRequests(
    //     requests ->
    //         requests.requestMatchers("/cars").permitAll());//TODO: change

    // httpSecurity.authorizeHttpRequests(
    //     requests ->
    //         requests.requestMatchers("/h2-console").permitAll());//TODO: change
    // httpSecurity.authorizeHttpRequests(
    //     requests ->
    //         requests.requestMatchers("/brands").authenticated());//TODO: change

  //   httpSecurity.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
  //   return httpSecurity.build();
  // }


  @Bean
  public BCryptPasswordEncoder bCryptPasswordEncoder() {
    return new BCryptPasswordEncoder(12);
  }

   


}
