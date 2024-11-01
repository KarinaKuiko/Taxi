package org.example.driver.unit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.driver.controller.CarController;
import org.example.driver.dto.create.CarCreateEditDto;
import org.example.driver.dto.read.CarReadDto;
import org.example.driver.dto.read.ValidationResponse;
import org.example.driver.exception.violation.Violation;
import org.example.driver.service.CarService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CarController.class)
public class CarControllerTest {
    private static final String URL = "/api/v1/cars";
    private static final Long DEFAULT_ID = 1L;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CarService carService;

    private List<CarReadDto> carList;
    private Page<CarReadDto> carPage;
    private CarReadDto defaultCar;

    @BeforeEach
    void init() {
        defaultCar = new CarReadDto(DEFAULT_ID, "red", "BMW", "AB123CD", 2023, List.of());
        CarReadDto car2 = new CarReadDto(2L, "white", "Peugeot", "CD321AB", 2023, List.of());
        carList = List.of(defaultCar, car2);
        carPage = new PageImpl<>(carList, PageRequest.of(0, 10), 2);
    }

    @Test
    void findAll_whenVerifyingRequestMatchingWithoutParams_thenReturn200() throws Exception {
        when(carService.findAll(0, 10)).thenReturn(carPage);

        mockMvc.perform(get(URL))
                .andExpect(status().isOk());
    }

    @Test
    void findAll_whenCorrectParams_thenReturn200() throws Exception {
        when(carService.findAll(0, 10)).thenReturn(carPage);

        mockMvc.perform(get(URL)
                        .param("page", "0")
                        .param("limit", "10"))
                .andExpect(status().isOk());
    }

    @Test
    void findAll_whenLimitIsGreaterThanMax_thenReturn400AndValidationResponse() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get(URL)
                        .param("page", "1")
                        .param("limit", "101"))
                .andExpect(status().isBadRequest())
                .andReturn();

        ValidationResponse expextedValidationResponse = new ValidationResponse(
                List.of(new Violation("limit", "must be less than or equal to 100")));
        String actualResponse = mvcResult.getResponse().getContentAsString();

        assertThat(actualResponse).isEqualToIgnoringWhitespace(
                objectMapper.writeValueAsString(expextedValidationResponse));
    }

    @Test
    void findAll_whenLimitIsLessThanMin_thenReturn400AndValidationResponse() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get(URL)
                        .param("page", "1")
                        .param("limit", "0"))
                .andExpect(status().isBadRequest())
                .andReturn();

        ValidationResponse expextedValidationResponse = new ValidationResponse(
                List.of(new Violation("limit", "must be greater than or equal to 1")));
        String actualResponse = mvcResult.getResponse().getContentAsString();

        assertThat(actualResponse).isEqualToIgnoringWhitespace(
                objectMapper.writeValueAsString(expextedValidationResponse));
    }

    @Test
    void findById_whenValidInput_thenReturn200() throws Exception {
        when(carService.findById(DEFAULT_ID)).thenReturn(carList.get(0));

        MvcResult mvcResult = mockMvc.perform(get(URL + "/{id}", DEFAULT_ID))
                .andExpect(status().isOk())
                .andReturn();

        String actual = mvcResult.getResponse().getContentAsString();

        assertThat(actual).isEqualToIgnoringWhitespace(
                objectMapper.writeValueAsString(defaultCar));
    }

    @Test
    void findById_whenVerifyingRequestMatching_thenReturn200() throws Exception {
        when(carService.findById(DEFAULT_ID)).thenReturn(carList.get(0));

        MvcResult mvcResult = mockMvc.perform(get(URL + "/1"))
                .andExpect(status().isOk())
                .andReturn();

        String actual = mvcResult.getResponse().getContentAsString();

        assertThat(actual).isEqualToIgnoringWhitespace(
                objectMapper.writeValueAsString(defaultCar));
    }

    @Test
    void create_whenVerifyingRequestMatching_thenReturn200() throws Exception {
        CarCreateEditDto createCar = new CarCreateEditDto("yellow", "Peugeot", "LK124AS", 2014);

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createCar)))
                .andExpect(status().isCreated())
                .andReturn();
    }

    @Test
    void create_whenValidInput_thenMapsToBusinessModel() throws Exception {
        CarCreateEditDto createCar = new CarCreateEditDto("yellow", "Peugeot", "LK124AS", 2014);

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createCar)))
                .andExpect(status().isCreated())
                .andReturn();

        ArgumentCaptor<CarCreateEditDto> carCaptor = ArgumentCaptor.forClass(CarCreateEditDto.class);

        verify(carService, times(1)).create(carCaptor.capture());
        assertThat(carCaptor.getValue().color()).isEqualTo("yellow");
        assertThat(carCaptor.getValue().brand()).isEqualTo("Peugeot");
        assertThat(carCaptor.getValue().number()).isEqualTo("LK124AS");
        assertThat(carCaptor.getValue().year()).isEqualTo(2014);
    }

    @Test
    void create_whenValidInput_thenReturn201AndCarReadDto() throws Exception {
        CarCreateEditDto createCar = new CarCreateEditDto("yellow", "Peugeot", "LK124AS", 2014);
        CarReadDto readCar = new CarReadDto(3L, "yellow", "Peugeot", "LK124AS", 2014, List.of());

        when(carService.create(createCar)).thenReturn(readCar);

        MvcResult mvcResult = mockMvc.perform(post(URL)
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
        CarCreateEditDto createCar = new CarCreateEditDto(null, null, null, 2026);

        MvcResult mvcResult = mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createCar)))
                .andExpect(status().isBadRequest())
                .andReturn();

        ValidationResponse expectedValidationResponse = new ValidationResponse(
                List.of(new Violation("color", "{color.blank}"),
                        new Violation("brand", "{brand.blank}"),
                        new Violation("number", "{number.blank}"),
                        new Violation("year", "{year.invalid}")));
        ValidationResponse actualResponse = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(), ValidationResponse.class);

        assertThat(actualResponse.violations()).containsExactlyInAnyOrderElementsOf(
                expectedValidationResponse.violations());
    }

    @Test
    void create_whenInvalidInputWithPattern_thenReturn400AndValidationResponse() throws Exception {
        CarCreateEditDto createCar = new CarCreateEditDto(null, null, "45sssa", 1900);

        MvcResult mvcResult = mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createCar)))
                .andExpect(status().isBadRequest())
                .andReturn();

        ValidationResponse expectedValidationResponse = new ValidationResponse(
                List.of(new Violation("color", "{color.blank}"),
                        new Violation("brand", "{brand.blank}"),
                        new Violation("number", "{number.invalid}"),
                        new Violation("year", "{year.invalid}")));
        ValidationResponse actualResponse = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(), ValidationResponse.class);

        assertThat(actualResponse.violations()).containsExactlyInAnyOrderElementsOf(
                expectedValidationResponse.violations());
    }

    @Test
    void update_whenVerifyingRequestMatching_thenReturn200() throws Exception {
        CarCreateEditDto updateCar = new CarCreateEditDto("white", "BMW", "AB123CD", 2023);

        mockMvc.perform(put(URL + "/{id}", DEFAULT_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateCar)))
                .andExpect(status().isOk());
    }

    @Test
    void update_whenValidInput_thenReturn200() throws Exception {
        CarCreateEditDto updateCar = new CarCreateEditDto("white", "BMW", "AB123CD", 2023);

        mockMvc.perform(put(URL + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateCar)))
                .andExpect(status().isOk());
    }

    @Test
    void update_whenValidInput_thenMapsToBusinessModel() throws Exception {
        CarCreateEditDto updateCar = new CarCreateEditDto("white", "BMW", "AB123CD", 2023);

        mockMvc.perform(put(URL + "/{id}", DEFAULT_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateCar)))
                .andExpect(status().isOk());

        ArgumentCaptor<CarCreateEditDto> carCaptor = ArgumentCaptor.forClass(CarCreateEditDto.class);
        ArgumentCaptor<Long> idCaptor = ArgumentCaptor.forClass(Long.class);

        verify(carService, times(1)).update(idCaptor.capture(), carCaptor.capture());
        assertThat(idCaptor.getValue()).isEqualTo(DEFAULT_ID);
        assertThat(carCaptor.getValue().color()).isEqualTo("white");
        assertThat(carCaptor.getValue().brand()).isEqualTo("BMW");
        assertThat(carCaptor.getValue().number()).isEqualTo("AB123CD");
        assertThat(carCaptor.getValue().year()).isEqualTo(2023);
    }

    @Test
    void update_whenValidInput_thenReturn200AndCarReadDto() throws Exception {
        CarCreateEditDto updateCar = new CarCreateEditDto("yellow", "Peugeot", "LK124AS", 2014);
        CarReadDto readCar = new CarReadDto(DEFAULT_ID, "yellow", "Peugeot", "LK124AS", 2014, List.of());

        when(carService.update(DEFAULT_ID, updateCar)).thenReturn(readCar);

        MvcResult mvcResult = mockMvc.perform(put(URL + "/{id}", DEFAULT_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateCar)))
                .andExpect(status().isOk())
                .andReturn();

        String actualResponseBody = mvcResult.getResponse().getContentAsString();
        assertThat(actualResponseBody).isEqualToIgnoringWhitespace(
                objectMapper.writeValueAsString(readCar));
    }

    @Test
    void update_whenInvalidInput_thenReturn400AndValidationResponse() throws Exception {
        CarCreateEditDto updateCar = new CarCreateEditDto(null, null, null, 2026);

        MvcResult mvcResult = mockMvc.perform(put(URL + "/{id}", DEFAULT_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateCar)))
                .andExpect(status().isBadRequest())
                .andReturn();

        ValidationResponse expectedValidationResponse = new ValidationResponse(
                List.of(new Violation("color", "{color.blank}"),
                        new Violation("brand", "{brand.blank}"),
                        new Violation("number", "{number.blank}"),
                        new Violation("year", "{year.invalid}")));
        ValidationResponse actualResponse = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(), ValidationResponse.class);

        assertThat(actualResponse.violations()).containsExactlyInAnyOrderElementsOf(
                expectedValidationResponse.violations());
    }

    @Test
    void update_whenInvalidInputWithPattern_thenReturn400AndValidationResponse() throws Exception {
        CarCreateEditDto updateCar = new CarCreateEditDto(null, null, "45sdsa", 1900);

        MvcResult mvcResult = mockMvc.perform(put(URL + "/{id}", DEFAULT_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateCar)))
                .andExpect(status().isBadRequest())
                .andReturn();

        ValidationResponse expectedValidationResponse = new ValidationResponse(
                List.of(new Violation("color", "{color.blank}"),
                        new Violation("brand", "{brand.blank}"),
                        new Violation("number", "{number.invalid}"),
                        new Violation("year", "{year.invalid}")));
        ValidationResponse actualResponse = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(), ValidationResponse.class);

        assertThat(actualResponse.violations()).containsExactlyInAnyOrderElementsOf(
                expectedValidationResponse.violations());
    }

    @Test
    void delete_whenVerifyingRequestMatching_thenReturn401() throws Exception {
        mockMvc.perform(delete(URL + "/{id}", DEFAULT_ID))
                .andExpect(status().isNoContent());
    }

    @Test
    void delete_whenValidInput_thenReturn401() throws Exception {
        mockMvc.perform(delete(URL + "/1"))
                .andExpect(status().isNoContent());
    }
}