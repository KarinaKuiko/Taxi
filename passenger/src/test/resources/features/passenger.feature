Feature: Passenger API

  Scenario: Create passenger
    Given request body to create or update passenger
      """
        {
          "name": "Test",
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
          "name": "Test",
          "email": "test@gmail.com",
          "phone": "+375441234567",
          "rating": 5.0
        }
      """

  Scenario: Get passenger by id
    When get passenger with id 2
    Then response status is 200
    And response body contain passenger data
      """
        {
          "id": 2,
          "name": "Test",
          "email": "test@gmail.com",
          "phone": "+375441234567",
          "rating": 5.0
        }
      """

  Scenario: Update passenger
    Given request body to create or update passenger
      """
        {
          "name": "Passenger",
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
          "name": "Passenger",
          "email": "test@gmail.com",
          "phone": "80441234567",
          "rating": 5.0
        }
      """

  Scenario: Delete passenger
    When delete passenger with id 2
    Then response status is 204