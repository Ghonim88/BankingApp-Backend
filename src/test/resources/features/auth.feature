Feature: Authentication

  Scenario: Register a new customer
    Given I provide registration details with email "new.customer@example.com", password "Password00$", first name "John", last name "Doe", BSN "123456789", and phone number "+31612345678"
    When I send a POST request to "/auth/register"
    Then I should receive a 201 status code
    And the response should confirm the customer is registered



  Scenario: Login with valid credentials
    Given I provide login details with email "customer2@gmail.com" and password "Password2!"
    When I send a POST request to "/auth/login"
    Then I should receive a 200 status code
    And the response should contain a valid JWT token



  Scenario: Login with invalid credentials
    Given I provide login details with email "invalid.customer@gmail.com" and password "wrongpassword"
    When I send a POST request to "/auth/login"
    Then I should receive a 401 status code
    And the response should indicate invalid credentials