Feature: Authentication

  Scenario: Register a new customer
    Given I provide registration details with email "new.customer@example.com" and password "Password00$"
    When I send a POST request to "/auth/register"
    Then I should receive a 201 status code
    And the response should confirm the customer is registered

  Scenario: Login with valid credentials
    Given I provide login details with email "new.customer@example.com" and password "Password00$"
    When I send a POST request to "/auth/login"
    Then I should receive a 200 status code
    And the response should contain a valid JWT token

  Scenario: Register an existing customer
    Given I provide registration details with email "existing.customer@example.com" and password "Password22&"
    When I send a POST request to "/auth/register"
    Then I should receive a 409 status code
    And the response should indicate the customer already exists

  Scenario: Login with invalid credentials
    Given I provide login details with email "invalid.customer@example.com" and password "wrongpassword"
    When I send a POST request to "/auth/login"
    Then I should receive a 401 status code
    And the response should indicate invalid credentials