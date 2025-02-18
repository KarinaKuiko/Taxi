Feature: Passenger API

  Scenario: Create passenger
    Given access token
    And request body to create or update passenger
      """
        {
          "firstName": "Test",
          "lastName": "Test",
          "email": "test@gmail.com",
          "phone": "+375441234567"
        }
      """
    When create passenger
    Then response status is 201
    And response body contain passenger data
      """
        {
          "id": 2,
          "firstName": "Test",
          "lastName": "Test",
          "email": "test@gmail.com",
          "phone": "+375441234567",
          "rating": 5.0
        }
      """

  Scenario: Get passenger by id
    Given access token
    When get passenger with id 2
    Then response status is 200
    And response body contain passenger data
      """
        {
          "id": 2,
          "firstName": "Test",
          "lastName": "Test",
          "email": "test@gmail.com",
          "phone": "+375441234567",
          "rating": 5.0
        }
      """

  Scenario: Update passenger
    Given access token
    And request body to create or update passenger
      """
        {
          "firstName": "passenger",
          "lastName": "Test",
          "email": "test@gmail.com",
          "phone": "80441234567"
        }
      """
    When update passenger with id 2
    Then response status is 200
    And response body contain passenger data
      """
        {
          "id": 2,
          "firstName": "passenger",
          "lastName": "Test",
          "email": "test@gmail.com",
          "phone": "80441234567",
          "rating": 5.0
        }
      """

  Scenario: Delete passenger
    Given access token
    When delete passenger with id 2
    Then response status is 204
