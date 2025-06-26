Feature: Account Creation

  Scenario: Valid account creation with CHECKING and SAVINGS
    Given I am logged in as an employee
    And I prepare two account DTOs for customer with ID 10: one CHECKING and one SAVINGS
    When I send a POST request to create accounts at "/accounts"
    Then the account creation response should have status code 201
    And the response should include both account types

  Scenario: Invalid account creation with only one account
    Given I am logged in as an employee
    And I prepare one CHECKING account DTO for customer with ID 10
    When I send a POST request to create accounts at "/accounts"
    Then the account creation response should have status code 400
    And the response should indicate invalid account creation

  Scenario: Invalid account creation for different customers
    Given I am logged in as an employee
    And I prepare two account DTOs for different customer IDs
    When I send a POST request to create accounts at "/accounts"
    Then the account creation response should have status code 400
    And the response should indicate invalid account creation
