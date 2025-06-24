Feature: Transfer Funds

  Scenario: Successful transfer between two customer accounts
    Given I am logged in as an employee
    And two customers with verified accounts exist
    And I get the IBAN of customer 1's CHECKING account
    And I get the IBAN of customer 2's SAVINGS account
    When I transfer 10.00 from the CHECKING to the SAVINGS account
    Then the transfer response should have status code 201
    And the response should confirm the transfer was completed
