Feature: Driver API

  Scenario: Create car
    Given request body to create or update car
      """
        {
          "color": "red",
          "brand": "BMW",
          "number": "AB123CD",
          "year": 2023
        }
      """
    When create car
    Then response status is 201
    And response body contain car data
      """
        {
          "id": 1,
          "color": "red",
          "brand": "BMW",
          "number": "AB123CD",
          "year": 2023,
          "drivers": []
        }
      """

  Scenario: Get car by id
    When get car with id 1
    Then response status is 200
    And response body contain car data
      """
        {
          "id": 1,
          "color": "red",
          "brand": "BMW",
          "number": "AB123CD",
          "year": 2023,
          "drivers": []
        }
      """

  Scenario: Update car
    Given request body to create or update car
      """
        {
          "color": "yellow",
          "brand": "BMW",
          "number": "AB123CD",
          "year": 2014
        }
      """
    When update car with id 1
    Then response status is 200
    And response body contain car data
      """
        {
          "id": 1,
          "color": "yellow",
          "brand": "BMW",
          "number": "AB123CD",
          "year": 2014,
          "drivers": []
        }
      """

  Scenario: Create driver
    Given request body to create or update driver
      """
        {
          "name": "Test",
          "email": "test@gmail.com",
          "phone": "+375331234567",
          "gender": "MALE",
          "carId": 1
        }
      """
    When create driver
    Then response status is 201
    And response body contain driver data
      """
        {
          "id": 1,
          "name": "Test",
          "email": "test@gmail.com",
          "phone": "+375331234567",
          "gender": "MALE",
          "carId": 1,
          "rating": 5.0
        }
      """

  Scenario: Get driver by id
    When get driver with id 1
    Then response status is 200
    And response body contain driver data
      """
        {
          "id": 1,
          "name": "Test",
          "email": "test@gmail.com",
          "phone": "+375331234567",
          "gender": "MALE",
          "carId": 1,
          "rating": 5.0
        }
      """

  Scenario: Update driver
    Given request body to create or update driver
      """
        {
          "name": "Driver",
          "email": "test@gmail.com",
          "phone": "+375331234567",
          "gender": "FEMALE",
          "carId": 1
        }
      """
    When update driver with id 1
    Then response status is 200
    And response body contain driver data
      """
        {
          "id": 1,
          "name": "Driver",
          "email": "test@gmail.com",
          "phone": "+375331234567",
          "gender": "FEMALE",
          "carId": 1,
          "rating": 5.0
        }
      """

  Scenario: Delete driver
    When delete driver with id 1
    Then response status is 204

  Scenario: Delete car
    When delete car with id 1
    Then response status is 204