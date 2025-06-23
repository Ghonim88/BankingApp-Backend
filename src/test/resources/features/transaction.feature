Feature: Transfer Funds Between Customers

  Scenario: Successful transfer between two verified customers
    Given customer A and customer B exist with CHECKING accounts
    When I transfer 20.00 from customer A to customer B
    Then the transfer should succeed with status code 201
