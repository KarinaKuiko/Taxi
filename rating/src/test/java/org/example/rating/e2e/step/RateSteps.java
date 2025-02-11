package org.example.rating.e2e.step;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.example.rating.dto.create.RateCreateEditDto;
import org.example.rating.dto.read.RateReadDto;
import org.example.rating.util.TokenReadDto;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.example.rating.util.DataUtil.*;

public class RateSteps {
    private final ObjectMapper objectMapper = new ObjectMapper();

    private RateCreateEditDto rateRequestDto;
    private Response response;
    private String accessToken;

    @Given("access token")
    public void accessToken() {
        Response token_response = given()
                .contentType(ContentType.JSON)
                .body(signInUserDto())
                .when()
                .post(AUTH_URL);

        TokenReadDto tokenReadDto = token_response.as(TokenReadDto.class);
        accessToken = tokenReadDto.accessToken();
    }

    @And("request body to create or update rate")
    public void requestBodyToCreateOrUpdateRate(String requestBody) throws Exception {
        rateRequestDto = objectMapper.readValue(requestBody, RateCreateEditDto.class);
    }

    @When("create rate")
    public void createRate() {
        response = given()
                .header(AUTHORIZATION, BEARER + accessToken)
                .contentType(ContentType.JSON)
                .body(rateRequestDto)
                .when()
                .post(BASE_URL);
    }

    @Then("response status is {int}")
    public void responseStatusIs(int responseStatus) {
        response
                .then()
                .statusCode(responseStatus);
    }

    @And("response body contain rate data")
    public void responseBodyContainRateData(String expected) throws Exception {
        assertThat(response.as(RateReadDto.class))
                .isEqualTo(objectMapper.readValue(expected, RateReadDto.class));
    }

    @When("get passenger rate with id {int}")
    public void getPassengerRateWithId(int id) {
        response = given()
                .header(AUTHORIZATION, BEARER + accessToken)
                .when()
                .get(HOST_PORT + PASSENGER_URL_WITH_ID, id);
    }

    @When("get driver rate with id {int}")
    public void getDriverRateWithId(int id) {
        response = given()
                .header(AUTHORIZATION, BEARER + accessToken)
                .when()
                .get(HOST_PORT + DRIVER_URL_WITH_ID, id);
    }

    @When("update rate with id {int}")
    public void updateRateWithId(int id) {
        response = given()
                .header(AUTHORIZATION, BEARER + accessToken)
                .contentType(ContentType.JSON)
                .body(rateRequestDto)
                .when()
                .put(BASE_URL_WITH_ID, id);
    }
}
