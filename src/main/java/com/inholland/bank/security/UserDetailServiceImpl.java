package com.inholland.bank.security;
import com.inholland.bank.model.User ;
import com.inholland.bank.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailServiceImpl implements UserDetailsService {

  @Autowired
  private UserService userService;  // or any other service to fetch users from DB

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user = userService.findByEmail(username);
    if (user == null) {
      throw new UsernameNotFoundException("User not found with username: " + username);
    }

    //This CustomUserDetails provides Spring Security with user info and authorities
    return new CustomUserDetails(user);
  }
}
