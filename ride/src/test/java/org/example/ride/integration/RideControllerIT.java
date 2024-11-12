package org.example.ride.integration;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.example.ride.dto.create.DriverRideStatusDto;
import org.example.ride.dto.create.PassengerRideStatusDto;
import org.example.ride.dto.create.RideCreateEditDto;
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

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.example.ride.util.DataUtil.DEFAULT_ID;
import static org.example.ride.util.DataUtil.URL;
import static org.example.ride.util.DataUtil.URL_WITH_ID;
import static org.example.ride.util.DataUtil.getRideBuilder;
import static org.example.ride.util.DataUtil.getRideCreateEditDtoBuilder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RideControllerIT {
    private static WireMockServer driverWireMockServer;
    private static WireMockServer passengererWireMockServer;

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
        driverWireMockServer = new WireMockServer(WireMockConfiguration.wireMockConfig()
                .port(8081));
        passengererWireMockServer = new WireMockServer(WireMockConfiguration.wireMockConfig()
                .port(8082));
        driverWireMockServer.start();
        passengererWireMockServer.start();
    }

    @BeforeEach
    void init() {
        RestAssuredMockMvc.mockMvc(MockMvcBuilders.webAppContextSetup(webApplicationContext).build());
        rideRepository.deleteAll();
        jdbcTemplate.execute("ALTER SEQUENCE rides_id_seq RESTART WITH 1");
        defaultRide = getRideBuilder().build();
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
                .get(URL_WITH_ID, DEFAULT_ID.toString())
                .then()
                .statusCode(200)
                .body("id", equalTo(1))
                .body("driverId", equalTo(1))
                .body("passengerId", equalTo(1))
                .body("addressFrom", equalTo("from"))
                .body("addressTo", equalTo("to"));
    }

    @Test
    void findById_whenRideIsNotFound_thenReturn404() {
        RestAssuredMockMvc
                .when()
                .get(URL_WITH_ID, "2")
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
                .put(URL_WITH_ID + "/driver-status", DEFAULT_ID.toString())
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
                .put(URL_WITH_ID + "/driver-status", DEFAULT_ID.toString())
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
                .put(URL_WITH_ID + "/driver-status", "2")
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
                .put(URL_WITH_ID + "/passenger-status", DEFAULT_ID.toString())
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
                .put(URL_WITH_ID + "/passenger-status", DEFAULT_ID.toString())
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
                .put(URL_WITH_ID + "/passenger-status", "2")
                .then()
                .statusCode(404)
                .body("message", equalTo("Ride was not found"));
    }

    @Test
    void create_whenValidInput_thenReturn201AndRideReadDto() {
        RideCreateEditDto createRide = getRideCreateEditDtoBuilder().build();

        getDriver();
        getPassenger();

        RestAssuredMockMvc
                .given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(createRide)
                .when()
                .post(URL)
                .then()
                .statusCode(201)
                .body("id", notNullValue());

    }

    @Test
    void create_whenDriverIsNotFound_thenReturn404() {
        RideCreateEditDto createRide = getRideCreateEditDtoBuilder().build();

        getNonexistentDriver();
        getPassenger();

        RestAssuredMockMvc
                .given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(createRide)
                .when()
                .post(URL)
                .then()
                .statusCode(404)
                .body("message", equalTo("Driver was not found"));
    }

    @Test
    void create_whenPassengerIsNotFound_thenReturn404() {
        RideCreateEditDto createRide = getRideCreateEditDtoBuilder().build();

        getDriver();
        getNonexistentPassenger();

        RestAssuredMockMvc
                .given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(createRide)
                .when()
                .post(URL)
                .then()
                .statusCode(404)
                .body("message", equalTo("Passenger was not found"));
    }

    @Test
    void update_whenValidInput_thenReturn200AndRideReadDto(){
        RideCreateEditDto updateRide = getRideCreateEditDtoBuilder()
                                        .addressFrom("Minsk")
                                        .addressTo("To")
                                        .build();

        getDriver();
        getPassenger();

        RestAssuredMockMvc
                .given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(updateRide)
                .when()
                .put(URL_WITH_ID, DEFAULT_ID.toString())
                .then()
                .statusCode(200)
                .body("driverId", equalTo(1))
                .body("passengerId", equalTo(1))
                .body("addressFrom", equalTo("Minsk"))
                .body("addressTo", equalTo("To"));
    }

    @Test
    void update_whenDriverIsNotFound_thenReturn404() {
        RideCreateEditDto updateRide = getRideCreateEditDtoBuilder()
                                        .addressFrom("Minsk")
                                        .addressTo("To")
                                        .build();

        getNonexistentDriver();
        getPassenger();

        RestAssuredMockMvc
                .given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(updateRide)
                .when()
                .put(URL_WITH_ID, DEFAULT_ID.toString())
                .then()
                .statusCode(404)
                .body("message", equalTo("Driver was not found"));
    }

    @Test
    void update_whenPassengerIsNotFound_thenReturn404() {
        RideCreateEditDto updateRide = getRideCreateEditDtoBuilder()
                                        .addressFrom("Minsk")
                                        .addressTo("To")
                                        .build();

        getDriver();
        getNonexistentPassenger();

        RestAssuredMockMvc
                .given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(updateRide)
                .when()
                .put(URL_WITH_ID, DEFAULT_ID.toString())
                .then()
                .statusCode(404)
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
                .statusCode(404)
                .body("message", equalTo("Ride was not found"));
    }

    private void getDriver() {
        driverWireMockServer.stubFor(
                get(urlEqualTo("/api/v1/drivers/1"))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withStatus(200)
                                .withBody("""
                                        {
                                            "id": 1,
                                            "name": "John Doe",
                                            "email": "john.doe@example.com",
                                            "phone": "+375441234567",
                                            "gender": "MALE",
                                            "carId": 1,
                                            "rating": 4.5
                                        }
                                        """)));
    }

    private void getPassenger() {
        passengererWireMockServer.stubFor(
                get(urlEqualTo("/api/v1/passengers/1"))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withStatus(200)
                                .withBody("""
                                        {
                                            "id": 1,
                                            "name": "Jane Smith",
                                            "email": "jane.smith@example.com",
                                            "phone": "+375441234567",
                                            "rating": 4.8
                                        }
                                        """)));
    }

    private void getNonexistentDriver() {
        driverWireMockServer.stubFor(
                get(urlEqualTo("/api/v1/drivers/1"))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withStatus(404)
                                .withBody("{\"message\": \"Driver was not found\"}")));
    }

    private void getNonexistentPassenger() {
        passengererWireMockServer.stubFor(
                get(urlEqualTo("/api/v1/passengers/1"))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withStatus(404)
                                .withBody("{\"message\": \"Passenger was not found\"}")));
    }
}
