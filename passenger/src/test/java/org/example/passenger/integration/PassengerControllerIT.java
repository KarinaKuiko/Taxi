package org.example.passenger.integration;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.example.passenger.dto.create.PassengerCreateEditDto;
import org.example.passenger.entity.Passenger;
import org.example.passenger.repository.PassengerRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PassengerControllerIT {
    private static final String URL = "/api/v1/passengers";

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
    private PassengerRepository passengerRepository;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private Passenger defaultPassenger;

    @BeforeAll
    static void setUp() {
        kafkaContainer.start();
    }

    @BeforeEach
    void init() {
        RestAssuredMockMvc.mockMvc(MockMvcBuilders.webAppContextSetup(webApplicationContext).build());
        passengerRepository.deleteAll();
        jdbcTemplate.execute("ALTER SEQUENCE passengers_id_seq RESTART WITH 1");
        defaultPassenger = new Passenger(1L, "name",
                "name@gmail.com", "+375441234567", 5.0);
        passengerRepository.save(defaultPassenger);
    }

    @Test
    void findAll_whenCorrectParams_thenReturn200() {
        RestAssuredMockMvc
                .given()
                .param("page", 0)
                .param("limit", 10)
                .when()
                .get(URL)
                .then()
                .statusCode(200)
                .body("content.size()", equalTo(1));
    }

    @Test
    void findById_whenPassengerIsFound_thenReturn200AndPassengerReadDto() {
        RestAssuredMockMvc
                .when()
                .get(URL + "/1")
                .then()
                .statusCode(200)
                .body("id", equalTo(1))
                .body("name", equalTo("name"))
                .body("email", equalTo("name@gmail.com"))
                .body("phone", equalTo("+375441234567"));
    }

    @Test
    void findById_whenPassengerIsNotFound_thenReturn404() {
        RestAssuredMockMvc
                .when()
                .get(URL + "/2")
                .then()
                .statusCode(404)
                .body("message", equalTo("Passenger was not found"));
    }

    @Test
    void create_whenValidInput_thenReturn201AndPassengerReadDto() {
        PassengerCreateEditDto createPassenger = new PassengerCreateEditDto("test",
                "test@gmail.com", "+375291112223");

        RestAssuredMockMvc
                .given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(createPassenger)
                .when()
                .post(URL)
                .then()
                .statusCode(201)
                .body("id", notNullValue());
    }

    @Test
    void create_whenDuplicatedEmail_thenReturn409() {
        PassengerCreateEditDto createPassenger = new PassengerCreateEditDto("test",
                "name@gmail.com", "+375291112223");

        RestAssuredMockMvc
                .given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(createPassenger)
                .when()
                .post(URL)
                .then()
                .statusCode(409)
                .body("message", equalTo("Passenger with this email already exists"));
    }

    @Test
    void update_whenValidInput_thenReturn200AndPassengerReadDto() {
        Passenger createPassenger = new Passenger(2L, "test",
                "test@gmail.com", "+375291112223", 5.0);
        passengerRepository.save(createPassenger);

        PassengerCreateEditDto updatePassenger = new PassengerCreateEditDto("naming",
                "name@gmail.com", "+375441234567");

        RestAssuredMockMvc
                .given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(updatePassenger)
                .when()
                .put(URL + "/1")
                .then()
                .statusCode(200)
                .body("id", equalTo(1))
                .body("name", equalTo("naming"))
                .body("email", equalTo("name@gmail.com"))
                .body("phone", equalTo("+375441234567"));
    }

    @Test
    void update_whenEmailIsDuplicated_thenReturn409() {
        Passenger createPassenger = new Passenger(2L, "test",
                "test@gmail.com", "+375291112223", 5.0);
        passengerRepository.save(createPassenger);

        PassengerCreateEditDto updatePassenger = new PassengerCreateEditDto("naming",
                "test@gmail.com", "+375441234567");

        RestAssuredMockMvc
                .given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(updatePassenger)
                .when()
                .put(URL + "/1")
                .then()
                .statusCode(409)
                .body("message", equalTo("Passenger with this email already exists"));
    }

    @Test
    void update_whenPassengerIsNotFound_thenReturn404() {
        PassengerCreateEditDto updatePassenger = new PassengerCreateEditDto("naming",
                "test@gmail.com", "+375441234567");

        RestAssuredMockMvc
                .given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(updatePassenger)
                .when()
                .put(URL + "/2")
                .then()
                .statusCode(404)
                .body("message", equalTo("Passenger was not found"));
    }

    @Test
    void safeDelete_whenCarIsFound_thenReturn204() {
        RestAssuredMockMvc
                .when()
                .delete(URL + "/1")
                .then()
                .statusCode(204);

        assertThat(0, equalTo(passengerRepository.findByIsDeletedFalse(
                PageRequest.of(0, 10)).getNumberOfElements()));
    }

    @Test
    void safeDelete_whenCarIsNotFound_thenReturn404() {
        RestAssuredMockMvc
                .when()
                .delete(URL + "/2")
                .then()
                .statusCode(404)
                .body("message", equalTo("Passenger was not found"));
    }
}
