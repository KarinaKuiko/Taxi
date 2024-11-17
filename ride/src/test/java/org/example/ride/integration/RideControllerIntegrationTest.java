package org.example.ride.integration;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.example.ride.dto.create.DriverRideStatusDto;
import org.example.ride.dto.create.PassengerRideStatusDto;
import org.example.ride.dto.create.RideCreateEditDto;
import org.example.ride.entity.Ride;
import org.example.ride.entity.enumeration.DriverRideStatus;
import org.example.ride.entity.enumeration.PassengerRideStatus;
import org.example.ride.repository.RideRepository;
import org.example.ride.wireMock.DriverWireMock;
import org.example.ride.wireMock.PassengerWireMock;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.example.ride.util.DataUtil.DEFAULT_ADDRESS_FROM;
import static org.example.ride.util.DataUtil.DEFAULT_ADDRESS_TO;
import static org.example.ride.util.DataUtil.DEFAULT_ID;
import static org.example.ride.util.DataUtil.DRIVER_STATUS;
import static org.example.ride.util.DataUtil.PASSENGER_STATUS;
import static org.example.ride.util.DataUtil.URL;
import static org.example.ride.util.DataUtil.URL_WITH_ID;
import static org.example.ride.util.DataUtil.getRideBuilder;
import static org.example.ride.util.DataUtil.getRideCreateEditDtoBuilder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@WireMockTest
@Sql(scripts = "/setup_ride_table.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class RideControllerIntegrationTest {

    @Container
    public static PostgreSQLContainer postgreSQLContainer =
            new PostgreSQLContainer<>("postgres:15-alpine");

    @DynamicPropertySource
    static void postgresqlProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
    }

    @Container
    public static KafkaContainer kafkaContainer = new KafkaContainer(
            DockerImageName.parse("confluentinc/cp-kafka:latest"));

    @DynamicPropertySource
    static void kafkaProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", kafkaContainer::getBootstrapServers);
    }

    @Autowired
    private RideRepository rideRepository;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @BeforeAll
    static void setUp() {
        kafkaContainer.start();
        DriverWireMock.driverWireMockServer.start();
        PassengerWireMock.passengerWireMockServer.start();
    }

    @BeforeEach
    void init() {
        RestAssuredMockMvc.mockMvc(MockMvcBuilders.webAppContextSetup(webApplicationContext).build());
    }

    @Test
    void findAll_whenCorrectParams_thenReturn() {
        RestAssuredMockMvc
                .when()
                .get(URL)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("content.size()", equalTo(1));
    }

    @Test
    void findById_whenRideIsFound_thenReturn200AndRideReadDto() {
        RestAssuredMockMvc
                .when()
                .get(URL_WITH_ID, DEFAULT_ID.toString())
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", equalTo(DEFAULT_ID.intValue()))
                .body("driverId", equalTo(DEFAULT_ID.intValue()))
                .body("passengerId", equalTo(DEFAULT_ID.intValue()))
                .body("addressFrom", equalTo(DEFAULT_ADDRESS_FROM))
                .body("addressTo", equalTo(DEFAULT_ADDRESS_TO));
    }

    @Test
    void findById_whenRideIsNotFound_thenReturn404() {
        RestAssuredMockMvc
                .when()
                .get(URL_WITH_ID, "2")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("message", equalTo("Ride was not found"));
    }

    @Test
    void updateDriverStatus_whenValidInput_thenReturn200AndRideReadDto() {
        DriverRideStatusDto rideStatusDto = new DriverRideStatusDto(DriverRideStatus.ON_WAY_FOR_PASSENGER);

        RestAssuredMockMvc
                .given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(rideStatusDto)
                .when()
                .put(URL_WITH_ID + DRIVER_STATUS, DEFAULT_ID.toString())
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("driverRideStatus", equalTo(DriverRideStatus.ON_WAY_FOR_PASSENGER.name()));
    }

    @Test
    void updateDriverStatus_whenInvalidInput_thenReturn409() {
        DriverRideStatusDto rideStatusDto = new DriverRideStatusDto(DriverRideStatus.COMPLETED);

        RestAssuredMockMvc
                .given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(rideStatusDto)
                .when()
                .put(URL_WITH_ID + DRIVER_STATUS, DEFAULT_ID.toString())
                .then()
                .statusCode(HttpStatus.CONFLICT.value())
                .body("message", equalTo("Cannot be updated to the proposed status"));
    }

    @Test
    void updateDriverStatus_whenRideIsNotFound_thenReturn404() {
        DriverRideStatusDto rideStatusDto = new DriverRideStatusDto(DriverRideStatus.COMPLETED);

        RestAssuredMockMvc
                .given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(rideStatusDto)
                .when()
                .put(URL_WITH_ID + DRIVER_STATUS, "2")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("message", equalTo("Ride was not found"));
    }

    @Test
    void updatePassengerStatus_whenValidInput_thenReturn200AndRideReadDto() {
        Ride defaultRide = getRideBuilder().build();
        defaultRide.setDriverRideStatus(DriverRideStatus.WAITING);
        rideRepository.save(defaultRide);

        PassengerRideStatusDto rideStatusDto = new PassengerRideStatusDto(PassengerRideStatus.GETTING_OUT);

        RestAssuredMockMvc
                .given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(rideStatusDto)
                .when()
                .put(URL_WITH_ID + PASSENGER_STATUS, DEFAULT_ID.toString())
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("passengerRideStatus", equalTo(PassengerRideStatus.GETTING_OUT.name()));
    }

    @Test
    void updatePassengerStatus_whenInvalidInput_thenReturn409() {
        PassengerRideStatusDto rideStatusDto = new PassengerRideStatusDto(PassengerRideStatus.GETTING_OUT);

        RestAssuredMockMvc
                .given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(rideStatusDto)
                .when()
                .put(URL_WITH_ID + PASSENGER_STATUS, DEFAULT_ID.toString())
                .then()
                .statusCode(HttpStatus.CONFLICT.value())
                .body("message", equalTo("Status cannot be changed now"));
    }

    @Test
    void updatePassengerStatus_whenRideIsNotFound_thenReturn404() {
        PassengerRideStatusDto rideStatusDto = new PassengerRideStatusDto(PassengerRideStatus.GETTING_OUT);

        RestAssuredMockMvc
                .given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(rideStatusDto)
                .when()
                .put(URL_WITH_ID + PASSENGER_STATUS, "2")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("message", equalTo("Ride was not found"));
    }

    @Test
    void create_whenValidInput_thenReturn201AndRideReadDto() {
        RideCreateEditDto createRide = getRideCreateEditDtoBuilder().build();

        DriverWireMock.getDriver();
        PassengerWireMock.getPassenger();

        RestAssuredMockMvc
                .given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(createRide)
                .when()
                .post(URL)
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .body("id", notNullValue());

    }

    @Test
    void create_whenDriverIsNotFound_thenReturn404() {
        RideCreateEditDto createRide = getRideCreateEditDtoBuilder().build();

        DriverWireMock.getNonexistentDriver();
        PassengerWireMock.getPassenger();

        RestAssuredMockMvc
                .given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(createRide)
                .when()
                .post(URL)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("message", equalTo("Driver was not found"));
    }

    @Test
    void create_whenPassengerIsNotFound_thenReturn404() {
        RideCreateEditDto createRide = getRideCreateEditDtoBuilder().build();

        DriverWireMock.getDriver();
        PassengerWireMock.getNonexistentPassenger();

        RestAssuredMockMvc
                .given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(createRide)
                .when()
                .post(URL)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("message", equalTo("Passenger was not found"));
    }

    @Test
    void update_whenValidInput_thenReturn200AndRideReadDto(){
        RideCreateEditDto updateRide = getRideCreateEditDtoBuilder()
                                        .addressFrom("Minsk")
                                        .addressTo("To")
                                        .build();

        DriverWireMock.getDriver();
        PassengerWireMock.getPassenger();

        RestAssuredMockMvc
                .given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(updateRide)
                .when()
                .put(URL_WITH_ID, DEFAULT_ID.toString())
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("driverId", equalTo(DEFAULT_ID.intValue()))
                .body("passengerId", equalTo(DEFAULT_ID.intValue()))
                .body("addressFrom", equalTo("Minsk"))
                .body("addressTo", equalTo("To"))
                .log().all();
    }

    @Test
    void update_whenDriverIsNotFound_thenReturn404() {
        RideCreateEditDto updateRide = getRideCreateEditDtoBuilder()
                                        .addressFrom("Minsk")
                                        .addressTo("To")
                                        .build();

        DriverWireMock.getNonexistentDriver();
        PassengerWireMock.getPassenger();

        RestAssuredMockMvc
                .given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(updateRide)
                .when()
                .put(URL_WITH_ID, DEFAULT_ID.toString())
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("message", equalTo("Driver was not found"));
    }

    @Test
    void update_whenPassengerIsNotFound_thenReturn404() {
        RideCreateEditDto updateRide = getRideCreateEditDtoBuilder()
                                        .addressFrom("Minsk")
                                        .addressTo("To")
                                        .build();

        DriverWireMock.getDriver();
        PassengerWireMock.getNonexistentPassenger();

        RestAssuredMockMvc
                .given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(updateRide)
                .when()
                .put(URL_WITH_ID, DEFAULT_ID.toString())
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("message", equalTo("Passenger was not found"));
    }

    @Test
    void update_whenRideIsNotFound() {
        RideCreateEditDto updateRide = getRideCreateEditDtoBuilder().build();

        RestAssuredMockMvc
                .given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(updateRide)
                .when()
                .put(URL_WITH_ID, "2")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("message", equalTo("Ride was not found"));
    }
}
