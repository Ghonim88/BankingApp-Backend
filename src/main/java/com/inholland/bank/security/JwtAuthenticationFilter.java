package com.inholland.bank.security;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  @Autowired
  private JwtTokenUtil jwtTokenUtil;

  @Autowired
  @Lazy // Lazy loading to avoid circular dependency issues
  private UserDetailsService userDetailsService;

  //intercepting every request and checks if a valid JWT is present.
  @Override
  protected void doFilterInternal(HttpServletRequest request,
                                  HttpServletResponse response,
                                  FilterChain filterChain) throws ServletException, IOException {

    String header = request.getHeader("Authorization");
    if (header != null && header.startsWith("Bearer ")) {
      String token = header.substring(7);

      String username = jwtTokenUtil.getUserNameFromJwtToken(token);

      if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);


        if (jwtTokenUtil.validateJwtToken(token)) {
          UsernamePasswordAuthenticationToken authentication =
              new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

          authentication.setDetails(
              new WebAuthenticationDetailsSource().buildDetails(request)
          );

          SecurityContextHolder.getContext().setAuthentication(authentication);
          System.out.println("Authentication set in SecurityContextHolder"); // Debugging
        } else {
          System.out.println("Invalid JWT Token"); // Debugging
        }
      }
    }
    filterChain.doFilter(request, response);
  }
}