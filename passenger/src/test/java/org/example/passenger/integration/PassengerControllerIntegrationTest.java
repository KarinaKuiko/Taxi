package org.example.passenger.integration;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.example.passenger.dto.create.PassengerCreateEditDto;
import org.example.passenger.entity.Passenger;
import org.example.passenger.repository.PassengerRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
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

import static org.example.passenger.util.DataUtil.DEFAULT_EMAIL;
import static org.example.passenger.util.DataUtil.DEFAULT_ID;
import static org.example.passenger.util.DataUtil.DEFAULT_NAME;
import static org.example.passenger.util.DataUtil.DEFAULT_PHONE;
import static org.example.passenger.util.DataUtil.LIMIT;
import static org.example.passenger.util.DataUtil.LIMIT_VALUE;
import static org.example.passenger.util.DataUtil.MESSAGE;
import static org.example.passenger.util.DataUtil.PAGE;
import static org.example.passenger.util.DataUtil.PAGE_VALUE;
import static org.example.passenger.util.DataUtil.URL;
import static org.example.passenger.util.DataUtil.URL_WITH_ID;
import static org.example.passenger.util.DataUtil.getPassengerBuilder;
import static org.example.passenger.util.DataUtil.getPassengerCreateEditDtoBuilder;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "/setup_passenger_table.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class PassengerControllerIntegrationTest {

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

    @BeforeAll
    static void setUp() {
        kafkaContainer.start();
    }

    @AfterAll
    static void tearDown() {
        kafkaContainer.stop();
    }

    @BeforeEach
    void init() {
        RestAssuredMockMvc.mockMvc(MockMvcBuilders.webAppContextSetup(webApplicationContext).build());
    }

    @Test
    void findAll_whenCorrectParams_thenReturn200() {
        RestAssuredMockMvc
                .given()
                .param(PAGE, PAGE_VALUE)
                .param(LIMIT, LIMIT_VALUE)
                .when()
                .get(URL)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("content.size()", equalTo(1));
    }

    @Test
    void findById_whenPassengerIsFound_thenReturn200AndPassengerReadDto() {
        RestAssuredMockMvc
                .when()
                .get(URL_WITH_ID, DEFAULT_ID.toString())
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", equalTo(DEFAULT_ID.intValue()))
                .body("name", equalTo(DEFAULT_NAME))
                .body("email", equalTo(DEFAULT_EMAIL))
                .body("phone", equalTo(DEFAULT_PHONE));
    }

    @Test
    void findById_whenPassengerIsNotFound_thenReturn404() {
        RestAssuredMockMvc
                .when()
                .get(URL_WITH_ID, "2")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body(MESSAGE, equalTo("Passenger was not found"));
    }

    @Test
    void create_whenValidInput_thenReturn201AndPassengerReadDto() {
        PassengerCreateEditDto createPassenger = getPassengerCreateEditDtoBuilder()
                                                .email("test@gmail.com")
                                                .build();

        RestAssuredMockMvc
                .given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(createPassenger)
                .when()
                .post(URL)
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .body("id", notNullValue());
    }

    @Test
    void create_whenDuplicatedEmail_thenReturn409() {
        PassengerCreateEditDto createPassenger = getPassengerCreateEditDtoBuilder().build();

        RestAssuredMockMvc
                .given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(createPassenger)
                .when()
                .post(URL)
                .then()
                .statusCode(HttpStatus.CONFLICT.value())
                .body(MESSAGE, equalTo("Passenger with this email already exists"));
    }

    @Test
    void update_whenValidInput_thenReturn200AndPassengerReadDto() {
        PassengerCreateEditDto updatePassenger = getPassengerCreateEditDtoBuilder()
                                                    .name("naming")
                                                    .build();

        RestAssuredMockMvc
                .given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(updatePassenger)
                .when()
                .put(URL_WITH_ID, DEFAULT_ID.toString())
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", equalTo(DEFAULT_ID.intValue()))
                .body("name", equalTo("naming"))
                .body("email", equalTo(DEFAULT_EMAIL))
                .body("phone", equalTo(DEFAULT_PHONE));
    }

    @Test
    void update_whenEmailIsDuplicated_thenReturn409() {
        Passenger createPassenger = getPassengerBuilder()
                                    .id(2L)
                                    .email("test@gmail.com")
                                    .build();
        passengerRepository.save(createPassenger);

        PassengerCreateEditDto updatePassenger = getPassengerCreateEditDtoBuilder()
                                                    .name("naming")
                                                    .email("test@gmail.com")
                                                    .build();

        RestAssuredMockMvc
                .given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(updatePassenger)
                .when()
                .put(URL_WITH_ID, DEFAULT_ID.toString())
                .then()
                .statusCode(HttpStatus.CONFLICT.value())
                .body(MESSAGE, equalTo("Passenger with this email already exists"));
    }

    @Test
    void update_whenPassengerIsNotFound_thenReturn404() {
        PassengerCreateEditDto updatePassenger = getPassengerCreateEditDtoBuilder().build();

        RestAssuredMockMvc
                .given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(updatePassenger)
                .when()
                .put(URL_WITH_ID, "2")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body(MESSAGE, equalTo("Passenger was not found"));
    }

    @Test
    void safeDelete_whenCarIsFound_thenReturn204() {
        RestAssuredMockMvc
                .when()
                .delete(URL_WITH_ID, DEFAULT_ID.toString())
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        assertThat(0, equalTo(passengerRepository.findByIsDeletedFalse(
                PageRequest.of(PAGE_VALUE, LIMIT_VALUE)).getNumberOfElements()));
    }

    @Test
    void safeDelete_whenCarIsNotFound_thenReturn404() {
        RestAssuredMockMvc
                .when()
                .delete(URL_WITH_ID,"2")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body(MESSAGE, equalTo("Passenger was not found"));
    }
}
