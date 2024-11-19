package org.example.driver.e2e;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.example.driver.dto.create.CarCreateEditDto;
import org.example.driver.dto.create.DriverCreateEditDto;
import org.example.driver.dto.read.CarReadDto;
import org.example.driver.dto.read.DriverReadDto;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.example.driver.util.DataUtil.BASE_URL;
import static org.example.driver.util.DataUtil.BASE_URL_WITH_ID;
import static org.example.driver.util.DataUtil.CAR_ENTITY;
import static org.example.driver.util.DataUtil.DRIVER_ENTITY;


public class DriverSteps {
    private final ObjectMapper objectMapper = new ObjectMapper();

    private CarCreateEditDto carRequestDto;
    private DriverCreateEditDto driverRequestDto;
    private Response response;

    @Given("request body to create or update car")
    public void requestBodyToCreateOrUpdateCar(String requestBody) throws Exception {
        carRequestDto = objectMapper.readValue(requestBody, CarCreateEditDto.class);
    }

    @When("create car")
    public void createCar() {
        response = given()
                .contentType(ContentType.JSON)
                .body(carRequestDto)
                .when()
                .post(BASE_URL, CAR_ENTITY);
    }

    @Then("response status is {int}")
    public void responseStatusIs(int responseStatus) {
        response
                .then()
                .statusCode(responseStatus);
    }

    @And("response body contain car data")
    public void responseBodyContainCarData(String expected) throws Exception {
        assertThat(response.as(CarReadDto.class))
                .isEqualTo(objectMapper.readValue(expected, CarReadDto.class));
    }

    @When("get car with id {int}")
    public void getCarWithId(int id) {
        response = given()
                .when()
                .get(BASE_URL_WITH_ID, CAR_ENTITY, id);
    }

    @When("update car with id {int}")
    public void updateCarWithId(int id) {
        response = given()
                .contentType(ContentType.JSON)
                .body(carRequestDto)
                .when()
                .put(BASE_URL_WITH_ID, CAR_ENTITY, id);
    }

    @When("delete car with id {int}")
    public void deleteCarWithId(int id) {
        response = given()
                .when()
                .delete(BASE_URL_WITH_ID, CAR_ENTITY, id);
    }

    @Given("request body to create or update driver")
    public void requestBodyToCreateOrUpdateDriver(String requestBody) throws Exception {
        driverRequestDto = objectMapper.readValue(requestBody, DriverCreateEditDto.class);
    }

    @When("create driver")
    public void createDriver() {
        response = given()
                .contentType(ContentType.JSON)
                .body(driverRequestDto)
                .when()
                .post(BASE_URL, DRIVER_ENTITY);
    }

    @And("response body contain driver data")
    public void responseBodyContainDriverData(String expected) throws Exception {
        assertThat(response.as(DriverReadDto.class))
                .isEqualTo(objectMapper.readValue(expected, DriverReadDto.class));
    }

    @When("get driver with id {int}")
    public void getDriverWithId(int id) {
        response = given()
                .when()
                .get(BASE_URL_WITH_ID, DRIVER_ENTITY, id);
    }

    @When("update driver with id {int}")
    public void updateDriverWithId(int id) {
        response = given()
                .contentType(ContentType.JSON)
                .body(driverRequestDto)
                .when()
                .put(BASE_URL_WITH_ID, DRIVER_ENTITY, id);
    }

    @When("delete driver with id {int}")
    public void deleteDriverWithId(int id) {
        response = given()
                .when()
                .delete(BASE_URL_WITH_ID, DRIVER_ENTITY, id);
    }
}