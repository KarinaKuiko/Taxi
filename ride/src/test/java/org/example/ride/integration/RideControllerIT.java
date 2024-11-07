package org.example.ride.integration;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.example.ride.dto.create.DriverRideStatusDto;
import org.example.ride.dto.create.PassengerRideStatusDto;
import org.example.ride.entity.Ride;
import org.example.ride.entity.enumeration.DriverRideStatus;
import org.example.ride.entity.enumeration.PassengerRideStatus;
import org.example.ride.repository.RideRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.equalTo;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RideControllerIT {
    private static final String URL = "/api/v1/rides";

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

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private Ride defaultRide;

    @BeforeAll
    static void setUp() {
        kafkaContainer.start();
    }

    @BeforeEach
    void init() {
        RestAssuredMockMvc.mockMvc(MockMvcBuilders.webAppContextSetup(webApplicationContext).build());
        rideRepository.deleteAll();
        jdbcTemplate.execute("ALTER SEQUENCE rides_id_seq RESTART WITH 1");
        defaultRide = new Ride(1L, 1L, 1L, "From", "To", DriverRideStatus.ACCEPTED, PassengerRideStatus.WAITING, new BigDecimal("123.45"));
        rideRepository.save(defaultRide);
    }

    @Test
    void findAll_whenCorrectParams_thenReturn() {
        RestAssuredMockMvc
                .when()
                .get(URL)
                .then()
                .statusCode(200)
                .body("content.size()", equalTo(1));
    }

    @Test
    void findById_whenRideIsFound_thenReturn200AndRideReadDto() {
        RestAssuredMockMvc
                .when()
                .get(URL + "/1")
                .then()
                .statusCode(200)
                .body("id", equalTo(1))
                .body("driverId", equalTo(1))
                .body("passengerId", equalTo(1))
                .body("addressFrom", equalTo("From"))
                .body("addressTo", equalTo("To"));
    }

    @Test
    void findById_whenRideIsNotFound_thenReturn404() {
        RestAssuredMockMvc
                .when()
                .get(URL + "/2")
                .then()
                .statusCode(404)
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
                .put(URL + "/1/driver-status")
                .then()
                .statusCode(200)
                .body("driverRideStatus", equalTo("ON_WAY_FOR_PASSENGER"));
    }

    @Test
    void updateDriverStatus_whenInvalidInput_thenReturn409() {
        DriverRideStatusDto rideStatusDto = new DriverRideStatusDto(DriverRideStatus.COMPLETED);

        RestAssuredMockMvc
                .given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(rideStatusDto)
                .when()
                .put(URL + "/1/driver-status")
                .then()
                .statusCode(409)
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
                .put(URL + "/2/driver-status")
                .then()
                .statusCode(404)
                .body("message", equalTo("Ride was not found"));
    }

    @Test
    void updatePassengerStatus_whenValidInput_thenReturn200AndRideReadDto() {
        defaultRide.setDriverRideStatus(DriverRideStatus.WAITING);
        rideRepository.save(defaultRide);

        PassengerRideStatusDto rideStatusDto = new PassengerRideStatusDto(PassengerRideStatus.GETTING_OUT);

        RestAssuredMockMvc
                .given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(rideStatusDto)
                .when()
                .put(URL + "/1/passenger-status")
                .then()
                .statusCode(200)
                .body("passengerRideStatus", equalTo("GETTING_OUT"));
    }

    @Test
    void updatePassengerStatus_whenInvalidInput_thenReturn409() {
        PassengerRideStatusDto rideStatusDto = new PassengerRideStatusDto(PassengerRideStatus.GETTING_OUT);

        RestAssuredMockMvc
                .given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(rideStatusDto)
                .when()
                .put(URL + "/1/passenger-status")
                .then()
                .statusCode(409)
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
                .put(URL + "/2/passenger-status")
                .then()
                .statusCode(404)
                .body("message", equalTo("Ride was not found"));
    }
}
