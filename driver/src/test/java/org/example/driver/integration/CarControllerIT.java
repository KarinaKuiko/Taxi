package org.example.driver.integration;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.example.driver.dto.create.CarCreateEditDto;
import org.example.driver.entity.Car;
import org.example.driver.repository.CarRepository;
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

import java.util.List;

import static org.example.driver.util.DataUtil.CAR_ENTITY;
import static org.example.driver.util.DataUtil.DEFAULT_ID;
import static org.example.driver.util.DataUtil.LIMIT;
import static org.example.driver.util.DataUtil.LIMIT_VALUE;
import static org.example.driver.util.DataUtil.PAGE;
import static org.example.driver.util.DataUtil.PAGE_VALUE;
import static org.example.driver.util.DataUtil.URL;
import static org.example.driver.util.DataUtil.URL_WITH_ID;
import static org.example.driver.util.DataUtil.getCar;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CarControllerIT {

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
    private CarRepository carRepository;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private Car defaultCar;

    @BeforeAll
    static void setUp() {
        kafkaContainer.start();
    }

    @BeforeEach
    void init() {
        RestAssuredMockMvc.mockMvc(MockMvcBuilders.webAppContextSetup(webApplicationContext).build());
        carRepository.deleteAll();
        jdbcTemplate.execute("ALTER SEQUENCE cars_id_seq RESTART WITH 1");
        defaultCar = getCar().build();
        carRepository.save(defaultCar);
    }

    @Test
    void findAll_whenCorrectParams_thenReturn200() {
        RestAssuredMockMvc
                .given()
                .param(PAGE, PAGE_VALUE)
                .param(LIMIT, LIMIT_VALUE)
                .when()
                .get(URL, CAR_ENTITY)
                .then()
                .statusCode(200)
                .body("content.size()", equalTo(1));
    }

    @Test
    void findById_whenCarIsFound_thenReturn200AndCarReadDto() {
        RestAssuredMockMvc
                .when()
                .get(URL_WITH_ID, CAR_ENTITY, DEFAULT_ID.toString())
                .then()
                .statusCode(200)
                .body("id", equalTo(1))
                .body("color", equalTo("red"))
                .body("brand", equalTo("BMW"))
                .body("number", equalTo("AB123CD"))
                .body("year", equalTo(2023));
    }

    @Test
    void findById_whenCarIsNotFound_thenReturn404() {
        RestAssuredMockMvc
                .when()
                .get(URL_WITH_ID, CAR_ENTITY, "10")
                .then()
                .statusCode(404)
                .body("message", equalTo("Car was not found"));
    }

    @Test
    void create_whenValidInput_thenReturn200AndCarReadDto() {
        CarCreateEditDto createCar = new CarCreateEditDto("yellow", "Peugeot", "LK124AS", 2014);

        RestAssuredMockMvc
                .given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(createCar)
                .when()
                .post(URL, CAR_ENTITY)
                .then()
                .statusCode(201)
                .body("id", notNullValue());
    }

    @Test
    void create_whenDuplicatedNumber_thenReturn409() {
        CarCreateEditDto createCar = new CarCreateEditDto("red", "BMW", "AB123CD", 2023);

        RestAssuredMockMvc
                .given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(createCar)
                .when()
                .post(URL, CAR_ENTITY)
                .then()
                .statusCode(409)
                .body("message", equalTo("Car with this number already exists"));
    }

    @Test
    void update_whenValidInput_thenReturn200AndCarReadDto() {
        CarCreateEditDto updateCar = new CarCreateEditDto("yellow", "BMW", "AB123CD", 2020);

        RestAssuredMockMvc
                .given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(updateCar)
                .when()
                .put(URL_WITH_ID, CAR_ENTITY, DEFAULT_ID.toString())
                .then()
                .statusCode(200)
                .body("id", equalTo(1))
                .body("color", equalTo("yellow"))
                .body("brand", equalTo("BMW"))
                .body("number", equalTo("AB123CD"))
                .body("year", equalTo(2020));
    }

    @Test
    void update_whenCarNumberIsDuplicatedAndDifferentIds_thenThrowDuplicatedCarNumberException() {
        Car createCar = new Car(2L, "yellow", "Peugeot", "LK124AS", 2014, List.of());
        carRepository.save(createCar);

        CarCreateEditDto updateCar = new CarCreateEditDto("yellow", "BMW", "LK124AS", 2020);

        RestAssuredMockMvc
                .given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(updateCar)
                .when()
                .put(URL_WITH_ID, CAR_ENTITY, DEFAULT_ID.toString())
                .then()
                .statusCode(409)
                .body("message", equalTo("Car with this number already exists"));
    }

    @Test
    void update_whenCarNotFound_thenThrowCarNotFoundException() {
        CarCreateEditDto updateCar = new CarCreateEditDto("yellow", "BMW", "LK124AS", 2020);

        RestAssuredMockMvc
                .given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(updateCar)
                .when()
                .put(URL_WITH_ID, CAR_ENTITY, "2")
                .then()
                .statusCode(404)
                .body("message", equalTo("Car was not found"));
    }

    @Test
    void safeDelete_whenCarIsFound_thenReturn204() {
        RestAssuredMockMvc
                .when()
                .delete(URL_WITH_ID, CAR_ENTITY, DEFAULT_ID.toString())
                .then()
                .statusCode(204);

        assertThat(0, equalTo(carRepository.findByIsDeletedFalse(
                PageRequest.of(PAGE_VALUE, LIMIT_VALUE)).getNumberOfElements()));
    }

    @Test
    void safeDelete_whenCarIsNotFound_thenReturn404() {
        RestAssuredMockMvc
                .when()
                .delete(URL_WITH_ID, CAR_ENTITY, "2")
                .then()
                .statusCode(404)
                .body("message", equalTo("Car was not found"));
    }
}
