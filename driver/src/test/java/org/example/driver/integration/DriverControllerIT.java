package org.example.driver.integration;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.example.driver.dto.create.DriverCreateEditDto;
import org.example.driver.entity.Car;
import org.example.driver.entity.Driver;
import org.example.driver.entity.enumeration.Gender;
import org.example.driver.repository.CarRepository;
import org.example.driver.repository.DriverRepository;
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

import static org.example.driver.util.DataUtil.DEFAULT_ID;
import static org.example.driver.util.DataUtil.DRIVER_ENTITY;
import static org.example.driver.util.DataUtil.LIMIT;
import static org.example.driver.util.DataUtil.LIMIT_VALUE;
import static org.example.driver.util.DataUtil.PAGE;
import static org.example.driver.util.DataUtil.PAGE_VALUE;
import static org.example.driver.util.DataUtil.URL;
import static org.example.driver.util.DataUtil.URL_WITH_ID;
import static org.example.driver.util.DataUtil.getCar;
import static org.example.driver.util.DataUtil.getDriver;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DriverControllerIT {
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
    private DriverRepository driverRepository;

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private Driver defaultDriver;
    private Car defaultCar;

    @BeforeAll
    static  void setUp() {
        kafkaContainer.start();
    }

    @BeforeEach
    void init() {
        RestAssuredMockMvc.mockMvc(MockMvcBuilders.webAppContextSetup(webApplicationContext).build());
        driverRepository.deleteAll();
        carRepository.deleteAll();
        jdbcTemplate.execute("ALTER SEQUENCE cars_id_seq RESTART WITH 1");
        jdbcTemplate.execute("ALTER SEQUENCE drivers_id_seq RESTART WITH 1");
        defaultCar = getCar().build();
        carRepository.save(defaultCar);
        defaultDriver = getDriver()
                .car(defaultCar)
                .build();
        driverRepository.save(defaultDriver);
    }

    @Test
    void findAll_whenCorrectParams_thenReturn200() {
        RestAssuredMockMvc
                .given()
                .param(PAGE, PAGE_VALUE)
                .param(LIMIT, LIMIT_VALUE)
                .when()
                .get(URL, DRIVER_ENTITY)
                .then()
                .statusCode(200)
                .body("content.size()", equalTo(1));
    }

    @Test
    void findById_whenDriverIsFound_thenReturn200AndDriverReadDto() {
        RestAssuredMockMvc
                .when()
                .get(URL_WITH_ID, DRIVER_ENTITY, DEFAULT_ID.toString())
                .then()
                .statusCode(200)
                .body("id", equalTo(1))
                .body("name", equalTo("test"))
                .body("email", equalTo("test@gmail.com"))
                .body("phone", equalTo("+375297654321"))
                .body("gender", equalTo("MALE"))
                .body("carId", equalTo(1));
    }

    @Test
    void findById_whenDriverIsNotFound_thenReturn404() {
        RestAssuredMockMvc
                .when()
                .get(URL_WITH_ID, DRIVER_ENTITY, "2")
                .then()
                .statusCode(404)
                .body("message", equalTo("Driver was not found"));
    }

    @Test
    void create_whenValidInput_thenReturn200AndDriverReadDto() {
        DriverCreateEditDto createDriver = new DriverCreateEditDto("name", "name@gmail.com",
                "+375291122334", Gender.MALE, 1L);

        RestAssuredMockMvc
                .given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(createDriver)
                .when()
                .post(URL, DRIVER_ENTITY)
                .then()
                .statusCode(201)
                .body("id", notNullValue());
    }

    @Test
    void create_whenEmailIsDuplicated_thenReturn409() {
        DriverCreateEditDto createDriver = new DriverCreateEditDto("test", "test@gmail.com",
                "+375291122334", Gender.MALE, 1L);

        RestAssuredMockMvc
                .given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(createDriver)
                .when()
                .post(URL, DRIVER_ENTITY)
                .then()
                .statusCode(409)
                .body("message", equalTo("Driver with this email already exists"));
    }

    @Test
    void create_whenCarIdNotFound_thenReturn404() {
        DriverCreateEditDto createDriver = new DriverCreateEditDto("name", "name@gmail.com",
                "+375291122334", Gender.MALE, 2L);

        RestAssuredMockMvc
                .given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(createDriver)
                .when()
                .post(URL, DRIVER_ENTITY)
                .then()
                .statusCode(404)
                .body("message", equalTo("Car was not found"));
    }

    @Test
    void update_whenValidInput_thenReturn200AndDriverReadDto() {
        DriverCreateEditDto updateDriver =  new DriverCreateEditDto("testing", "test@gmail.com",
                "+375297654321", Gender.FEMALE, 1L);

        RestAssuredMockMvc
                .given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(updateDriver)
                .when()
                .put(URL_WITH_ID, DRIVER_ENTITY, DEFAULT_ID.toString())
                .then()
                .statusCode(200)
                .body("id", equalTo(1))
                .body("name", equalTo("testing"))
                .body("email", equalTo("test@gmail.com"))
                .body("phone", equalTo("+375297654321"))
                .body("gender", equalTo("FEMALE"))
                .body("carId", equalTo(1));
    }

    @Test
    void update_whenDriverIsNotFound_thenReturn404() {
        DriverCreateEditDto updateDriver =  new DriverCreateEditDto("testing", "testing@gmail.com",
                "+375297654321", Gender.FEMALE, 1L);

        RestAssuredMockMvc
                .given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(updateDriver)
                .when()
                .put(URL_WITH_ID, DRIVER_ENTITY, "2")
                .then()
                .statusCode(404)
                .body("message", equalTo("Driver was not found"));
    }

    @Test
    void update_whenEmailIsDuplicated_thenReturn409() {
        Driver createDriver = new Driver(2L, "name", "name@gmail.com",
                "+375291122334", Gender.MALE, defaultCar, 5.0);
        driverRepository.save(createDriver);

        DriverCreateEditDto updateDriver =  new DriverCreateEditDto("testing", "name@gmail.com",
                "+375297654321", Gender.FEMALE, 1L);

        RestAssuredMockMvc
                .given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(updateDriver)
                .when()
                .put(URL_WITH_ID, DRIVER_ENTITY, DEFAULT_ID.toString())
                .then()
                .statusCode(409)
                .body("message", equalTo("Driver with this email already exists"));
    }

    @Test
    void update_whenCarIsNotFound_thenReturn404() {
        DriverCreateEditDto updateDriver =  new DriverCreateEditDto("testing", "test@gmail.com",
                "+375297654321", Gender.FEMALE, 2L);

        RestAssuredMockMvc
                .given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(updateDriver)
                .when()
                .put(URL_WITH_ID, DRIVER_ENTITY, DEFAULT_ID.toString())
                .then()
                .statusCode(404)
                .body("message", equalTo("Car was not found"));
    }

    @Test
    void safeDelete_whenDriverIsFound_thenReturn204() {
        RestAssuredMockMvc
                .when()
                .delete(URL_WITH_ID, DRIVER_ENTITY, DEFAULT_ID.toString())
                .then()
                .statusCode(204);

        assertThat(0, equalTo(driverRepository.findByIsDeletedFalse(
                PageRequest.of(0, 10)).getNumberOfElements()));
    }

    @Test
    void safeDelete_whenDriverIsNotFound_thenReturn404() {
        RestAssuredMockMvc
                .when()
                .delete(URL_WITH_ID,DRIVER_ENTITY, "2")
                .then()
                .statusCode(404)
                .body("message", equalTo("Driver was not found"));
    }
}
