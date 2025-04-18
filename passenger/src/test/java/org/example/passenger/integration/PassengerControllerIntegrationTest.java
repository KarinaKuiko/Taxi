package org.example.passenger.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import dasniko.testcontainers.keycloak.KeycloakContainer;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.example.passenger.dto.create.PassengerCreateEditDto;
import org.example.passenger.entity.Passenger;
import org.example.passenger.repository.PassengerRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;

import static org.example.passenger.util.DataUtil.ACCESS_TOKEN;
import static org.example.passenger.util.DataUtil.AUTHORIZATION;
import static org.example.passenger.util.DataUtil.BEARER;
import static org.example.passenger.util.DataUtil.DEFAULT_EMAIL;
import static org.example.passenger.util.DataUtil.DEFAULT_ID;
import static org.example.passenger.util.DataUtil.DEFAULT_NAME;
import static org.example.passenger.util.DataUtil.DEFAULT_PHONE;
import static org.example.passenger.util.DataUtil.JWT_ISSUER_URI;
import static org.example.passenger.util.DataUtil.JWT_ISSUER_URI_VALUE;
import static org.example.passenger.util.DataUtil.LIMIT;
import static org.example.passenger.util.DataUtil.LIMIT_VALUE;
import static org.example.passenger.util.DataUtil.MESSAGE;
import static org.example.passenger.util.DataUtil.PAGE;
import static org.example.passenger.util.DataUtil.PAGE_VALUE;
import static org.example.passenger.util.DataUtil.PASSENGER_DUPLICATED_EMAIL_MESSAGE;
import static org.example.passenger.util.DataUtil.PASSENGER_NOT_FOUND_EXCEPTION_MESSAGE;
import static org.example.passenger.util.DataUtil.URL;
import static org.example.passenger.util.DataUtil.URL_WITH_ID;
import static org.example.passenger.util.DataUtil.getPassengerBuilder;
import static org.example.passenger.util.DataUtil.getPassengerCreateEditDtoBuilder;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "/sql/setup_passenger_table.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class PassengerControllerIntegrationTest {

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

    @DynamicPropertySource
    static void minioProperties(DynamicPropertyRegistry registry) {
        registry.add("minio.access-key", () -> DEFAULT_NAME);
        registry.add("minio.secret-key", () -> DEFAULT_NAME);
        registry.add("minio.bucket-name", () -> DEFAULT_NAME);
        registry.add("minio.url", () -> DEFAULT_NAME);
    }

    @Container
    public static KafkaContainer kafkaContainer = new KafkaContainer(
            DockerImageName.parse("confluentinc/cp-kafka:latest"));

    @DynamicPropertySource
    static void kafkaProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", kafkaContainer::getBootstrapServers);
    }

    @Container
    static final KeycloakContainer keycloakContainer = new KeycloakContainer();

    @DynamicPropertySource
    static void keycloakProperties(DynamicPropertyRegistry registry) {
        registry.add(JWT_ISSUER_URI, () -> String.format(JWT_ISSUER_URI_VALUE,
                keycloakContainer.getMappedPort(8080)));
    }

    @Autowired
    private PassengerRepository passengerRepository;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    static GenericContainer<?> redisContainer = new GenericContainer<>(
            DockerImageName.parse("redis:7.4.2")).withExposedPorts(6379);

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry r) {
        r.add("spring.data.redis.host", redisContainer::getContainerIpAddress);
        r.add("spring.data.redis.port", redisContainer::getFirstMappedPort);
    }

    @BeforeAll
    static void setUp() {
        kafkaContainer.start();
        redisContainer.start();
    }

    @AfterAll
    static void tearDown() {
        kafkaContainer.stop();
        redisContainer.stop();
    }

    @BeforeEach
    void init() {
        RestAssuredMockMvc.mockMvc(MockMvcBuilders.webAppContextSetup(webApplicationContext)
                        .apply(springSecurity())
                .build());
    }

    @AfterEach
    void tearDownRedis() {
        redisTemplate.getConnectionFactory().getConnection().flushAll();
    }

    @Test
    void findAll_whenCorrectParams_thenReturn200() {
        RestAssuredMockMvc
                .given()
                .header(AUTHORIZATION, BEARER + keycloakAuthorization())
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
                .given()
                .header(AUTHORIZATION, BEARER + keycloakAuthorization())
                .when()
                .get(URL_WITH_ID, DEFAULT_ID.toString())
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", equalTo(DEFAULT_ID.intValue()))
                .body("firstName", equalTo(DEFAULT_NAME))
                .body("lastName", equalTo(DEFAULT_NAME))
                .body("email", equalTo(DEFAULT_EMAIL))
                .body("phone", equalTo(DEFAULT_PHONE));
    }

    @Test
    void findById_whenPassengerIsNotFound_thenReturn404() {
        RestAssuredMockMvc
                .given()
                .header(AUTHORIZATION, BEARER + keycloakAuthorization())
                .when()
                .get(URL_WITH_ID, "2")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body(MESSAGE, equalTo(PASSENGER_NOT_FOUND_EXCEPTION_MESSAGE));
    }

    @Test
    void create_whenValidInput_thenReturn201AndPassengerReadDto() throws Exception {
        PassengerCreateEditDto createPassenger = getPassengerCreateEditDtoBuilder()
                                                .email("test@gmail.com")
                                                .build();

        RestAssuredMockMvc
                .given()
                .header(AUTHORIZATION, BEARER + keycloakAuthorization())
                .multiPart("dto", objectMapper.writeValueAsString(createPassenger), MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post(URL)
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .body("id", notNullValue());
    }

    @Test
    void create_whenDuplicatedEmail_thenReturn409() throws Exception {
        PassengerCreateEditDto createPassenger = getPassengerCreateEditDtoBuilder().build();

        RestAssuredMockMvc
                .given()
                .header(AUTHORIZATION, BEARER + keycloakAuthorization())
                .multiPart("dto", objectMapper.writeValueAsString(createPassenger), MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post(URL)
                .then()
                .statusCode(HttpStatus.CONFLICT.value())
                .body(MESSAGE, equalTo(PASSENGER_DUPLICATED_EMAIL_MESSAGE));
    }

    @Test
    void update_whenValidInput_thenReturn200AndPassengerReadDto() throws Exception {
        PassengerCreateEditDto updatePassenger = getPassengerCreateEditDtoBuilder()
                                                    .firstName("naming")
                                                    .build();

        RestAssuredMockMvc
                .given()
                .header(AUTHORIZATION, BEARER + keycloakAuthorization())
                .multiPart("dto", objectMapper.writeValueAsString(updatePassenger), MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put(URL_WITH_ID, DEFAULT_ID.toString())
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", equalTo(DEFAULT_ID.intValue()))
                .body("firstName", equalTo("naming"))
                .body("lastName", equalTo(DEFAULT_NAME))
                .body("email", equalTo(DEFAULT_EMAIL))
                .body("phone", equalTo(DEFAULT_PHONE));
    }

    @Test
    void update_whenEmailIsDuplicated_thenReturn409() throws Exception {
        Passenger createPassenger = getPassengerBuilder()
                                    .id(2L)
                                    .email("test@gmail.com")
                                    .build();
        passengerRepository.save(createPassenger);

        PassengerCreateEditDto updatePassenger = getPassengerCreateEditDtoBuilder()
                                                    .firstName("naming")
                                                    .email("test@gmail.com")
                                                    .build();

        RestAssuredMockMvc
                .given()
                .header(AUTHORIZATION, BEARER + keycloakAuthorization())
                .multiPart("dto", objectMapper.writeValueAsString(updatePassenger), MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put(URL_WITH_ID, DEFAULT_ID.toString())
                .then()
                .statusCode(HttpStatus.CONFLICT.value())
                .body(MESSAGE, equalTo(PASSENGER_DUPLICATED_EMAIL_MESSAGE));
    }

    @Test
    void update_whenPassengerIsNotFound_thenReturn404() throws Exception {
        PassengerCreateEditDto updatePassenger = getPassengerCreateEditDtoBuilder().build();

        RestAssuredMockMvc
                .given()
                .header(AUTHORIZATION, BEARER + keycloakAuthorization())
                .multiPart("dto", objectMapper.writeValueAsString(updatePassenger), MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put(URL_WITH_ID, "2")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body(MESSAGE, equalTo(PASSENGER_NOT_FOUND_EXCEPTION_MESSAGE));
    }

    @Test
    void safeDelete_whenCarIsFound_thenReturn204() {
        RestAssuredMockMvc
                .given()
                .header(AUTHORIZATION, BEARER + keycloakAuthorization())
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
                .given()
                .header(AUTHORIZATION, BEARER + keycloakAuthorization())
                .when()
                .delete(URL_WITH_ID,"2")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body(MESSAGE, equalTo(PASSENGER_NOT_FOUND_EXCEPTION_MESSAGE));
    }

    private String keycloakAuthorization() {
        return keycloakContainer.getKeycloakAdminClient()
                .tokenManager()
                .getAccessToken()
                .getToken();
    }
}
