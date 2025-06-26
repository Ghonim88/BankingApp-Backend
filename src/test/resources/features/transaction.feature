Feature: Transfer Funds

  Scenario: Successful transfer between two customer accounts
    Given I am logged in as an employee for transfer
    And two customers with verified accounts exist
    And I get the IBAN of customer 2's CHECKING account
    And I get the IBAN of customer 3's CHECKING account
    When I transfer 10.00 from the CHECKING to the CHECKING account
    Then the transfer response should have status code 201
    And the response should have status "success" and message "Transfer completed."

  Scenario: Transfer fails due to insufficient funds
    Given I am logged in as an employee for transfer
    And a customer with low balance exists
    And I get the IBAN of that customer's CHECKING account
    And I get the IBAN of customer 3's CHECKING account
    When I attempt to transfer 1000.00 from the CHECKING to the CHECKING account
    Then the transfer response should have status code 400
    And the response should have status "error" and message "Transfer failed: Insufficient funds for this transaction."

  Scenario: Transfer fails due to negative amount
    Given I am logged in as an employee for transfer
    And I get the IBAN of customer 2's CHECKING account
    And I get the IBAN of customer 3's CHECKING account
    When I attempt to transfer -50.00 from the CHECKING to the CHECKING account
    Then the transfer response should have status code 400
    And the response should have status "error" and message "Transfer failed: Amount must be provided and greater than zero."

  Scenario: View transaction history for a customer
    Given I am logged in as an employee for transfer
    And I get the transaction history for customer 2
    Then the transaction history response should contain at least 1 transaction
