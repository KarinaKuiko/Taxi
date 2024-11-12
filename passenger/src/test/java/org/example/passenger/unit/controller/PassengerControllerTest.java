package org.example.passenger.unit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.passenger.config.MessageSourceConfig;
import org.example.passenger.controller.PassengerController;
import org.example.passenger.dto.create.PassengerCreateEditDto;
import org.example.passenger.dto.read.PassengerReadDto;
import org.example.passenger.dto.read.ValidationResponse;
import org.example.passenger.exception.violation.Violation;
import org.example.passenger.service.PassengerService;
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
import static org.example.passenger.util.DataUtil.DEFAULT_ID;
import static org.example.passenger.util.DataUtil.LIMIT;
import static org.example.passenger.util.DataUtil.LIMIT_VALUE;
import static org.example.passenger.util.DataUtil.PAGE;
import static org.example.passenger.util.DataUtil.PAGE_VALUE;
import static org.example.passenger.util.DataUtil.URL;
import static org.example.passenger.util.DataUtil.URL_WITH_ID;
import static org.example.passenger.util.DataUtil.getPassengerCreateEditDtoBuilder;
import static org.example.passenger.util.DataUtil.getPassengerReadDtoBuilder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PassengerController.class)
@Import(MessageSourceConfig.class)
class PassengerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PassengerService passengerService;

    private PassengerReadDto readPassenger = getPassengerReadDtoBuilder().build();
    private PassengerCreateEditDto createPassenger = getPassengerCreateEditDtoBuilder().build();

    @Nested
    @DisplayName("Find all tests")
    public class findAllTests {
        @Test
        void findAll_whenVerifyingRequestMatchingWithoutParams_thenReturn200() throws Exception {
            Page<PassengerReadDto> passengerPage = new PageImpl<>(List.of(readPassenger),
                    PageRequest.of(PAGE_VALUE, LIMIT_VALUE), 1);

            when(passengerService.findAll(PAGE_VALUE, LIMIT_VALUE)).thenReturn(passengerPage);

            mockMvc.perform(get(URL))
                    .andExpect(status().isOk());
        }

        @Test
        void findAll_whenCorrectParams_thenReturn200() throws Exception {
            Page<PassengerReadDto> passengerPage = new PageImpl<>(List.of(readPassenger),
                    PageRequest.of(PAGE_VALUE, LIMIT_VALUE), 1);

            when(passengerService.findAll(PAGE_VALUE, LIMIT_VALUE)).thenReturn(passengerPage);

            mockMvc.perform(get(URL)
                            .param(PAGE, PAGE_VALUE.toString())
                            .param(LIMIT, LIMIT_VALUE.toString()))
                    .andExpect(status().isOk());
        }

        @Test
        void findAll_whenLimitIsGreaterThanMax_thenReturn400AndValidationResponse() throws Exception {
            MvcResult mvcResult = mockMvc.perform(get(URL)
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
            MvcResult mvcResult = mockMvc.perform(get(URL)
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
            when(passengerService.findById(DEFAULT_ID)).thenReturn(readPassenger);

            MvcResult mvcResult = mockMvc.perform(get(URL_WITH_ID, DEFAULT_ID))
                    .andExpect(status().isOk())
                    .andReturn();

            String actual = mvcResult.getResponse().getContentAsString();

            assertThat(actual).isEqualToIgnoringWhitespace(
                    objectMapper.writeValueAsString(readPassenger));
        }
    }

    @Nested
    @DisplayName("Create tests")
    public class createTests {
        @Test
        void create_whenVerifyingRequestMatching_thenReturn200() throws Exception {
            mockMvc.perform(post(URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createPassenger)))
                    .andExpect(status().isCreated())
                    .andReturn();

            verify(passengerService, times(1)).create(createPassenger);
        }

        @Test
        void create_whenValidInput_thenMapsToBusinessModel() throws Exception {
            mockMvc.perform(post(URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createPassenger)))
                    .andExpect(status().isCreated())
                    .andReturn();

            ArgumentCaptor<PassengerCreateEditDto> carCaptor = ArgumentCaptor.forClass(PassengerCreateEditDto.class);

            verify(passengerService, times(1)).create(carCaptor.capture());
            assertThat(carCaptor.getValue().name()).isEqualTo("passenger");
            assertThat(carCaptor.getValue().email()).isEqualTo("passenger@gmail.com");
            assertThat(carCaptor.getValue().phone()).isEqualTo("+375441234567");
        }

        @Test
        void create_whenValidInput_thenReturn201AndCarReadDto() throws Exception {
            when(passengerService.create(createPassenger)).thenReturn(readPassenger);

            MvcResult mvcResult = mockMvc.perform(post(URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createPassenger)))
                    .andExpect(status().isCreated())
                    .andReturn();

            String actualResponseBody = mvcResult.getResponse().getContentAsString();
            assertThat(actualResponseBody).isEqualToIgnoringWhitespace(
                    objectMapper.writeValueAsString(readPassenger));
        }

        @Test
        void create_whenInvalidInput_thenReturn400AndValidationResponse() throws Exception {
            createPassenger = getPassengerCreateEditDtoBuilder()
                                .name(null)
                                .email(null)
                                .phone(null)
                                .build();

            MvcResult mvcResult = mockMvc.perform(post(URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createPassenger)))
                    .andExpect(status().isBadRequest())
                    .andReturn();

            ValidationResponse expectedValidationResponse = new ValidationResponse(
                    List.of(new Violation("name", "Name cannot be blank"),
                            new Violation("email", "Email cannot be blank"),
                            new Violation("phone", "Phone cannot be blank")));
            ValidationResponse actualResponse = objectMapper.readValue(
                    mvcResult.getResponse().getContentAsString(), ValidationResponse.class);

            assertThat(actualResponse.violations()).containsExactlyInAnyOrderElementsOf(
                    expectedValidationResponse.violations());
        }

        @Test
        void create_whenInvalidPhonePattern_thenReturn400AndValidationResponse() throws Exception {
            createPassenger = getPassengerCreateEditDtoBuilder()
                                .phone("+85699")
                                .build();

            MvcResult mvcResult = mockMvc.perform(post(URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createPassenger)))
                    .andExpect(status().isBadRequest())
                    .andReturn();

            ValidationResponse expectedValidationResponse = new ValidationResponse(
                    List.of(new Violation("phone",
                            "Invalid phone. Possible form: +375-(XX)-XXX-XX-XX or 80(XX)-XXX-XX-XX")));
            ValidationResponse actualResponse = objectMapper.readValue(
                    mvcResult.getResponse().getContentAsString(), ValidationResponse.class);

            assertThat(actualResponse.violations()).containsExactlyInAnyOrderElementsOf(
                    expectedValidationResponse.violations());
        }

        @Test
        void create_whenInvalidEmailPattern_thenReturn400AndValidationResponse() throws Exception {
            createPassenger = getPassengerCreateEditDtoBuilder()
                                .email("passenger.gmail")
                                .build();

            MvcResult mvcResult = mockMvc.perform(post(URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createPassenger)))
                    .andExpect(status().isBadRequest())
                    .andReturn();

            ValidationResponse expectedValidationResponse = new ValidationResponse(
                    List.of(new Violation("email", "Invalid email")));
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
            mockMvc.perform(put(URL_WITH_ID, DEFAULT_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createPassenger)))
                    .andExpect(status().isOk());

            verify(passengerService, times(1)).update(DEFAULT_ID, createPassenger);
        }

        @Test
        void update_whenValidInput_thenMapsToBusinessModel() throws Exception {
            mockMvc.perform(put(URL_WITH_ID, DEFAULT_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createPassenger)))
                    .andExpect(status().isOk());

            ArgumentCaptor<PassengerCreateEditDto> passengerCaptor =
                    ArgumentCaptor.forClass(PassengerCreateEditDto.class);
            ArgumentCaptor<Long> idCaptor = ArgumentCaptor.forClass(Long.class);

            verify(passengerService, times(1)).update(
                    idCaptor.capture(), passengerCaptor.capture());
            assertThat(idCaptor.getValue()).isEqualTo(DEFAULT_ID);
            assertThat(passengerCaptor.getValue().name()).isEqualTo("passenger");
            assertThat(passengerCaptor.getValue().email()).isEqualTo("passenger@gmail.com");
            assertThat(passengerCaptor.getValue().phone()).isEqualTo("+375441234567");
        }

        @Test
        void update_whenValidInput_thenReturn200AndCarReadDto() throws Exception {
            when(passengerService.update(DEFAULT_ID, createPassenger)).thenReturn(readPassenger);

            MvcResult mvcResult = mockMvc.perform(put(URL_WITH_ID, DEFAULT_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createPassenger)))
                    .andExpect(status().isOk())
                    .andReturn();

            String actualResponseBody = mvcResult.getResponse().getContentAsString();
            assertThat(actualResponseBody).isEqualToIgnoringWhitespace(
                    objectMapper.writeValueAsString(readPassenger));
        }

        @Test
        void update_whenInvalidInput_thenReturn400AndValidationResponse() throws Exception {
            createPassenger = getPassengerCreateEditDtoBuilder()
                                .name(null)
                                .email(null)
                                .phone(null)
                                .build();

            MvcResult mvcResult = mockMvc.perform(put(URL_WITH_ID, DEFAULT_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createPassenger)))
                    .andExpect(status().isBadRequest())
                    .andReturn();

            ValidationResponse expectedValidationResponse = new ValidationResponse(
                    List.of(new Violation("name", "Name cannot be blank"),
                            new Violation("email", "Email cannot be blank"),
                            new Violation("phone", "Phone cannot be blank")));
            ValidationResponse actualResponse = objectMapper.readValue(
                    mvcResult.getResponse().getContentAsString(), ValidationResponse.class);

            assertThat(actualResponse.violations()).containsExactlyInAnyOrderElementsOf(
                    expectedValidationResponse.violations());
        }

        @Test
        void update_whenInvalidPhonePattern_thenReturn400AndValidationResponse() throws Exception {
            createPassenger = getPassengerCreateEditDtoBuilder()
                                .phone("+87522")
                                .build();

            MvcResult mvcResult = mockMvc.perform(put(URL_WITH_ID, DEFAULT_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createPassenger)))
                    .andExpect(status().isBadRequest())
                    .andReturn();

            ValidationResponse expectedValidationResponse = new ValidationResponse(
                    List.of(new Violation("phone",
                            "Invalid phone. Possible form: +375-(XX)-XXX-XX-XX or 80(XX)-XXX-XX-XX")));
            ValidationResponse actualResponse = objectMapper.readValue(
                    mvcResult.getResponse().getContentAsString(), ValidationResponse.class);

            assertThat(actualResponse.violations()).containsExactlyInAnyOrderElementsOf(
                    expectedValidationResponse.violations());
        }

        @Test
        void update_whenInvalidEmailPattern_thenReturn400AndValidationResponse() throws Exception {
            createPassenger = getPassengerCreateEditDtoBuilder()
                                .email("passenger.gmail")
                                .build();

            MvcResult mvcResult = mockMvc.perform(put(URL_WITH_ID, DEFAULT_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createPassenger)))
                    .andExpect(status().isBadRequest())
                    .andReturn();

            ValidationResponse expectedValidationResponse = new ValidationResponse(
                    List.of(new Violation("email", "Invalid email")));
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
        void delete_whenVerifyingRequestMatching_thenReturn401() throws Exception {
            mockMvc.perform(delete(URL_WITH_ID, DEFAULT_ID))
                    .andExpect(status().isNoContent());

            verify(passengerService, times(1)).safeDelete(DEFAULT_ID);
        }
    }
}
