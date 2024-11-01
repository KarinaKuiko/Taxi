package org.example.driver.unit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.driver.controller.DriverController;
import org.example.driver.dto.create.DriverCreateEditDto;
import org.example.driver.dto.read.DriverReadDto;
import org.example.driver.dto.read.ValidationResponse;
import org.example.driver.entity.enumeration.Gender;
import org.example.driver.exception.violation.Violation;
import org.example.driver.service.DriverService;
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

@WebMvcTest(controllers = DriverController.class)
public class DriverControllerTest {
    private static final String URL = "/api/v1/drivers";
    private static final Long DEFAULT_ID = 1L;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DriverService driverService;

    private List<DriverReadDto> driverList;
    private Page<DriverReadDto> driverPage;
    private DriverReadDto defaultDriver;

    @BeforeEach
    void init() {
        defaultDriver = new DriverReadDto(1L, "Vlad",
                "vlad@gmail.com", "+375441234567", Gender.MALE, 1L, 5.0);
        DriverReadDto driver2 = new DriverReadDto(2L, "Pasha",
                "pasha@gmail.com", "+375441111111", Gender.MALE, 2L, 3.0);
        driverList = List.of(defaultDriver, driver2);
        driverPage = new PageImpl<>(driverList, PageRequest.of(0, 10), 2);
    }

    @Test
    void findAll_whenVerifyingRequestMatchingWithoutParams_thenReturn200() throws Exception {
        when(driverService.findAll(0, 10)).thenReturn(driverPage);

        mockMvc.perform(get(URL))
                .andExpect(status().isOk());
    }

    @Test
    void findAll_whenCorrectParams_thenReturn200() throws Exception {
        when(driverService.findAll(1, 20)).thenReturn(driverPage);

        mockMvc.perform(get(URL)
                        .param("page", "1")
                        .param("limit", "20"))
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
        when(driverService.findById(DEFAULT_ID)).thenReturn(driverList.get(0));

        MvcResult mvcResult = mockMvc.perform(get(URL + "/{id}", DEFAULT_ID))
                .andExpect(status().isOk())
                .andReturn();

        String actual = mvcResult.getResponse().getContentAsString();

        assertThat(actual).isEqualToIgnoringWhitespace(
                objectMapper.writeValueAsString(defaultDriver));
    }

    @Test
    void findById_whenVerifyingRequestMatching_thenReturn200() throws Exception {
        when(driverService.findById(DEFAULT_ID)).thenReturn(driverList.get(0));

        MvcResult mvcResult = mockMvc.perform(get(URL + "/1"))
                .andExpect(status().isOk())
                .andReturn();

        String actual = mvcResult.getResponse().getContentAsString();

        assertThat(actual).isEqualToIgnoringWhitespace(
                objectMapper.writeValueAsString(defaultDriver));
    }

    @Test
    void create_whenVerifyingRequestMatching_thenReturn200() throws Exception {
        DriverCreateEditDto createDriver = new DriverCreateEditDto("test",
                "test@gmail.com", "+375297654321", Gender.MALE, 2L);

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDriver)))
                .andExpect(status().isCreated())
                .andReturn();
    }

    @Test
    void create_whenValidInput_thenMapsToBusinessModel() throws Exception {
        DriverCreateEditDto createDriver = new DriverCreateEditDto("test",
                "test@gmail.com", "+375297654321", Gender.MALE, 2L);

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDriver)))
                .andExpect(status().isCreated())
                .andReturn();

        ArgumentCaptor<DriverCreateEditDto> driverCaptor = ArgumentCaptor.forClass(DriverCreateEditDto.class);

        verify(driverService, times(1)).create(driverCaptor.capture());
        assertThat(driverCaptor.getValue().name()).isEqualTo("test");
        assertThat(driverCaptor.getValue().email()).isEqualTo("test@gmail.com");
        assertThat(driverCaptor.getValue().phone()).isEqualTo("+375297654321");
        assertThat(driverCaptor.getValue().gender()).isEqualTo(Gender.MALE);
        assertThat(driverCaptor.getValue().carId()).isEqualTo(2L);
    }

    @Test
    void create_whenValidInput_thenReturn201AndCarReadDto() throws Exception {
        DriverCreateEditDto createDriver = new DriverCreateEditDto("test",
                "test@gmail.com", "+375297654321", Gender.MALE, 2L);
        DriverReadDto readDriver = new DriverReadDto(3L, "test",
                "test@gmail.com", "+375297654321", Gender.MALE, 2L, 5.0);

        when(driverService.create(createDriver)).thenReturn(readDriver);

        MvcResult mvcResult = mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDriver)))
                .andExpect(status().isCreated())
                .andReturn();

        String actualResponseBody = mvcResult.getResponse().getContentAsString();
        assertThat(actualResponseBody).isEqualToIgnoringWhitespace(
                objectMapper.writeValueAsString(readDriver));
    }

    @Test
    void create_whenInvalidInput_thenReturn400AndValidationResponse() throws Exception {
        DriverCreateEditDto createDriver = new DriverCreateEditDto(null, null, null, Gender.MALE, 2L);

        MvcResult mvcResult = mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDriver)))
                .andExpect(status().isBadRequest())
                .andReturn();

        ValidationResponse expectedValidationResponse = new ValidationResponse(
                List.of(new Violation("name", "{name.blank}"),
                        new Violation("email", "{email.blank}"),
                        new Violation("phone", "{phone.blank}")));
        ValidationResponse actualResponse = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(), ValidationResponse.class);

        assertThat(actualResponse.violations()).containsExactlyInAnyOrderElementsOf(
                expectedValidationResponse.violations());
    }

    @Test
    void create_whenInvalidInputWithPattern_thenReturn400AndValidationResponse() throws Exception {
        DriverCreateEditDto createDriver = new DriverCreateEditDto(null,
                "test.gmail", "+375884562", Gender.MALE, 2L);

        MvcResult mvcResult = mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDriver)))
                .andExpect(status().isBadRequest())
                .andReturn();

        ValidationResponse expectedValidationResponse = new ValidationResponse(
                List.of(new Violation("name", "{name.blank}"),
                        new Violation("email", "{email.invalid}"),
                        new Violation("phone", "{phone.invalid}")));
        ValidationResponse actualResponse = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(), ValidationResponse.class);

        assertThat(actualResponse.violations()).containsExactlyInAnyOrderElementsOf(
                expectedValidationResponse.violations());
    }

    @Test
    void update_whenValidInput_thenReturn200() throws Exception {
        DriverCreateEditDto updateDriver = new DriverCreateEditDto("test",
                "test@gmail.com", "+375297654321", Gender.MALE, 2L);

        mockMvc.perform(put(URL + "/{id}", DEFAULT_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDriver)))
                .andExpect(status().isOk());
    }

    @Test
    void update_whenVerifyingRequestMatching_thenReturn200() throws Exception {
        DriverCreateEditDto updateDriver = new DriverCreateEditDto("test",
                "test@gmail.com", "+375297654321", Gender.MALE, 2L);

        mockMvc.perform(put(URL + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDriver)))
                .andExpect(status().isOk());
    }

    @Test
    void update_whenValidInput_thenMapsToBusinessModel() throws Exception {
        DriverCreateEditDto updateDriver = new DriverCreateEditDto("test",
                "test@gmail.com", "+375297654321", Gender.MALE, 2L);

        mockMvc.perform(put(URL + "/{id}", DEFAULT_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDriver)))
                .andExpect(status().isOk());

        ArgumentCaptor<DriverCreateEditDto> driverCaptor = ArgumentCaptor.forClass(DriverCreateEditDto.class);
        ArgumentCaptor<Long> idCaptor = ArgumentCaptor.forClass(Long.class);

        verify(driverService, times(1)).update(idCaptor.capture(), driverCaptor.capture());
        assertThat(idCaptor.getValue()).isEqualTo(DEFAULT_ID);
        assertThat(driverCaptor.getValue().name()).isEqualTo("test");
        assertThat(driverCaptor.getValue().email()).isEqualTo("test@gmail.com");
        assertThat(driverCaptor.getValue().phone()).isEqualTo("+375297654321");
        assertThat(driverCaptor.getValue().gender()).isEqualTo(Gender.MALE);
    }

    @Test
    void update_whenValidInput_thenReturn200AndCarReadDto() throws Exception {
        DriverCreateEditDto updateDriver = new DriverCreateEditDto("test",
                "test@gmail.com", "+375297654321", Gender.MALE, 2L);
        DriverReadDto readDriver = new DriverReadDto(DEFAULT_ID, "test",
                "test@gmail.com", "+375297654321", Gender.MALE, 2L, 5.);
        when(driverService.update(DEFAULT_ID, updateDriver)).thenReturn(readDriver);

        MvcResult mvcResult = mockMvc.perform(put(URL + "/{id}", DEFAULT_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDriver)))
                .andExpect(status().isOk())
                .andReturn();

        String actualResponseBody = mvcResult.getResponse().getContentAsString();
        assertThat(actualResponseBody).isEqualToIgnoringWhitespace(
                objectMapper.writeValueAsString(readDriver));
    }

    @Test
    void update_whenInvalidInput_thenReturn400AndValidationResponse() throws Exception {
        DriverCreateEditDto updateDriver = new DriverCreateEditDto(null, null, null, Gender.MALE, 2L);

        MvcResult mvcResult = mockMvc.perform(put(URL + "/{id}", DEFAULT_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDriver)))
                .andExpect(status().isBadRequest())
                .andReturn();

        ValidationResponse expectedValidationResponse = new ValidationResponse(
                List.of(new Violation("name", "{name.blank}"),
                        new Violation("email", "{email.blank}"),
                        new Violation("phone", "{phone.blank}")));
        ValidationResponse actualResponse = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(), ValidationResponse.class);

        assertThat(actualResponse.violations()).containsExactlyInAnyOrderElementsOf(
                expectedValidationResponse.violations());
    }

    @Test
    void update_whenInvalidInputWithPattern_thenReturn400AndValidationResponse() throws Exception {
        DriverCreateEditDto updateDriver = new DriverCreateEditDto(null,
                "test.gmail", "+375884562", Gender.MALE, 2L);

        MvcResult mvcResult = mockMvc.perform(put(URL + "/{id}", DEFAULT_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDriver)))
                .andExpect(status().isBadRequest())
                .andReturn();

        ValidationResponse expectedValidationResponse = new ValidationResponse(
                List.of(new Violation("name", "{name.blank}"),
                        new Violation("email", "{email.invalid}"),
                        new Violation("phone", "{phone.invalid}")));
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
