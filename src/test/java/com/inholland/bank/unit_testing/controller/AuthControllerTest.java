package com.inholland.bank.unit_testing.controller;
import com.inholland.bank.controller.AuthController;
import com.inholland.bank.exceptions.BsnAlreadyExistsException;
import com.inholland.bank.exceptions.EmailAlreadyExistsException;
import com.inholland.bank.exceptions.PhoneAlreadyExistsException;
import com.inholland.bank.model.Customer;
import com.inholland.bank.model.User;
import com.inholland.bank.model.dto.CustomerDTO;
import com.inholland.bank.model.dto.LoginDTO;
import com.inholland.bank.security.CustomUserDetails;
import com.inholland.bank.security.JwtTokenUtil;
import com.inholland.bank.service.CustomerService;
import com.inholland.bank.service.UserService;
import com.inholland.bank.unit_testing.TestSecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;


@SpringJUnitWebConfig
@WebMvcTest(AuthController.class)
@Import(TestSecurityConfig.class) // Import the TestSecurityConfig
@ActiveProfiles("test") // Use the test profile
class AuthControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private JwtTokenUtil jwtTokenUtil;

  @MockBean
  private UserService userService;

  @MockBean
  private CustomerService customerService;

  @MockBean
  private AuthenticationManager authenticationManager;

  @Test
  void authenticateUser_ShouldReturnToken_WhenCredentialsAreValid() throws Exception {
    //setup
    LoginDTO loginDTO = new LoginDTO();
    loginDTO.setEmail("test@example.com");
    loginDTO.setPassword("password123");
    Authentication authentication = mock(Authentication.class);
    String token = "mocked-jwt-token";

    // Mocking the behavior of authenticationManager and jwtTokenUtil
    when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
        .thenReturn(authentication);
    when(jwtTokenUtil.generateToken(authentication)).thenReturn(token);

    // Mocking the behavior of userService
    mockMvc.perform(post("/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"email\":\"test@example.com\",\"password\":\"password123\"}"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.token").value(token));
  }

  @Test
  void authenticateUser_ShouldReturnBadRequest_WhenCredentialsAreMissing() throws Exception {
    mockMvc.perform(post("/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"email\":\"\",\"password\":\"\"}"))
        .andDo(print())
        .andExpect(status().isBadRequest());
  }

  @Test
  void authenticateUser_ShouldReturnUnauthorized_WhenCredentialsAreInvalid() throws Exception {
    when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
        .thenThrow(new BadCredentialsException("Invalid credentials"));

    mockMvc.perform(post("/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"email\":\"test@example.com\",\"password\":\"wrongpassword\"}"))
        .andDo(print())
        .andExpect(status().isUnauthorized())
        .andExpect(content().string("Invalid email or password"));
  }
  @Test
  void registerCustomer_ShouldReturnCreated_WhenRegistrationIsSuccessful() throws Exception {
    // Arrange - create DTO and expected Customer object
    CustomerDTO customerDTO = new CustomerDTO();
    customerDTO.setFirstName("John");
    customerDTO.setLastName("Doe");
    customerDTO.setEmail("john.doe@example.com");
    customerDTO.setPassword("Password123!");
    customerDTO.setBsn("123456789");
    customerDTO.setPhoneNumber("+31612345678");

    Customer customer = new Customer();
    customer.setUserId(1L);
    customer.setFirstName("John");
    customer.setLastName("Doe");
    customer.setEmail("john.doe@example.com");
    customer.setBsn("123456789");
    customer.setPhoneNumber("+31612345678");

    // Mock service behavior
    when(customerService.registerNewCustomer(any(CustomerDTO.class))).thenReturn(customer);

    // Act & Assert - simulate POST request
    mockMvc.perform(post("/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "firstName": "John",
                  "lastName": "Doe",
                  "email": "john.doe@example.com",
                  "password": "Password123!",
                  "bsn": "123456789",
                  "phoneNumber": "+31612345678"
                }
            """))
        .andDo(print())
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.firstName").value("John"))
        .andExpect(jsonPath("$.lastName").value("Doe"))
        .andExpect(jsonPath("$.email").value("john.doe@example.com"))
        .andExpect(jsonPath("$.bsn").value("123456789"))
        .andExpect(jsonPath("$.phoneNumber").value("+31612345678"));
  }


  @Test
  void registerCustomer_ShouldReturnConflict_WhenEmailAlreadyExists() throws Exception {
    when(customerService.registerNewCustomer(any(CustomerDTO.class)))
        .thenThrow(new EmailAlreadyExistsException("Email already exists"));

    mockMvc.perform(post("/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"firstName\":\"John\",\"lastName\":\"Doe\",\"email\":\"john.doe@example.com\",\"password\":\"password123\",\"phoneNumber\":\"+31612345678\",\"bsn\":\"123456789\"}"))
        .andDo(print())
        .andExpect(status().isConflict())
        .andExpect(content().string("The email address 'Email already exists' is already registered in the system. Please use a different email address."));
  }

  @Test
  void registerCustomer_ShouldReturnConflict_WhenBsnAlreadyExists() throws Exception {
    when(customerService.registerNewCustomer(any(CustomerDTO.class)))
        .thenThrow(new BsnAlreadyExistsException("BSN already exists"));

    mockMvc.perform(post("/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"firstName\":\"John\",\"lastName\":\"Doe\",\"email\":\"john.doe@example.com\",\"password\":\"password123\",\"phoneNumber\":\"+31612345678\",\"bsn\":\"123456789\"}"))
        .andDo(print())
        .andExpect(status().isConflict())
        .andExpect(content().string("The BSN 'BSN already exists' is already registered. Please use a different BSN."));
  }

  @Test
  void registerCustomer_ShouldReturnConflict_WhenPhoneAlreadyExists() throws Exception {
    when(customerService.registerNewCustomer(any(CustomerDTO.class)))
        .thenThrow(new PhoneAlreadyExistsException("Phone number already exists"));

    mockMvc.perform(post("/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"firstName\":\"John\",\"lastName\":\"Doe\",\"email\":\"john.doe@example.com\",\"password\":\"password123\",\"phoneNumber\":\"+31612345678\",\"bsn\":\"123456789\"}"))
        .andDo(print())
        .andExpect(status().isConflict())
        .andExpect(content().string("The phone number 'Phone number already exists' is already registered. Please use a different phone number."));
  }

  @Test
  void getLoggedIn_ShouldReturnUserData_WhenAuthenticated() throws Exception {
    User user = new User();
    user.setEmail("john.doe@example.com");
    user.setPassword("password123");

    // Mock authentication and security context
    Authentication authentication = mock(Authentication.class);
    when(authentication.getPrincipal()).thenReturn(new CustomUserDetails(user));
    SecurityContext securityContext = mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);

    when(userService.getLoggedInUser(any(User.class))).thenReturn(user);

    mockMvc.perform(get("/auth/me")
            .header("Authorization", "Bearer mocked-jwt-token"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.email").value("john.doe@example.com"));
  }

  @Test
  void getLoggedIn_ShouldReturnUnauthorized_WhenAuthenticationIsMissing() throws Exception {
    mockMvc.perform(get("/auth/me"))
        .andDo(print())
        .andExpect(status().isUnauthorized())
        .andExpect(content().string("Authentication is missingnull"));
  }
}
