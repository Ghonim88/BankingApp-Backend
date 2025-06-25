Feature: ATM functional tests

  Scenario: Successful deposit and withdrawal
    Given I am logged in as an employee for ATM
    And I select an existing CHECKING account for customer 2
    When I deposit 500.00 into the ATM
    Then the ATM response should show new balance 1000.00

  Scenario: Simple withdrawal
    Given I am logged in as an employee for ATM
    And I select an existing CHECKING account for customer 2
    When I withdraw 100.00 from the ATM
    Then the ATM response should show new balance 900.00

  Scenario: Withdrawal exceeding balance
    Given I am logged in as an employee for ATM
    And I select an existing CHECKING account for customer 2
    When I withdraw 2000.00 from the ATM
    Then the ATM response should contain error message "Insufficient funds"