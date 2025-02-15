package org.example.passenger.e2e.step;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.example.passenger.dto.create.PassengerCreateEditDto;
import org.example.passenger.dto.read.PassengerReadDto;
import org.example.passenger.util.TokenReadDto;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.example.passenger.util.DataUtil.AUTHORIZATION;
import static org.example.passenger.util.DataUtil.AUTH_URL;
import static org.example.passenger.util.DataUtil.BASE_URL;
import static org.example.passenger.util.DataUtil.BASE_URL_WITH_ID;
import static org.example.passenger.util.DataUtil.BEARER;
import static org.example.passenger.util.DataUtil.signInUserDto;

public class PassengerSteps {
    private final ObjectMapper objectMapper = new ObjectMapper();

    private PassengerCreateEditDto passengerRequestDto;
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

    @And("request body to create or update passenger")
    public void requestBodyToCreateOrUpdatePassenger(String requestBody) throws Exception {
        passengerRequestDto = objectMapper.readValue(requestBody, PassengerCreateEditDto.class);
    }

    @When("create passenger")
    public void createPassenger() {
        response = given()
                .contentType(ContentType.JSON)
                .body(passengerRequestDto)
                .header(AUTHORIZATION, BEARER + accessToken)
                .when()
                .post(BASE_URL);
    }

    @Then("response status is {int}")
    public void responseStatusIs(int responseStatus) {
        response
                .then()
                .statusCode(responseStatus);
    }

    @And("response body contain passenger data")
    public void responseBodyContainPassengerData(String expected) throws Exception {
        assertThat(response.as(PassengerReadDto.class))
                .isEqualTo(objectMapper.readValue(expected, PassengerReadDto.class));
    }

    @When("get passenger with id {int}")
    public void getPassengerWithId(int id) {
        response = given()
                .header(AUTHORIZATION, BEARER + accessToken)
                .when()
                .get(BASE_URL_WITH_ID, id);
    }

    @When("update passenger with id {int}")
    public void updatePassengerWithId(int id) {
        response = given()
                .header(AUTHORIZATION, BEARER + accessToken)
                .contentType(ContentType.JSON)
                .body(passengerRequestDto)
                .when()
                .put(BASE_URL_WITH_ID, id);
    }

    @When("delete passenger with id {int}")
    public void deletePassengerWithId(int id) {
        response = given()
                .header(AUTHORIZATION, BEARER + accessToken)
                .when()
                .delete(BASE_URL_WITH_ID, id);
    }
}
