package org.example.driver.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import dasniko.testcontainers.keycloak.KeycloakContainer;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.example.driver.dto.create.CarCreateEditDto;
import org.example.driver.dto.create.DriverCreateEditDto;
import org.example.driver.entity.enumeration.Gender;
import org.example.driver.repository.DriverRepository;
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

import static org.example.driver.util.DataUtil.AUTHORIZATION;
import static org.example.driver.util.DataUtil.BEARER;
import static org.example.driver.util.DataUtil.DEFAULT_EMAIL;
import static org.example.driver.util.DataUtil.DEFAULT_ID;
import static org.example.driver.util.DataUtil.DEFAULT_NAME;
import static org.example.driver.util.DataUtil.DEFAULT_PHONE;
import static org.example.driver.util.DataUtil.DRIVER_DUPLICATED_EMAIL;
import static org.example.driver.util.DataUtil.DRIVER_ENTITY;
import static org.example.driver.util.DataUtil.DRIVER_NOT_FOUND;
import static org.example.driver.util.DataUtil.JWT_ISSUER_URI;
import static org.example.driver.util.DataUtil.JWT_ISSUER_URI_VALUE;
import static org.example.driver.util.DataUtil.LIMIT;
import static org.example.driver.util.DataUtil.LIMIT_VALUE;
import static org.example.driver.util.DataUtil.MESSAGE;
import static org.example.driver.util.DataUtil.PAGE;
import static org.example.driver.util.DataUtil.PAGE_VALUE;
import static org.example.driver.util.DataUtil.URL;
import static org.example.driver.util.DataUtil.URL_WITH_ID;
import static org.example.driver.util.DataUtil.getCarCreateEditDtoBuilder;
import static org.example.driver.util.DataUtil.getDriverCreateEditDtoBuilder;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = {"/sql/setup_car_table.sql", "/sql/setup_driver_table.sql"},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class DriverControllerIntegrationTest {

    @DynamicPropertySource
    static void disableEureka(DynamicPropertyRegistry registry) {
        registry.add("eureka.client.enabled", () -> "false");
    }

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
    private DriverRepository driverRepository;

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
                .get(URL, DRIVER_ENTITY)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("content.size()", equalTo(2));
    }

    @Test
    void findById_whenDriverIsFound_thenReturn200AndDriverReadDto() {
        RestAssuredMockMvc
                .given()
                .header(AUTHORIZATION, BEARER + keycloakAuthorization())
                .when()
                .get(URL_WITH_ID, DRIVER_ENTITY, DEFAULT_ID.toString())
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", equalTo(DEFAULT_ID.intValue()))
                .body("firstName", equalTo(DEFAULT_NAME))
                .body("lastName", equalTo(DEFAULT_NAME))
                .body("email", equalTo(DEFAULT_EMAIL))
                .body("phone", equalTo(DEFAULT_PHONE))
                .body("gender", equalTo(Gender.MALE.name()))
                .body("carId", equalTo(DEFAULT_ID.intValue()));
    }

    @Test
    void findById_whenDriverIsNotFound_thenReturn404() {
        RestAssuredMockMvc
                .given()
                .header(AUTHORIZATION, BEARER + keycloakAuthorization())
                .when()
                .get(URL_WITH_ID, DRIVER_ENTITY, "10")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body(MESSAGE, equalTo(DRIVER_NOT_FOUND));
    }

    @Test
    void create_whenValidInput_thenReturn201AndDriverReadDto() throws Exception {
        DriverCreateEditDto createDriver = getDriverCreateEditDtoBuilder()
                .email("driver@gmail.com")
                .build();

        RestAssuredMockMvc
                .given()
                .header(AUTHORIZATION, BEARER + keycloakAuthorization())
                .multiPart("dto", objectMapper.writeValueAsString(createDriver), MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post(URL, DRIVER_ENTITY)
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .body("id", notNullValue());
    }

    @Test
    void create_whenEmailIsDuplicated_thenReturn409() throws Exception {
        DriverCreateEditDto createDriver = getDriverCreateEditDtoBuilder().build();

        RestAssuredMockMvc
                .given()
                .header(AUTHORIZATION, BEARER + keycloakAuthorization())
                .multiPart("dto", objectMapper.writeValueAsString(createDriver), MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post(URL, DRIVER_ENTITY)
                .then()
                .statusCode(HttpStatus.CONFLICT.value())
                .body(MESSAGE, equalTo(DRIVER_DUPLICATED_EMAIL));
    }

    @Test
    void create_whenCarNumberNotFound_thenReturn201() throws Exception {
        CarCreateEditDto createCar = getCarCreateEditDtoBuilder()
                .number("AB123CL")
                .build();
        DriverCreateEditDto createDriver = getDriverCreateEditDtoBuilder()
                .email("driver@gmail.com")
                .carCreateEditDto(createCar)
                .build();

        RestAssuredMockMvc
                .given()
                .header(AUTHORIZATION, BEARER + keycloakAuthorization())
                .multiPart("dto", objectMapper.writeValueAsString(createDriver), MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post(URL, DRIVER_ENTITY)
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .body("id", notNullValue());
    }

    @Test
    void update_whenValidInput_thenReturn200AndDriverReadDto() throws Exception {
        DriverCreateEditDto updateDriver = getDriverCreateEditDtoBuilder()
                .firstName("testing")
                .gender(Gender.FEMALE)
                .build();

        RestAssuredMockMvc
                .given()
                .header(AUTHORIZATION, BEARER + keycloakAuthorization())
                .multiPart("dto", objectMapper.writeValueAsString(updateDriver), MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put(URL_WITH_ID, DRIVER_ENTITY, DEFAULT_ID.toString())
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", equalTo(DEFAULT_ID.intValue()))
                .body("firstName", equalTo("testing"))
                .body("lastName", equalTo("test"))
                .body("email", equalTo(DEFAULT_EMAIL))
                .body("phone", equalTo(DEFAULT_PHONE))
                .body("gender", equalTo(Gender.FEMALE.name()))
                .body("carId", equalTo(DEFAULT_ID.intValue()));
    }

    @Test
    void update_whenDriverIsNotFound_thenReturn404() throws Exception {
        DriverCreateEditDto updateDriver = getDriverCreateEditDtoBuilder().build();

        RestAssuredMockMvc
                .given()
                .header(AUTHORIZATION, BEARER + keycloakAuthorization())
                .multiPart("dto", objectMapper.writeValueAsString(updateDriver), MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put(URL_WITH_ID, DRIVER_ENTITY, "10")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body(MESSAGE, equalTo(DRIVER_NOT_FOUND));
    }

    @Test
    void update_whenEmailIsDuplicated_thenReturn409() throws Exception {
        DriverCreateEditDto updateDriver = getDriverCreateEditDtoBuilder()
                .firstName("testing")
                .email("name@gmail.com")
                .build();

        RestAssuredMockMvc
                .given()
                .header(AUTHORIZATION, BEARER + keycloakAuthorization())
                .multiPart("dto", objectMapper.writeValueAsString(updateDriver), MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put(URL_WITH_ID, DRIVER_ENTITY, DEFAULT_ID.toString())
                .then()
                .statusCode(HttpStatus.CONFLICT.value())
                .body(MESSAGE, equalTo(DRIVER_DUPLICATED_EMAIL));
    }

    @Test
    void update_whenCarIsNotFound_thenReturn200() throws Exception {
        CarCreateEditDto createCar = getCarCreateEditDtoBuilder()
                .number("AB123CL")
                .build();
        DriverCreateEditDto updateDriver = getDriverCreateEditDtoBuilder()
                .carCreateEditDto(createCar)
                .build();

        RestAssuredMockMvc
                .given()
                .header(AUTHORIZATION, BEARER + keycloakAuthorization())
                .multiPart("dto", objectMapper.writeValueAsString(updateDriver), MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put(URL_WITH_ID, DRIVER_ENTITY, DEFAULT_ID.toString())
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", notNullValue());
    }

    @Test
    void safeDelete_whenDriverIsFound_thenReturn204() {
        RestAssuredMockMvc
                .given()
                .header(AUTHORIZATION, BEARER + keycloakAuthorization())
                .when()
                .delete(URL_WITH_ID, DRIVER_ENTITY, DEFAULT_ID.toString())
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        assertThat(1, equalTo(driverRepository.findByIsDeletedFalse(
                PageRequest.of(PAGE_VALUE, LIMIT_VALUE)).getNumberOfElements()));
    }

    @Test
    void safeDelete_whenDriverIsNotFound_thenReturn404() {
        RestAssuredMockMvc
                .given()
                .header(AUTHORIZATION, BEARER + keycloakAuthorization())
                .when()
                .delete(URL_WITH_ID, DRIVER_ENTITY, "10")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body(MESSAGE, equalTo(DRIVER_NOT_FOUND));
    }

    private String keycloakAuthorization() {
        return keycloakContainer.getKeycloakAdminClient()
                .tokenManager()
                .getAccessToken()
                .getToken();
    }
}
