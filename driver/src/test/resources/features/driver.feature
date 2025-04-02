Feature: Driver API

  Scenario: Create car
    Given access token
    And request body to create or update car
      """
        {
          "color": "red",
          "brand": "BMW",
          "number": "AB321CD",
          "year": 2023
        }
      """
    When create car
    Then response status is 201
    And response body contain car data
      """
        {
          "id": 3,
          "color": "red",
          "brand": "BMW",
          "number": "AB321CD",
          "year": 2023,
          "drivers": []
        }
      """

  Scenario: Get car by id
    Given access token
    When get car with id 3
    Then response status is 200
    And response body contain car data
      """
        {
          "id": 3,
          "color": "red",
          "brand": "BMW",
          "number": "AB321CD",
          "year": 2023,
          "drivers": []
        }
      """

  Scenario: Update car
    Given access token
    And request body to create or update car
      """
        {
          "color": "yellow",
          "brand": "BMW",
          "number": "AB321CD",
          "year": 2014
        }
      """
    When update car with id 3
    Then response status is 200
    And response body contain car data
      """
        {
          "id": 3,
          "color": "yellow",
          "brand": "BMW",
          "number": "AB321CD",
          "year": 2014,
          "drivers": []
        }
      """

  Scenario: Create driver
    Given access token
    And request body to create or update driver
      """
        {
          "firstName": "Driver",
          "lastName": "Driver",
          "email": "driver@gmail.com",
          "phone": "+375331234567",
          "gender": "MALE",
          "carCreateEditDto": {
              "color": "red",
              "brand": "BMW",
              "number": "AB123CD",
              "year": 2023
          }
        }
      """
    When create driver
    Then response status is 201
    And response body contain driver data
      """
        {
          "id": 3,
          "firstName": "Driver",
          "lastName": "Driver",
          "email": "driver@gmail.com",
          "phone": "+375331234567",
          "gender": "MALE",
          "carId": 1,
          "rating": 5.0
        }
      """

  Scenario: Get driver by id
    Given access token
    When get driver with id 3
    Then response status is 200
    And response body contain driver data
      """
        {
          "id": 3,
          "firstName": "Driver",
          "lastName": "Driver",
          "email": "driver@gmail.com",
          "phone": "+375331234567",
          "gender": "MALE",
          "carId": 1,
          "rating": 5.0
        }
      """

  Scenario: Update driver
    Given access token
    And request body to create or update driver
      """
        {
          "firstName": "Driver",
          "lastName": "Driver",
          "email": "test@gmail.com",
          "phone": "+375331234567",
          "gender": "FEMALE",
          "carCreateEditDto": {
              "color": "red",
              "brand": "BMW",
              "number": "AB123CD",
              "year": 2023
          }
        }
      """
    When update driver with id 1
    Then response status is 200
    And response body contain driver data
      """
        {
          "id": 1,
          "firstName": "Driver",
          "lastName": "Driver",
          "email": "test@gmail.com",
          "phone": "+375331234567",
          "gender": "FEMALE",
          "carId": 1,
          "rating": 5.0
        }
      """

  Scenario: Delete driver
    Given access token
    When delete driver with id 1
    Then response status is 204
