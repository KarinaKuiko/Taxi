package org.example.ride.e2e.step;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.example.ride.dto.create.DriverRideStatusDto;
import org.example.ride.dto.create.PassengerRideStatusDto;
import org.example.ride.dto.create.RideCreateEditDto;
import org.example.ride.dto.read.RideReadDto;
import org.example.ride.util.TokenReadDto;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.example.ride.util.DataUtil.AUTHORIZATION;
import static org.example.ride.util.DataUtil.AUTH_URL;
import static org.example.ride.util.DataUtil.BASE_URL;
import static org.example.ride.util.DataUtil.BASE_URL_WITH_ID;
import static org.example.ride.util.DataUtil.BEARER;
import static org.example.ride.util.DataUtil.COST_FIELD;
import static org.example.ride.util.DataUtil.DRIVER_STATUS;
import static org.example.ride.util.DataUtil.PASSENGER_STATUS;
import static org.example.ride.util.DataUtil.signInUserDto;


public class RideSteps {
    private final ObjectMapper objectMapper = new ObjectMapper();

    private RideCreateEditDto rideRequestDto;
    private DriverRideStatusDto driverRideStatusDto;
    private PassengerRideStatusDto passengerRideStatusDto;
    private Response response;
    private String accessToken;

    @Given("access token")
    public void accessToken() {
        Response tokenResponse = given()
                .contentType(ContentType.JSON)
                .body(signInUserDto())
                .when()
                .post(AUTH_URL);

        TokenReadDto tokenReadDto = tokenResponse.as(TokenReadDto.class);
        accessToken = tokenReadDto.accessToken();
    }

    @And("request body to create or update ride")
    public void requestBodyToCreateOrUpdateRide(String requestBody) throws Exception {
        rideRequestDto = objectMapper.readValue(requestBody, RideCreateEditDto.class);
    }

    @When("create ride")
    public void createRide() {
        response = given()
                .header(AUTHORIZATION, BEARER + accessToken)
                .contentType(ContentType.JSON)
                .body(rideRequestDto)
                .when()
                .post(BASE_URL);
    }

    @Then("response status is {int}")
    public void responseStatusIs(int responseStatus) {
        response
                .then()
                .statusCode(responseStatus);
    }

    @And("response body contain ride data")
    public void responseBodyContainRideData(String expected) throws Exception {
        assertThat(response.as(RideReadDto.class))
                .usingRecursiveComparison()
                .ignoringFields(COST_FIELD)
                .isEqualTo(objectMapper.readValue(expected, RideReadDto.class));
    }

    @When("get ride with id {int}")
    public void getRideWithId(int id) {
        response = given()
                .header(AUTHORIZATION, BEARER + accessToken)
                .when()
                .get(BASE_URL_WITH_ID, id);
    }

    @When("update ride with id {int}")
    public void updateRideWithId(int id) {
        response = given()
                .header(AUTHORIZATION, BEARER + accessToken)
                .contentType(ContentType.JSON)
                .body(rideRequestDto)
                .when()
                .put(BASE_URL_WITH_ID, id);
    }

    @And("request body to create or update driver ride status")
    public void requestBodyToCreateOrUpdateDriverRideStatus(String requestBody) throws Exception {
        driverRideStatusDto = objectMapper.readValue(requestBody, DriverRideStatusDto.class);
    }

    @When("update driver status ride with id {int}")
    public void updateDriverStatusRideWithId(int id) {
        response = given()
                .header(AUTHORIZATION, BEARER + accessToken)
                .contentType(ContentType.JSON)
                .body(driverRideStatusDto)
                .when()
                .put(BASE_URL_WITH_ID + DRIVER_STATUS, id);
    }

    @And("request body to create or update passenger ride status")
    public void requestBodyToCreateOrUpdatePassengerRideStatus(String requestBody) throws Exception {
        passengerRideStatusDto = objectMapper.readValue(requestBody, PassengerRideStatusDto.class);
    }

    @When("update passenger status ride with id {int}")
    public void updatePassengerStatusRideWithId(int id) {
        response = given()
                .header(AUTHORIZATION, BEARER + accessToken)
                .contentType(ContentType.JSON)
                .body(passengerRideStatusDto)
                .when()
                .put(BASE_URL_WITH_ID + PASSENGER_STATUS, id);
    }
}
