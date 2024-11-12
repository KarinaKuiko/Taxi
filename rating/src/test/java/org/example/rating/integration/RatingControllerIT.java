package org.example.rating.integration;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.example.rating.dto.create.RateCreateEditDto;
import org.example.rating.entity.DriverRate;
import org.example.rating.entity.PassengerRate;
import org.example.rating.repository.DriverRateRepository;
import org.example.rating.repository.PassengerRateRepository;
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
import static org.example.rating.util.DataUtil.DEFAULT_ID;
import static org.example.rating.util.DataUtil.DRIVER_URL;
import static org.example.rating.util.DataUtil.DRIVER_URL_WITH_ID;
import static org.example.rating.util.DataUtil.LIMIT;
import static org.example.rating.util.DataUtil.LIMIT_VALUE;
import static org.example.rating.util.DataUtil.PAGE;
import static org.example.rating.util.DataUtil.PAGE_VALUE;
import static org.example.rating.util.DataUtil.PASSENGER_URL;
import static org.example.rating.util.DataUtil.PASSENGER_URL_WITH_ID;
import static org.example.rating.util.DataUtil.URL;
import static org.example.rating.util.DataUtil.URL_WITH_ID;
import static org.example.rating.util.DataUtil.getDriverRateBuilder;
import static org.example.rating.util.DataUtil.getPassengerRateBuilder;
import static org.example.rating.util.DataUtil.getPassengerRateCreateEditDtoBuilder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RatingControllerIT {

    private static WireMockServer wireMockServer;

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
    private PassengerRateRepository passengerRateRepository;

    @Autowired
    private DriverRateRepository driverRateRepository;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private PassengerRate defaultPassengerRate;
    private DriverRate defaultDriverRate;

    @BeforeAll
    static void setUp() {
        kafkaContainer.start();
        wireMockServer = new WireMockServer(WireMockConfiguration.wireMockConfig()
                .port(8083));
        wireMockServer.start();
    }

    @BeforeEach
    void init() {
        RestAssuredMockMvc.mockMvc(MockMvcBuilders.webAppContextSetup(webApplicationContext).build());
        passengerRateRepository.deleteAll();
        jdbcTemplate.execute("ALTER SEQUENCE passenger_rates_id_seq RESTART WITH 1");
        jdbcTemplate.execute("ALTER SEQUENCE rating_id_seq RESTART WITH 1");
        defaultDriverRate = getDriverRateBuilder().build();
        defaultPassengerRate = getPassengerRateBuilder().build();
        passengerRateRepository.save(defaultPassengerRate);
        driverRateRepository.save(defaultDriverRate);
    }

    @Test
    void findAllDriversRates_whenValidParams_thenReturn200() {
        RestAssuredMockMvc
                .given()
                .param(PAGE, PAGE_VALUE)
                .param(LIMIT, LIMIT_VALUE)
                .when()
                .get(DRIVER_URL)
                .then()
                .statusCode(200)
                .body("content.size()", equalTo(1));
    }

    @Test
    void findAllPassengersRates_whenValidParams_thenReturn200() {
        RestAssuredMockMvc
                .when()
                .get(PASSENGER_URL)
                .then()
                .statusCode(200)
                .body("content.size()", equalTo(1));
    }

    @Test
    void findDriverRateById_whenRateIsFound_thenReturn200AndRateReadDto() {
        RestAssuredMockMvc
                .when()
                .get(DRIVER_URL_WITH_ID, DEFAULT_ID.toString())
                .then()
                .statusCode(200)
                .body("id", equalTo(1))
                .body("rideId", equalTo(1))
                .body("comment", equalTo("Good"))
                .body("rating", equalTo(4));
    }

    @Test
    void findDriverRateById_whenRateIsNotFound_thenReturn404() {
        RestAssuredMockMvc
                .when()
                .get(DRIVER_URL_WITH_ID, "2")
                .then()
                .statusCode(404)
                .body("message", equalTo("Rate was not found"));
    }

    @Test
    void findPassengerRateById_whenRateIsFound_thenReturn200AndRateReadDto() {
        RestAssuredMockMvc
                .when()
                .get(PASSENGER_URL_WITH_ID, DEFAULT_ID.toString())
                .then()
                .statusCode(200)
                .body("id", equalTo(1))
                .body("rideId", equalTo(1))
                .body("comment", equalTo("Good"))
                .body("rating", equalTo(4));
    }

    @Test
    void findPassengerRateById_whenRateIsNotFound_thenReturn404() {
        RestAssuredMockMvc
                .when()
                .get(PASSENGER_URL_WITH_ID, "2")
                .then()
                .statusCode(404)
                .body("message", equalTo("Rate was not found"));
    }

    @Test
    void create_whenRideIsFound_thenReturn200AndRateReadDto() {
        RateCreateEditDto createRate = getPassengerRateCreateEditDtoBuilder().build();

        getRide();

        RestAssuredMockMvc
                .given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(createRate)
                .when()
                .post(URL)
                .then()
                .statusCode(201)
                .body("id", notNullValue());
    }

    @Test
    void create_whenRideIsNotFound_thenReturn404() {
        RateCreateEditDto createRate = getPassengerRateCreateEditDtoBuilder().build();

        getNonexistentRide();

        RestAssuredMockMvc
                .given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(createRate)
                .when()
                .post(URL)
                .then()
                .statusCode(404)
                .body("message", equalTo("Ride was not found"));
    }

    @Test
    void update_whenRideAndRateIsFound_thenReturn200AndRateReadDto() {
        RateCreateEditDto updateRate = getPassengerRateCreateEditDtoBuilder()
                                        .rating(5)
                                        .build();

        getRide();

        RestAssuredMockMvc
                .given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(updateRate)
                .when()
                .put(URL_WITH_ID, DEFAULT_ID.toString())
                .then()
                .statusCode(200)
                .body("id", equalTo(1))
                .body("rideId", equalTo(1))
                .body("comment", equalTo("Good"))
                .body("rating", equalTo(5));
    }

    @Test
    void update_whenRideIsNotFound_thenReturn404() {
        RateCreateEditDto updateRate = getPassengerRateCreateEditDtoBuilder().build();

        getNonexistentRide();

        RestAssuredMockMvc
                .given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(updateRate)
                .when()
                .put(URL_WITH_ID, DEFAULT_ID.toString())
                .then()
                .statusCode(404)
                .body("message", equalTo("Ride was not found"));
    }

    @Test
    void update_whenRateIsNotFound_thenReturn404() {
        RateCreateEditDto updateRate = getPassengerRateCreateEditDtoBuilder().build();

        RestAssuredMockMvc
                .given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(updateRate)
                .when()
                .put(URL_WITH_ID, "2")
                .then()
                .statusCode(404)
                .body("message", equalTo("Rate was not found"));
    }

    private void getRide() {
        wireMockServer.stubFor(
                get(urlEqualTo("/api/v1/rides/1"))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withStatus(200)
                                .withBody("""
                                        {
                                            "id": 1,
                                            "driverId": 1,
                                            "passengerId": 1,
                                            "addressFrom": "From",
                                            "addressTo": "To",
                                            "driverRideStatus": "ACCEPTED",
                                            "passengerRideStatus": "WAITING",
                                            "cost": 29.99
                                        }
                                        """)));
    }

    private void getNonexistentRide() {
        wireMockServer.stubFor(
                get(urlEqualTo("/api/v1/rides/1"))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withStatus(404)
                                .withBody("{\"message\": \"Ride was not found\"}")));

    }
}
