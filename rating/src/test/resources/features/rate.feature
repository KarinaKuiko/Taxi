Feature: Rate API

  Scenario: Create rate
    Given request body to create or update rate
      """
        {
          "rideId": 1,
          "comment": "comment",
          "rating": 5,
          "userId": 1,
          "userType": "DRIVER"
        }
      """
    When create rate
    Then response status is 201
    And response body contain rate data
      """
        {
          "id": 2,
          "rideId": 1,
          "comment": "comment",
          "rating": 5,
          "userId": 1,
          "userType": "DRIVER"
        }
      """

  Scenario: Get passenger rate by id
    When get passenger rate with id 1
    Then response status is 200
    And response body contain rate data
      """
        {
          "id": 1,
          "rideId": 1,
          "comment": "Good",
          "rating": 4,
          "userId": 1,
          "userType": "DRIVER"
        }
      """

  Scenario: Get driver rate by id
    When get driver rate with id 1
    Then response status is 200
    And response body contain rate data
      """
        {
          "id": 1,
          "rideId": 1,
          "comment": "Good",
          "rating": 4,
          "userId": 1,
          "userType": "PASSENGER"
        }
      """

  Scenario: Update rate
    Given request body to create or update rate
      """
        {
          "rideId": 1,
          "comment": "Great",
          "rating": 5,
          "userId": 1,
          "userType": "DRIVER"
        }
      """
    When update rate with id 2
    Then response status is 200
    And response body contain rate data
      """
        {
          "id": 2,
          "rideId": 1,
          "comment": "Great",
          "rating": 5,
          "userId": 1,
          "userType": "DRIVER"
        }
      """