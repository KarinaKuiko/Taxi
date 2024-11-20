Feature: Ride API

  Scenario: Create ride
    Given request body to create or update ride
      """
        {
          "driverId": 1,
          "passengerId": 1,
          "addressFrom": "Minsk",
          "addressTo": "Gomel"
        }
      """
    When create ride
    Then response status is 201
    And response body contain ride data
      """
        {
          "id": 3,
          "driverId": 1,
          "passengerId": 1,
          "addressFrom": "Minsk",
          "addressTo": "Gomel",
          "driverRideStatus": "CREATED",
          "passengerRideStatus": "WAITING"
        }
      """

  Scenario: Get ride by id
    When get ride with id 3
    Then response status is 200
    And response body contain ride data
      """
        {
          "id": 3,
          "driverId": 1,
          "passengerId": 1,
          "addressFrom": "Minsk",
          "addressTo": "Gomel",
          "driverRideStatus": "CREATED",
          "passengerRideStatus": "WAITING"
        }
      """

  Scenario: Update ride
    Given request body to create or update ride
      """
        {
          "driverId": 1,
          "passengerId": 1,
          "addressFrom": "Gomel",
          "addressTo": "Minsk"
        }
      """
    When update ride with id 3
    Then response status is 200
    And response body contain ride data
      """
        {
          "id": 3,
          "driverId": 1,
          "passengerId": 1,
          "addressFrom": "Gomel",
          "addressTo": "Minsk",
          "driverRideStatus": "CREATED",
          "passengerRideStatus": "WAITING"
        }
      """

  Scenario: Update driver status ride with id
    Given request body to create or update driver ride status
      """
        {
          "rideStatus": "ACCEPTED"
        }
      """
    When update driver status ride with id 3
    Then response status is 200
    And response body contain ride data
      """
        {
          "id": 3,
          "driverId": 1,
          "passengerId": 1,
          "addressFrom": "Gomel",
          "addressTo": "Minsk",
          "driverRideStatus": "ACCEPTED",
          "passengerRideStatus": "WAITING"
        }
      """

  Scenario: Update passenger status ride with id
    Given request body to create or update passenger ride status
      """
        {
          "rideStatus": "GETTING_OUT"
        }
      """
    When update passenger status ride with id 2
    Then response status is 200
    And response body contain ride data
      """
        {
          "id": 2,
          "driverId": 1,
          "passengerId": 1,
          "addressFrom": "from",
          "addressTo": "to",
          "driverRideStatus": "WAITING",
          "passengerRideStatus": "GETTING_OUT"
        }
      """