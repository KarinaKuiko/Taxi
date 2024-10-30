package org.example.passenger.unit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.passenger.controller.PassengerController;
import org.example.passenger.dto.create.PassengerCreateEditDto;
import org.example.passenger.dto.read.PassengerReadDto;
import org.example.passenger.dto.read.ValidationResponse;
import org.example.passenger.exception.violation.Violation;
import org.example.passenger.service.PassengerService;
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

@WebMvcTest(controllers = PassengerController.class)
public class PassengerControllerTest {
    private static final String URL = "/api/v1/passengers";
    private static final Long DEFAULT_ID = 1L;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PassengerService passengerService;

    private List<PassengerReadDto> passengerList;
    private Page<PassengerReadDto> passengerPage;
    private PassengerReadDto defaultPassenger;

    @BeforeEach
    void init() {
        defaultPassenger = new PassengerReadDto(DEFAULT_ID, "passenger", "passenger@gmail.com", "+375441234567", 5.);
        PassengerReadDto passenger2 = new PassengerReadDto(2L, "Ignat", "ignat@gmail.com", "+375299876541", 3.);
        passengerList = List.of(defaultPassenger, passenger2);
        passengerPage = new PageImpl<>(passengerList, PageRequest.of(0, 10), 2);
    }

    @Test
    void findAll_whenVerifyingRequestMatchingWithoutParams_thenReturn200() throws Exception {
        when(passengerService.findAll(0, 10)).thenReturn(passengerPage);

        mockMvc.perform(get(URL))
                .andExpect(status().isOk());
    }

    @Test
    void findAll_whenCorrectParams_thenReturn200() throws Exception {
        when(passengerService.findAll(0, 10)).thenReturn(passengerPage);

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

        assertThat(actualResponse).isEqualToIgnoringWhitespace(objectMapper.writeValueAsString(expextedValidationResponse));
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
        when(passengerService.findById(DEFAULT_ID)).thenReturn(passengerList.get(0));

        MvcResult mvcResult = mockMvc.perform(get(URL + "/{id}", DEFAULT_ID))
                .andExpect(status().isOk())
                .andReturn();

        String actual = mvcResult.getResponse().getContentAsString();

        assertThat(actual).isEqualToIgnoringWhitespace(
                objectMapper.writeValueAsString(defaultPassenger));
    }

    @Test
    void findById_whenVerifyingRequestMatching_thenReturn200() throws Exception {
        when(passengerService.findById(DEFAULT_ID)).thenReturn(passengerList.get(0));

        MvcResult mvcResult = mockMvc.perform(get(URL + "/1"))
                .andExpect(status().isOk())
                .andReturn();

        String actual = mvcResult.getResponse().getContentAsString();

        assertThat(actual).isEqualToIgnoringWhitespace(
                objectMapper.writeValueAsString(defaultPassenger));
    }

    @Test
    void create_whenVerifyingRequestMatching_thenReturn200() throws Exception {
        PassengerCreateEditDto passengerCreate = new PassengerCreateEditDto("test", "test@gmail.com", "+375331122339");

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passengerCreate)))
                .andExpect(status().isCreated())
                .andReturn();
    }

    @Test
    void create_whenValidInput_thenMapsToBusinessModel() throws Exception {
        PassengerCreateEditDto passengerCreate = new PassengerCreateEditDto("test", "test@gmail.com", "+375331122339");

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passengerCreate)))
                .andExpect(status().isCreated())
                .andReturn();

        ArgumentCaptor<PassengerCreateEditDto> carCaptor = ArgumentCaptor.forClass(PassengerCreateEditDto.class);

        verify(passengerService, times(1)).create(carCaptor.capture());
        assertThat(carCaptor.getValue().name()).isEqualTo("test");
        assertThat(carCaptor.getValue().email()).isEqualTo("test@gmail.com");
        assertThat(carCaptor.getValue().phone()).isEqualTo("+375331122339");
    }

    @Test
    void create_whenValidInput_thenReturn201AndCarReadDto() throws Exception {
        PassengerCreateEditDto passengerCreate = new PassengerCreateEditDto("test", "test@gmail.com", "+375331122339");
        PassengerReadDto passengerRead = new PassengerReadDto(3L, "test", "test@gmail.com", "+375331122339", 5.);

        when(passengerService.create(passengerCreate)).thenReturn(passengerRead);

        MvcResult mvcResult = mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passengerCreate)))
                .andExpect(status().isCreated())
                .andReturn();

        String actualResponseBody = mvcResult.getResponse().getContentAsString();
        assertThat(actualResponseBody).isEqualToIgnoringWhitespace(
                objectMapper.writeValueAsString(passengerRead));
    }

    @Test
    void create_whenInvalidInput_thenReturn400AndValidationResponse() throws Exception {
        PassengerCreateEditDto passengerCreate = new PassengerCreateEditDto(null, null, null);

        MvcResult mvcResult = mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passengerCreate)))
                .andExpect(status().isBadRequest())
                .andReturn();

        ValidationResponse expectedValidationResponse = new ValidationResponse(
                List.of(new Violation("name", "{name.blank}"),
                        new Violation("email", "{email.blank}"),
                        new Violation("phone", "{phone.blank}")));
        ValidationResponse actualResponse = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ValidationResponse.class);

        assertThat(actualResponse.violations()).containsExactlyInAnyOrderElementsOf(expectedValidationResponse.violations());
    }

    @Test
    void create_whenInvalidInputWithPattern_thenReturn400AndValidationResponse() throws Exception {
        PassengerCreateEditDto passengerCreate = new PassengerCreateEditDto(null, "test.gmail", "+45619");

        MvcResult mvcResult = mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passengerCreate)))
                .andExpect(status().isBadRequest())
                .andReturn();

        ValidationResponse expectedValidationResponse = new ValidationResponse(
                List.of(new Violation("name", "{name.blank}"),
                        new Violation("email", "{email.invalid}"),
                        new Violation("phone", "{phone.invalid}")));
        ValidationResponse actualResponse = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ValidationResponse.class);

        assertThat(actualResponse.violations()).containsExactlyInAnyOrderElementsOf(expectedValidationResponse.violations());
    }

    @Test
    void update_whenVerifyingRequestMatching_thenReturn200() throws Exception {
        PassengerCreateEditDto updatePassenger = new PassengerCreateEditDto("test", "test@gmail.com", "+375331122339");

        mockMvc.perform(put(URL + "/{id}", DEFAULT_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatePassenger)))
                .andExpect(status().isOk());
    }

    @Test
    void update_whenValidInput_thenReturn200() throws Exception {
        PassengerCreateEditDto updatePassenger = new PassengerCreateEditDto("test", "test@gmail.com", "+375331122339");

        mockMvc.perform(put(URL + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatePassenger)))
                .andExpect(status().isOk());
    }

    @Test
    void update_whenValidInput_thenMapsToBusinessModel() throws Exception {
        PassengerCreateEditDto updatePassenger = new PassengerCreateEditDto("test", "test@gmail.com", "+375331122339");

        mockMvc.perform(put(URL + "/{id}", DEFAULT_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatePassenger)))
                .andExpect(status().isOk());

        ArgumentCaptor<PassengerCreateEditDto> passengerCaptor = ArgumentCaptor.forClass(PassengerCreateEditDto.class);
        ArgumentCaptor<Long> idCaptor = ArgumentCaptor.forClass(Long.class);

        verify(passengerService, times(1)).update(idCaptor.capture(), passengerCaptor.capture());
        assertThat(idCaptor.getValue()).isEqualTo(DEFAULT_ID);
        assertThat(passengerCaptor.getValue().name()).isEqualTo("test");
        assertThat(passengerCaptor.getValue().email()).isEqualTo("test@gmail.com");
        assertThat(passengerCaptor.getValue().phone()).isEqualTo("+375331122339");
    }

    @Test
    void update_whenValidInput_thenReturn200AndCarReadDto() throws Exception {
        PassengerCreateEditDto updatePassenger = new PassengerCreateEditDto("test", "test@gmail.com", "+375331122339");
        PassengerReadDto readPassenger = new PassengerReadDto(DEFAULT_ID, "test", "test@gmail.com", "+375331122339", 5.);

        when(passengerService.update(DEFAULT_ID, updatePassenger)).thenReturn(readPassenger);

        MvcResult mvcResult = mockMvc.perform(put(URL + "/{id}", DEFAULT_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatePassenger)))
                .andExpect(status().isOk())
                .andReturn();

        String actualResponseBody = mvcResult.getResponse().getContentAsString();
        assertThat(actualResponseBody).isEqualToIgnoringWhitespace(
                objectMapper.writeValueAsString(readPassenger));
    }

    @Test
    void update_whenInvalidInput_thenReturn400AndValidationResponse() throws Exception {
        PassengerCreateEditDto updatePassenger = new PassengerCreateEditDto(null, null, null);

        MvcResult mvcResult = mockMvc.perform(put(URL + "/{id}", DEFAULT_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatePassenger)))
                .andExpect(status().isBadRequest())
                .andReturn();

        ValidationResponse expectedValidationResponse = new ValidationResponse(
                List.of(new Violation("name", "{name.blank}"),
                        new Violation("email", "{email.blank}"),
                        new Violation("phone", "{phone.blank}")));
        ValidationResponse actualResponse = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ValidationResponse.class);

        assertThat(actualResponse.violations()).containsExactlyInAnyOrderElementsOf(expectedValidationResponse.violations());
    }

    @Test
    void update_whenInvalidInputWithPattern_thenReturn400AndValidationResponse() throws Exception {
        PassengerCreateEditDto updatePassenger = new PassengerCreateEditDto(null, "test.gmail", "+45619");

        MvcResult mvcResult = mockMvc.perform(put(URL + "/{id}", DEFAULT_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatePassenger)))
                .andExpect(status().isBadRequest())
                .andReturn();

        ValidationResponse expectedValidationResponse = new ValidationResponse(
                List.of(new Violation("name", "{name.blank}"),
                        new Violation("email", "{email.invalid}"),
                        new Violation("phone", "{phone.invalid}")));
        ValidationResponse actualResponse = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ValidationResponse.class);

        assertThat(actualResponse.violations()).containsExactlyInAnyOrderElementsOf(expectedValidationResponse.violations());
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
