package org.example.rating.integration;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.example.rating.entity.DriverRate;
import org.example.rating.entity.PassengerRate;
import org.example.rating.entity.enumeration.UserType;
import org.example.rating.repository.DriverRateRepository;
import org.example.rating.repository.PassengerRateRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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

import static org.hamcrest.Matchers.equalTo;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RatingControllerIT {
    private static final String URL = "/api/v1/rates";

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
    }

    @BeforeEach
    void init() {
        RestAssuredMockMvc.mockMvc(MockMvcBuilders.webAppContextSetup(webApplicationContext).build());
        passengerRateRepository.deleteAll();
        jdbcTemplate.execute("ALTER SEQUENCE passenger_rates_id_seq RESTART WITH 1");
        jdbcTemplate.execute("ALTER SEQUENCE rating_id_seq RESTART WITH 1");
        defaultDriverRate = new DriverRate(1L, 1L, "Great!", 5, 1L, UserType.PASSENGER);
        defaultPassengerRate = new PassengerRate(1L, 1L, "Good", 4, 1L, UserType.DRIVER);
        passengerRateRepository.save(defaultPassengerRate);
        driverRateRepository.save(defaultDriverRate);
    }

    @Test
    void findAllDriversRates_whenValidParams_thenReturn200() {
        RestAssuredMockMvc
                .given()
                .param("page", 0)
                .param("limit", 10)
                .when()
                .get(URL + "/driver")
                .then()
                .statusCode(200)
                .body("content.size()", equalTo(1));
    }

    @Test
    void findAllPassengersRates_whenValidParams_thenReturn200() {
        RestAssuredMockMvc
                .when()
                .get(URL + "/passenger")
                .then()
                .statusCode(200)
                .body("content.size()", equalTo(1));
    }

    @Test
    void findDriverRateById_whenRateIsFound_thenReturn200AndRateReadDto() {
        RestAssuredMockMvc
                .when()
                .get(URL + "/driver/1")
                .then()
                .statusCode(200)
                .body("id", equalTo(1))
                .body("rideId", equalTo(1))
                .body("comment", equalTo("Great!"))
                .body("rating", equalTo(5));
    }

    @Test
    void findDriverRateById_whenRateIsNotFound_thenReturn404() {
        RestAssuredMockMvc
                .when()
                .get(URL + "/driver/2")
                .then()
                .statusCode(404)
                .body("message", equalTo("Rate was not found"));
    }

    @Test
    void findPassengerRateById_whenRateIsFound_thenReturn200AndRateReadDto() {
        RestAssuredMockMvc
                .when()
                .get(URL + "/passenger/1")
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
                .get(URL + "/passenger/2")
                .then()
                .statusCode(404)
                .body("message", equalTo("Rate was not found"));
    }
}
