package org.example.rating.integration;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.example.rating.dto.create.RateCreateEditDto;
import org.example.rating.wiremock.RideWireMock;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
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

import static org.example.rating.util.DataUtil.ACCESS_TOKEN;
import static org.example.rating.util.DataUtil.AUTHORIZATION;
import static org.example.rating.util.DataUtil.BEARER;
import static org.example.rating.util.DataUtil.DEFAULT_COMMENT;
import static org.example.rating.util.DataUtil.DEFAULT_ID;
import static org.example.rating.util.DataUtil.DEFAULT_RATE;
import static org.example.rating.util.DataUtil.DRIVER_URL;
import static org.example.rating.util.DataUtil.DRIVER_URL_WITH_ID;
import static org.example.rating.util.DataUtil.LIMIT;
import static org.example.rating.util.DataUtil.LIMIT_VALUE;
import static org.example.rating.util.DataUtil.MESSAGE;
import static org.example.rating.util.DataUtil.PAGE;
import static org.example.rating.util.DataUtil.PAGE_VALUE;
import static org.example.rating.util.DataUtil.PASSENGER_URL;
import static org.example.rating.util.DataUtil.PASSENGER_URL_WITH_ID;
import static org.example.rating.util.DataUtil.RATE_NOT_FOUND_EXCEPTION_MESSAGE;
import static org.example.rating.util.DataUtil.RIDE_NOT_FOUND_EXCEPTION_MESSAGE;
import static org.example.rating.util.DataUtil.URL;
import static org.example.rating.util.DataUtil.URL_WITH_ID;
import static org.example.rating.util.DataUtil.getPassengerRateCreateEditDtoBuilder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = {"/sql/setup_passenger_rate_table.sql", "/sql/setup_driver_rate_table.sql"},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@ActiveProfiles("test")
public class RatingControllerIntegrationTest {

    @DynamicPropertySource
    static void disableEureka(DynamicPropertyRegistry registry) {
        registry.add("eureka.client.enabled", () -> "false");}

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
    private WebApplicationContext webApplicationContext;

    @BeforeAll
    static void setUp() {
        kafkaContainer.start();
        RideWireMock.wireMockServer.start();
    }

    @AfterAll
    static void tearDown() {
        kafkaContainer.stop();
        RideWireMock.wireMockServer.stop();
    }

    @BeforeEach
    void init() {
        RestAssuredMockMvc.mockMvc(MockMvcBuilders.webAppContextSetup(webApplicationContext).build());
    }

    @Test
    void findAllDriversRates_whenValidParams_thenReturn200() {
        RestAssuredMockMvc
                .given()
                .header(AUTHORIZATION, BEARER + ACCESS_TOKEN)
                .param(PAGE, PAGE_VALUE)
                .param(LIMIT, LIMIT_VALUE)
                .when()
                .get(DRIVER_URL)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("content.size()", equalTo(1));
    }

    @Test
    void findAllPassengersRates_whenValidParams_thenReturn200() {
        RestAssuredMockMvc
                .given()
                .header(AUTHORIZATION, BEARER + ACCESS_TOKEN)
                .when()
                .get(PASSENGER_URL)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("content.size()", equalTo(1));
    }

    @Test
    void findDriverRateById_whenRateIsFound_thenReturn200AndRateReadDto() {
        RestAssuredMockMvc
                .given()
                .header(AUTHORIZATION, BEARER + ACCESS_TOKEN)
                .when()
                .get(DRIVER_URL_WITH_ID, DEFAULT_ID.toString())
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", equalTo(DEFAULT_ID.intValue()))
                .body("rideId", equalTo(DEFAULT_ID.intValue()))
                .body("comment", equalTo(DEFAULT_COMMENT))
                .body("rating", equalTo(DEFAULT_RATE));
    }

    @Test
    void findDriverRateById_whenRateIsNotFound_thenReturn404() {
        RestAssuredMockMvc
                .given()
                .header(AUTHORIZATION, BEARER + ACCESS_TOKEN)
                .when()
                .get(DRIVER_URL_WITH_ID, "2")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body(MESSAGE, equalTo(RATE_NOT_FOUND_EXCEPTION_MESSAGE));
    }

    @Test
    void findPassengerRateById_whenRateIsFound_thenReturn200AndRateReadDto() {
        RestAssuredMockMvc
                .given()
                .header(AUTHORIZATION, BEARER + ACCESS_TOKEN)
                .when()
                .get(PASSENGER_URL_WITH_ID, DEFAULT_ID.toString())
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", equalTo(DEFAULT_ID.intValue()))
                .body("rideId", equalTo(DEFAULT_ID.intValue()))
                .body("comment", equalTo(DEFAULT_COMMENT))
                .body("rating", equalTo(DEFAULT_RATE));
    }

    @Test
    void findPassengerRateById_whenRateIsNotFound_thenReturn404() {
        RestAssuredMockMvc
                .given()
                .header(AUTHORIZATION, BEARER + ACCESS_TOKEN)
                .when()
                .get(PASSENGER_URL_WITH_ID, "2")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body(MESSAGE, equalTo(RATE_NOT_FOUND_EXCEPTION_MESSAGE));
    }

    @Test
    void create_whenRideIsFound_thenReturn200AndRateReadDto() {
        RateCreateEditDto createRate = getPassengerRateCreateEditDtoBuilder().build();

        RideWireMock.getRide();

        RestAssuredMockMvc
                .given()
                .header(AUTHORIZATION, BEARER + ACCESS_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .body(createRate)
                .when()
                .post(URL)
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .body("id", notNullValue());
    }

    @Test
    void create_whenRideIsNotFound_thenReturn404() {
        RateCreateEditDto createRate = getPassengerRateCreateEditDtoBuilder().build();

        RideWireMock.getNonexistentRide();

        RestAssuredMockMvc
                .given()
                .header(AUTHORIZATION, BEARER + ACCESS_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .body(createRate)
                .when()
                .post(URL)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body(MESSAGE, equalTo(RIDE_NOT_FOUND_EXCEPTION_MESSAGE));
    }

    @Test
    void update_whenRideAndRateIsFound_thenReturn200AndRateReadDto() {
        RateCreateEditDto updateRate = getPassengerRateCreateEditDtoBuilder()
                                        .rating(5)
                                        .build();

        RideWireMock.getRide();

        RestAssuredMockMvc
                .given()
                .header(AUTHORIZATION, BEARER + ACCESS_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .body(updateRate)
                .when()
                .put(URL_WITH_ID, DEFAULT_ID.toString())
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", equalTo(DEFAULT_ID.intValue()))
                .body("rideId", equalTo(DEFAULT_ID.intValue()))
                .body("comment", equalTo(DEFAULT_COMMENT))
                .body("rating", equalTo(5));
    }

    @Test
    void update_whenRideIsNotFound_thenReturn404() {
        RateCreateEditDto updateRate = getPassengerRateCreateEditDtoBuilder().build();

        RideWireMock.getNonexistentRide();

        RestAssuredMockMvc
                .given()
                .header(AUTHORIZATION, BEARER + ACCESS_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .body(updateRate)
                .when()
                .put(URL_WITH_ID, DEFAULT_ID.toString())
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body(MESSAGE, equalTo(RIDE_NOT_FOUND_EXCEPTION_MESSAGE));
    }

    @Test
    void update_whenRateIsNotFound_thenReturn404() {
        RateCreateEditDto updateRate = getPassengerRateCreateEditDtoBuilder().build();

        RestAssuredMockMvc
                .given()
                .header(AUTHORIZATION, BEARER + ACCESS_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .body(updateRate)
                .when()
                .put(URL_WITH_ID, "2")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body(MESSAGE, equalTo(RATE_NOT_FOUND_EXCEPTION_MESSAGE));
    }
}
