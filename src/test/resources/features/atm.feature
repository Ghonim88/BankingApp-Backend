Feature: ATM Transactions

  Scenario: Deposit funds successfully
    Given an account exists with balance 100.00
    When I deposit 50.00 into the account
    Then the new balance should be 150.00

  Scenario: Withdraw funds successfully
    Given an account exists with balance 100.00
    When I withdraw 30.00 from the account
    Then the new balance should be 70.00

  Scenario: Attempt to withdraw more than allowed overdraft
    Given an account exists with balance 100.00
    When I withdraw 500.00 from the account
    Then I should receive an insufficient funds error
