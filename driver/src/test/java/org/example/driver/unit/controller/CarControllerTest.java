package org.example.driver.unit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.driver.config.MessageSourceConfig;
import org.example.driver.controller.CarController;
import org.example.driver.dto.create.CarCreateEditDto;
import org.example.driver.dto.read.CarReadDto;
import org.example.driver.dto.read.ValidationResponse;
import org.example.driver.exception.violation.Violation;
import org.example.driver.service.CarService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.example.driver.util.DataUtil.CAR_ENTITY;
import static org.example.driver.util.DataUtil.DEFAULT_BRAND;
import static org.example.driver.util.DataUtil.DEFAULT_COLOR;
import static org.example.driver.util.DataUtil.DEFAULT_ID;
import static org.example.driver.util.DataUtil.DEFAULT_NUMBER;
import static org.example.driver.util.DataUtil.DEFAULT_YEAR;
import static org.example.driver.util.DataUtil.LIMIT;
import static org.example.driver.util.DataUtil.LIMIT_VALUE;
import static org.example.driver.util.DataUtil.PAGE;
import static org.example.driver.util.DataUtil.PAGE_VALUE;
import static org.example.driver.util.DataUtil.URL;
import static org.example.driver.util.DataUtil.URL_WITH_ID;
import static org.example.driver.util.DataUtil.getCarCreateEditDtoBuilder;
import static org.example.driver.util.DataUtil.getCarReadDtoBuilder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CarController.class)
@Import(MessageSourceConfig.class)
class CarControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CarService carService;

    @Nested
    @DisplayName("Find all tests")
    public class findAllTests {
        @Test
        void findAll_whenVerifyingRequestMatchingWithoutParams_thenReturn200() throws Exception {
            CarReadDto readCar = getCarReadDtoBuilder().build();
            Page<CarReadDto> carPage = new PageImpl<>(List.of(readCar), PageRequest.of(PAGE_VALUE, LIMIT_VALUE), 1);

            when(carService.findAll(PAGE_VALUE, LIMIT_VALUE)).thenReturn(carPage);

            mockMvc.perform(get(URL, CAR_ENTITY))
                    .andExpect(status().isOk());
        }

        @Test
        void findAll_whenCorrectParams_thenReturn200() throws Exception {
            CarReadDto readCar = getCarReadDtoBuilder().build();
            Page<CarReadDto> carPage = new PageImpl<>(List.of(readCar), PageRequest.of(PAGE_VALUE, LIMIT_VALUE), 1);

            when(carService.findAll(PAGE_VALUE, LIMIT_VALUE)).thenReturn(carPage);

            mockMvc.perform(get(URL, CAR_ENTITY)
                            .param(PAGE, PAGE_VALUE.toString())
                            .param(LIMIT, LIMIT_VALUE.toString()))
                    .andExpect(status().isOk());
        }

        @Test
        void findAll_whenLimitIsGreaterThanMax_thenReturn400AndValidationResponse() throws Exception {
            MvcResult mvcResult = mockMvc.perform(get(URL, CAR_ENTITY)
                            .param(PAGE, PAGE_VALUE.toString())
                            .param(LIMIT, "101"))
                    .andExpect(status().isBadRequest())
                    .andReturn();

            ValidationResponse expextedValidationResponse = new ValidationResponse(
                    List.of(new Violation(LIMIT, "must be less than or equal to 100")));
            String actualResponse = mvcResult.getResponse().getContentAsString();

            assertThat(actualResponse).isEqualToIgnoringWhitespace(
                    objectMapper.writeValueAsString(expextedValidationResponse));
        }

        @Test
        void findAll_whenLimitIsLessThanMin_thenReturn400AndValidationResponse() throws Exception {
            MvcResult mvcResult = mockMvc.perform(get(URL, CAR_ENTITY)
                            .param(PAGE, PAGE_VALUE.toString())
                            .param(LIMIT, "0"))
                    .andExpect(status().isBadRequest())
                    .andReturn();

            ValidationResponse expextedValidationResponse = new ValidationResponse(
                    List.of(new Violation(LIMIT, "must be greater than or equal to 1")));
            String actualResponse = mvcResult.getResponse().getContentAsString();

            assertThat(actualResponse).isEqualToIgnoringWhitespace(
                    objectMapper.writeValueAsString(expextedValidationResponse));
        }
    }

    @Nested
    @DisplayName("Find by id tests")
    public class findByIdTests {
        @Test
        void findById_whenVerifyingRequestMatching_thenReturn200() throws Exception {
            CarReadDto readCar = getCarReadDtoBuilder().build();

            when(carService.findById(DEFAULT_ID)).thenReturn(readCar);

            MvcResult mvcResult = mockMvc.perform(get(URL_WITH_ID, CAR_ENTITY, DEFAULT_ID))
                    .andExpect(status().isOk())
                    .andReturn();

            String actual = mvcResult.getResponse().getContentAsString();

            assertThat(actual).isEqualToIgnoringWhitespace(
                    objectMapper.writeValueAsString(readCar));
        }
    }

    @Nested
    @DisplayName("Create tests")
    public class createTests {
        @Test
        void create_whenVerifyingRequestMatching_thenReturn200() throws Exception {
            CarCreateEditDto createCar = getCarCreateEditDtoBuilder().build();

            mockMvc.perform(post(URL, CAR_ENTITY)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createCar)))
                    .andExpect(status().isCreated())
                    .andReturn();

            verify(carService, times(1)).create(createCar);
        }

        @Test
        void create_whenValidInput_thenMapsToBusinessModel() throws Exception {
            CarCreateEditDto createCar = getCarCreateEditDtoBuilder().build();

            mockMvc.perform(post(URL, CAR_ENTITY)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createCar)))
                    .andExpect(status().isCreated())
                    .andReturn();

            ArgumentCaptor<CarCreateEditDto> carCaptor = ArgumentCaptor.forClass(CarCreateEditDto.class);

            verify(carService, times(1)).create(carCaptor.capture());
            assertThat(carCaptor.getValue().color()).isEqualTo(DEFAULT_COLOR);
            assertThat(carCaptor.getValue().brand()).isEqualTo(DEFAULT_BRAND);
            assertThat(carCaptor.getValue().number()).isEqualTo(DEFAULT_NUMBER);
            assertThat(carCaptor.getValue().year()).isEqualTo(DEFAULT_YEAR);
        }

        @Test
        void create_whenValidInput_thenReturn201AndCarReadDto() throws Exception {
            CarReadDto readCar = getCarReadDtoBuilder().build();
            CarCreateEditDto createCar = getCarCreateEditDtoBuilder().build();

            when(carService.create(createCar)).thenReturn(readCar);

            MvcResult mvcResult = mockMvc.perform(post(URL, CAR_ENTITY)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createCar)))
                    .andExpect(status().isCreated())
                    .andReturn();

            String actualResponseBody = mvcResult.getResponse().getContentAsString();
            assertThat(actualResponseBody).isEqualToIgnoringWhitespace(
                    objectMapper.writeValueAsString(readCar));
        }

        @Test
        void create_whenInvalidInput_thenReturn400AndValidationResponse() throws Exception {
            CarCreateEditDto createCar = getCarCreateEditDtoBuilder()
                        .color(null)
                        .brand(null)
                        .number(null)
                        .year(2026)
                        .build();

            MvcResult mvcResult = mockMvc.perform(post(URL, CAR_ENTITY)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createCar)))
                    .andExpect(status().isBadRequest())
                    .andReturn();

            ValidationResponse expectedValidationResponse = new ValidationResponse(
                    List.of(new Violation("color", "Color cannot be blank"),
                            new Violation("brand", "Brand cannot be blank"),
                            new Violation("number", "Number cannot be blank"),
                            new Violation("year", "Year cannot be less 1980 and more 2025")));
            ValidationResponse actualResponse = objectMapper.readValue(
                    mvcResult.getResponse().getContentAsString(), ValidationResponse.class);

            assertThat(actualResponse.violations()).containsExactlyInAnyOrderElementsOf(
                    expectedValidationResponse.violations());
        }

        @Test
        void create_whenInvalidNumberPattern_thenReturn400AndValidationResponse() throws Exception {
            CarCreateEditDto createCar = getCarCreateEditDtoBuilder()
                        .number("45sssa")
                        .build();

            MvcResult mvcResult = mockMvc.perform(post(URL, CAR_ENTITY)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createCar)))
                    .andExpect(status().isBadRequest())
                    .andReturn();

            ValidationResponse expectedValidationResponse = new ValidationResponse(
                    List.of(new Violation("number", "Invalid number. Possible form: AB123CD")));
            ValidationResponse actualResponse = objectMapper.readValue(
                    mvcResult.getResponse().getContentAsString(), ValidationResponse.class);

            assertThat(actualResponse.violations()).containsExactlyInAnyOrderElementsOf(
                    expectedValidationResponse.violations());
        }

        @Test
        void create_whenColorIsNull_thenReturn400AndValidationResponse() throws Exception {
            CarCreateEditDto createCar = getCarCreateEditDtoBuilder()
                        .color(null)
                        .build();

            MvcResult mvcResult = mockMvc.perform(post(URL, CAR_ENTITY)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createCar)))
                    .andExpect(status().isBadRequest())
                    .andReturn();

            ValidationResponse expectedValidationResponse = new ValidationResponse(
                    List.of(new Violation("color", "Color cannot be blank")));
            ValidationResponse actualResponse = objectMapper.readValue(
                    mvcResult.getResponse().getContentAsString(), ValidationResponse.class);

            assertThat(actualResponse.violations()).containsExactlyInAnyOrderElementsOf(
                    expectedValidationResponse.violations());
        }

        @Test
        void create_whenBrandIsNull_thenReturn400AndValidationResponse() throws Exception {
            CarCreateEditDto createCar = getCarCreateEditDtoBuilder()
                        .brand(null)
                        .build();

            MvcResult mvcResult = mockMvc.perform(post(URL, CAR_ENTITY)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createCar)))
                    .andExpect(status().isBadRequest())
                    .andReturn();

            ValidationResponse expectedValidationResponse = new ValidationResponse(
                    List.of(new Violation("brand", "Brand cannot be blank")));
            ValidationResponse actualResponse = objectMapper.readValue(
                    mvcResult.getResponse().getContentAsString(), ValidationResponse.class);

            assertThat(actualResponse.violations()).containsExactlyInAnyOrderElementsOf(
                    expectedValidationResponse.violations());
        }

        @Test
        void create_whenNumberIsNull_thenReturn400AndValidationResponse() throws Exception {
            CarCreateEditDto createCar = getCarCreateEditDtoBuilder()
                        .number(null)
                        .build();

            MvcResult mvcResult = mockMvc.perform(post(URL, CAR_ENTITY)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createCar)))
                    .andExpect(status().isBadRequest())
                    .andReturn();

            ValidationResponse expectedValidationResponse = new ValidationResponse(
                    List.of(new Violation("number", "Number cannot be blank")));
            ValidationResponse actualResponse = objectMapper.readValue(
                    mvcResult.getResponse().getContentAsString(), ValidationResponse.class);

            assertThat(actualResponse.violations()).containsExactlyInAnyOrderElementsOf(
                    expectedValidationResponse.violations());
        }

        @Test
        void create_whenYearIsNull_thenReturn400AndValidationResponse() throws Exception {
            CarCreateEditDto createCar = getCarCreateEditDtoBuilder()
                        .year(null)
                        .build();

            MvcResult mvcResult = mockMvc.perform(post(URL, CAR_ENTITY)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createCar)))
                    .andExpect(status().isBadRequest())
                    .andReturn();

            ValidationResponse expectedValidationResponse = new ValidationResponse(
                    List.of(new Violation("year", "Year cannot be null")));
            ValidationResponse actualResponse = objectMapper.readValue(
                    mvcResult.getResponse().getContentAsString(), ValidationResponse.class);

            assertThat(actualResponse.violations()).containsExactlyInAnyOrderElementsOf(
                    expectedValidationResponse.violations());
        }

        @Test
        void create_whenYearIsLess_thenReturn400AndValidationResponse() throws Exception {
            CarCreateEditDto createCar = getCarCreateEditDtoBuilder()
                        .year(1900)
                        .build();

            MvcResult mvcResult = mockMvc.perform(post(URL, CAR_ENTITY)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createCar)))
                    .andExpect(status().isBadRequest())
                    .andReturn();

            ValidationResponse expectedValidationResponse = new ValidationResponse(
                    List.of(new Violation("year", "Year cannot be less 1980 and more 2025")));
            ValidationResponse actualResponse = objectMapper.readValue(
                    mvcResult.getResponse().getContentAsString(), ValidationResponse.class);

            assertThat(actualResponse.violations()).containsExactlyInAnyOrderElementsOf(
                    expectedValidationResponse.violations());
        }
    }

    @Nested
    @DisplayName("Update tests")
    public class updateTests {
        @Test
        void update_whenVerifyingRequestMatching_thenReturn200() throws Exception {
            CarCreateEditDto createCar = getCarCreateEditDtoBuilder().build();

            mockMvc.perform(put(URL_WITH_ID, CAR_ENTITY, DEFAULT_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createCar)))
                    .andExpect(status().isOk());

            verify(carService, times(1)).update(DEFAULT_ID, createCar);
        }

        @Test
        void update_whenValidInput_thenMapsToBusinessModel() throws Exception {
            CarCreateEditDto createCar = getCarCreateEditDtoBuilder()
                        .color("yellow")
                        .build();

            mockMvc.perform(put(URL_WITH_ID, CAR_ENTITY, DEFAULT_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createCar)))
                    .andExpect(status().isOk());

            ArgumentCaptor<CarCreateEditDto> carCaptor = ArgumentCaptor.forClass(CarCreateEditDto.class);
            ArgumentCaptor<Long> idCaptor = ArgumentCaptor.forClass(Long.class);

            verify(carService, times(1)).update(idCaptor.capture(), carCaptor.capture());
            assertThat(idCaptor.getValue()).isEqualTo(DEFAULT_ID);
            assertThat(carCaptor.getValue().color()).isEqualTo("yellow");
            assertThat(carCaptor.getValue().brand()).isEqualTo(DEFAULT_BRAND);
            assertThat(carCaptor.getValue().number()).isEqualTo(DEFAULT_NUMBER);
            assertThat(carCaptor.getValue().year()).isEqualTo(DEFAULT_YEAR);
        }

        @Test
        void update_whenValidInput_thenReturn200AndCarReadDto() throws Exception {
            CarReadDto readCar = getCarReadDtoBuilder().build();
            CarCreateEditDto createCar = getCarCreateEditDtoBuilder().build();

            when(carService.update(DEFAULT_ID, createCar)).thenReturn(readCar);

            MvcResult mvcResult = mockMvc.perform(put(URL_WITH_ID, CAR_ENTITY, DEFAULT_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createCar)))
                    .andExpect(status().isOk())
                    .andReturn();

            String actualResponseBody = mvcResult.getResponse().getContentAsString();
            assertThat(actualResponseBody).isEqualToIgnoringWhitespace(
                    objectMapper.writeValueAsString(readCar));
        }

        @Test
        void update_whenInvalidInput_thenReturn400AndValidationResponse() throws Exception {
            CarCreateEditDto createCar = getCarCreateEditDtoBuilder()
                        .color(null)
                        .brand(null)
                        .number(null)
                        .year(2026)
                        .build();

            MvcResult mvcResult = mockMvc.perform(put(URL_WITH_ID, CAR_ENTITY, DEFAULT_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createCar)))
                    .andExpect(status().isBadRequest())
                    .andReturn();

            ValidationResponse expectedValidationResponse = new ValidationResponse(
                    List.of(new Violation("color", "Color cannot be blank"),
                            new Violation("brand", "Brand cannot be blank"),
                            new Violation("number", "Number cannot be blank"),
                            new Violation("year", "Year cannot be less 1980 and more 2025")));
            ValidationResponse actualResponse = objectMapper.readValue(
                    mvcResult.getResponse().getContentAsString(), ValidationResponse.class);

            assertThat(actualResponse.violations()).containsExactlyInAnyOrderElementsOf(
                    expectedValidationResponse.violations());
        }

        @Test
        void update_whenInvalidNumberPattern_thenReturn400AndValidationResponse() throws Exception {
            CarCreateEditDto createCar = getCarCreateEditDtoBuilder()
                        .number("45sssa")
                        .build();

            MvcResult mvcResult = mockMvc.perform(put(URL_WITH_ID, CAR_ENTITY, DEFAULT_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createCar)))
                    .andExpect(status().isBadRequest())
                    .andReturn();

            ValidationResponse expectedValidationResponse = new ValidationResponse(
                    List.of(new Violation("number", "Invalid number. Possible form: AB123CD")));
            ValidationResponse actualResponse = objectMapper.readValue(
                    mvcResult.getResponse().getContentAsString(), ValidationResponse.class);

            assertThat(actualResponse.violations()).containsExactlyInAnyOrderElementsOf(
                    expectedValidationResponse.violations());
        }

        @Test
        void update_whenColorIsNull_thenReturn400AndValidationResponse() throws Exception {
            CarCreateEditDto createCar = getCarCreateEditDtoBuilder()
                        .color(null)
                        .build();

            MvcResult mvcResult = mockMvc.perform(put(URL_WITH_ID, CAR_ENTITY, DEFAULT_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createCar)))
                    .andExpect(status().isBadRequest())
                    .andReturn();

            ValidationResponse expectedValidationResponse = new ValidationResponse(
                    List.of(new Violation("color", "Color cannot be blank")));
            ValidationResponse actualResponse = objectMapper.readValue(
                    mvcResult.getResponse().getContentAsString(), ValidationResponse.class);

            assertThat(actualResponse.violations()).containsExactlyInAnyOrderElementsOf(
                    expectedValidationResponse.violations());
        }

        @Test
        void update_whenBrandIsNull_thenReturn400AndValidationResponse() throws Exception {
            CarCreateEditDto createCar = getCarCreateEditDtoBuilder()
                        .brand(null)
                        .build();

            MvcResult mvcResult = mockMvc.perform(put(URL_WITH_ID, CAR_ENTITY, DEFAULT_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createCar)))
                    .andExpect(status().isBadRequest())
                    .andReturn();

            ValidationResponse expectedValidationResponse = new ValidationResponse(
                    List.of(new Violation("brand", "Brand cannot be blank")));
            ValidationResponse actualResponse = objectMapper.readValue(
                    mvcResult.getResponse().getContentAsString(), ValidationResponse.class);

            assertThat(actualResponse.violations()).containsExactlyInAnyOrderElementsOf(
                    expectedValidationResponse.violations());
        }

        @Test
        void update_whenNumberIsNull_thenReturn400AndValidationResponse() throws Exception {
            CarCreateEditDto createCar = getCarCreateEditDtoBuilder()
                        .number(null)
                        .build();

            MvcResult mvcResult = mockMvc.perform(put(URL_WITH_ID, CAR_ENTITY, DEFAULT_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createCar)))
                    .andExpect(status().isBadRequest())
                    .andReturn();

            ValidationResponse expectedValidationResponse = new ValidationResponse(
                    List.of(new Violation("number", "Number cannot be blank")));
            ValidationResponse actualResponse = objectMapper.readValue(
                    mvcResult.getResponse().getContentAsString(), ValidationResponse.class);

            assertThat(actualResponse.violations()).containsExactlyInAnyOrderElementsOf(
                    expectedValidationResponse.violations());
        }

        @Test
        void update_whenYearIsNull_thenReturn400AndValidationResponse() throws Exception {
            CarCreateEditDto createCar = getCarCreateEditDtoBuilder()
                        .year(null)
                        .build();

            MvcResult mvcResult = mockMvc.perform(put(URL_WITH_ID, CAR_ENTITY, DEFAULT_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createCar)))
                    .andExpect(status().isBadRequest())
                    .andReturn();

            ValidationResponse expectedValidationResponse = new ValidationResponse(
                    List.of(new Violation("year", "Year cannot be null")));
            ValidationResponse actualResponse = objectMapper.readValue(
                    mvcResult.getResponse().getContentAsString(), ValidationResponse.class);

            assertThat(actualResponse.violations()).containsExactlyInAnyOrderElementsOf(
                    expectedValidationResponse.violations());
        }

        @Test
        void update_whenYearIsLess_thenReturn400AndValidationResponse() throws Exception {
            CarCreateEditDto createCar = getCarCreateEditDtoBuilder()
                        .year(1900)
                        .build();

            MvcResult mvcResult = mockMvc.perform(put(URL_WITH_ID, CAR_ENTITY, DEFAULT_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createCar)))
                    .andExpect(status().isBadRequest())
                    .andReturn();

            ValidationResponse expectedValidationResponse = new ValidationResponse(
                    List.of(new Violation("year", "Year cannot be less 1980 and more 2025")));
            ValidationResponse actualResponse = objectMapper.readValue(
                    mvcResult.getResponse().getContentAsString(), ValidationResponse.class);

            assertThat(actualResponse.violations()).containsExactlyInAnyOrderElementsOf(
                    expectedValidationResponse.violations());
        }
    }

    @Nested
    @DisplayName("Delete tests")
    public class deleteTests {
        @Test
        void delete_whenVerifyingRequestMatching_thenReturn204() throws Exception {
            mockMvc.perform(delete(URL_WITH_ID, CAR_ENTITY, DEFAULT_ID))
                    .andExpect(status().isNoContent());

            verify(carService, times(1)).safeDelete(DEFAULT_ID);
        }
    }
}