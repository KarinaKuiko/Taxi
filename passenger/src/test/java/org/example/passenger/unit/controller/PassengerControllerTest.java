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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockPart;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.example.passenger.util.DataUtil.DEFAULT_EMAIL;
import static org.example.passenger.util.DataUtil.DEFAULT_ID;
import static org.example.passenger.util.DataUtil.DEFAULT_NAME;
import static org.example.passenger.util.DataUtil.DEFAULT_PHONE;
import static org.example.passenger.util.DataUtil.LIMIT;
import static org.example.passenger.util.DataUtil.LIMIT_VALUE;
import static org.example.passenger.util.DataUtil.PAGE;
import static org.example.passenger.util.DataUtil.PAGE_VALUE;
import static org.example.passenger.util.DataUtil.URL;
import static org.example.passenger.util.DataUtil.URL_WITH_ID;
import static org.example.passenger.util.DataUtil.getPassengerCreateEditDtoBuilder;
import static org.example.passenger.util.DataUtil.getPassengerReadDtoBuilder;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PassengerController.class)
@Import(MessageSourceConfig.class)
@WithMockUser
class PassengerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PassengerService passengerService;

    @Nested
    @DisplayName("Find all tests")
    public class findAllTests {
        @Test
        void findAll_whenVerifyingRequestMatchingWithoutParams_thenReturn200() throws Exception {
            PassengerReadDto readPassenger = getPassengerReadDtoBuilder().build();
            Page<PassengerReadDto> passengerPage = new PageImpl<>(List.of(readPassenger),
                    PageRequest.of(PAGE_VALUE, LIMIT_VALUE), 1);

            when(passengerService.findAll(PAGE_VALUE, LIMIT_VALUE)).thenReturn(passengerPage);

            mockMvc.perform(get(URL))
                    .andExpect(status().isOk());
        }

        @Test
        void findAll_whenCorrectParams_thenReturn200() throws Exception {
            PassengerReadDto readPassenger = getPassengerReadDtoBuilder().build();
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
            PassengerReadDto readPassenger = getPassengerReadDtoBuilder().build();

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
            PassengerCreateEditDto createPassenger = getPassengerCreateEditDtoBuilder().build();
            MockPart part = new MockPart("dto", objectMapper.writeValueAsString(createPassenger).getBytes());
            part.getHeaders().setContentType(MediaType.APPLICATION_JSON);

            mockMvc.perform(multipart(URL)
                            .part(part)
                            .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                            .with(csrf()))
                    .andExpect(status().isCreated())
                    .andReturn();

            verify(passengerService, times(1)).create(createPassenger, null);
        }

        @Test
        void create_whenValidInput_thenMapsToBusinessModel() throws Exception {
            PassengerCreateEditDto createPassenger = getPassengerCreateEditDtoBuilder().build();
            MockPart part = new MockPart("dto", objectMapper.writeValueAsString(createPassenger).getBytes());
            part.getHeaders().setContentType(MediaType.APPLICATION_JSON);

            mockMvc.perform(multipart(URL)
                            .part(part)
                            .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                            .with(csrf()))
                    .andExpect(status().isCreated())
                    .andReturn();

            ArgumentCaptor<PassengerCreateEditDto> carCaptor = ArgumentCaptor.forClass(PassengerCreateEditDto.class);

            verify(passengerService, times(1)).create(carCaptor.capture(), any());
            assertThat(carCaptor.getValue().firstName()).isEqualTo(DEFAULT_NAME);
            assertThat(carCaptor.getValue().lastName()).isEqualTo(DEFAULT_NAME);
            assertThat(carCaptor.getValue().email()).isEqualTo(DEFAULT_EMAIL);
            assertThat(carCaptor.getValue().phone()).isEqualTo(DEFAULT_PHONE);
        }

        @Test
        void create_whenValidInput_thenReturn201AndCarReadDto() throws Exception {
            PassengerReadDto readPassenger = getPassengerReadDtoBuilder().build();
            PassengerCreateEditDto createPassenger = getPassengerCreateEditDtoBuilder().build();
            MockPart part = new MockPart("dto", objectMapper.writeValueAsString(createPassenger).getBytes());
            part.getHeaders().setContentType(MediaType.APPLICATION_JSON);

            when(passengerService.create(createPassenger, null)).thenReturn(readPassenger);

            MvcResult mvcResult = mockMvc.perform(multipart(URL)
                            .part(part)
                            .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                            .with(csrf()))
                    .andExpect(status().isCreated())
                    .andReturn();

            String actualResponseBody = mvcResult.getResponse().getContentAsString();
            assertThat(actualResponseBody).isEqualToIgnoringWhitespace(
                    objectMapper.writeValueAsString(readPassenger));
        }

        @Test
        void create_whenInvalidInput_thenReturn400AndValidationResponse() throws Exception {
            PassengerCreateEditDto createPassenger = getPassengerCreateEditDtoBuilder()
                                .firstName(null)
                                .lastName(null)
                                .email(null)
                                .phone(null)
                                .build();
            MockPart part = new MockPart("dto", objectMapper.writeValueAsString(createPassenger).getBytes());
            part.getHeaders().setContentType(MediaType.APPLICATION_JSON);

            MvcResult mvcResult = mockMvc.perform(multipart(URL)
                            .part(part)
                            .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                            .with(csrf()))
                    .andExpect(status().isBadRequest())
                    .andReturn();

            ValidationResponse expectedValidationResponse = new ValidationResponse(
                    List.of(new Violation("firstName", "Name cannot be blank"),
                            new Violation("lastName", "Name cannot be blank"),
                            new Violation("email", "Email cannot be blank"),
                            new Violation("phone", "Phone cannot be blank")));
            ValidationResponse actualResponse = objectMapper.readValue(
                    mvcResult.getResponse().getContentAsString(), ValidationResponse.class);

            assertThat(actualResponse.violations()).containsExactlyInAnyOrderElementsOf(
                    expectedValidationResponse.violations());
        }

        @ParameterizedTest
        @ValueSource(strings = {"375441234567", "+37544123456", "+375551234567", "+546", "8079265"})
        void create_whenInvalidPhonePattern_thenReturn400AndValidationResponse(String number) throws Exception {
            PassengerCreateEditDto createPassenger = getPassengerCreateEditDtoBuilder()
                                .phone(number)
                                .build();
            MockPart part = new MockPart("dto", objectMapper.writeValueAsString(createPassenger).getBytes());
            part.getHeaders().setContentType(MediaType.APPLICATION_JSON);

            MvcResult mvcResult = mockMvc.perform(multipart(URL)
                            .part(part)
                            .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                            .with(csrf()))
                    .andExpect(status().isBadRequest())
                    .andReturn();

            ValidationResponse expectedValidationResponse = new ValidationResponse(
                    List.of(new Violation("phone",
                            "Invalid phone. Possible form: +375XXXXXXXXX or 80XXXXXXXXX")));
            ValidationResponse actualResponse = objectMapper.readValue(
                    mvcResult.getResponse().getContentAsString(), ValidationResponse.class);

            assertThat(actualResponse.violations()).containsExactlyInAnyOrderElementsOf(
                    expectedValidationResponse.violations());
        }

        @Test
        void create_whenInvalidEmailPattern_thenReturn400AndValidationResponse() throws Exception {
            PassengerCreateEditDto createPassenger = getPassengerCreateEditDtoBuilder()
                                .email("passenger.gmail")
                                .build();
            MockPart part = new MockPart("dto", objectMapper.writeValueAsString(createPassenger).getBytes());
            part.getHeaders().setContentType(MediaType.APPLICATION_JSON);

            MvcResult mvcResult = mockMvc.perform(multipart(URL)
                            .part(part)
                            .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                            .with(csrf()))
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
            PassengerCreateEditDto createPassenger = getPassengerCreateEditDtoBuilder().build();
            MockPart part = new MockPart("dto", objectMapper.writeValueAsString(createPassenger).getBytes());
            part.getHeaders().setContentType(MediaType.APPLICATION_JSON);

            mockMvc.perform(multipart(HttpMethod.PUT, URL_WITH_ID, DEFAULT_ID)
                            .part(part)
                            .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                            .with(csrf()))
                    .andExpect(status().isOk());

            verify(passengerService, times(1)).update(DEFAULT_ID, createPassenger, null);
        }

        @Test
        void update_whenValidInput_thenMapsToBusinessModel() throws Exception {
            PassengerCreateEditDto createPassenger = getPassengerCreateEditDtoBuilder().build();
            MockPart part = new MockPart("dto", objectMapper.writeValueAsString(createPassenger).getBytes());
            part.getHeaders().setContentType(MediaType.APPLICATION_JSON);

            mockMvc.perform(multipart(HttpMethod.PUT, URL_WITH_ID, DEFAULT_ID)
                            .part(part)
                            .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                            .with(csrf()))
                    .andExpect(status().isOk());

            ArgumentCaptor<PassengerCreateEditDto> passengerCaptor =
                    ArgumentCaptor.forClass(PassengerCreateEditDto.class);
            ArgumentCaptor<Long> idCaptor = ArgumentCaptor.forClass(Long.class);

            verify(passengerService, times(1)).update(
                    idCaptor.capture(), passengerCaptor.capture(), any());
            assertThat(idCaptor.getValue()).isEqualTo(DEFAULT_ID);
            assertThat(passengerCaptor.getValue().firstName()).isEqualTo(DEFAULT_NAME);
            assertThat(passengerCaptor.getValue().lastName()).isEqualTo(DEFAULT_NAME);
            assertThat(passengerCaptor.getValue().email()).isEqualTo(DEFAULT_EMAIL);
            assertThat(passengerCaptor.getValue().phone()).isEqualTo(DEFAULT_PHONE);
        }

        @Test
        void update_whenValidInput_thenReturn200AndCarReadDto() throws Exception {
            PassengerReadDto readPassenger = getPassengerReadDtoBuilder().build();
            PassengerCreateEditDto createPassenger = getPassengerCreateEditDtoBuilder().build();
            MockPart part = new MockPart("dto", objectMapper.writeValueAsString(createPassenger).getBytes());
            part.getHeaders().setContentType(MediaType.APPLICATION_JSON);

            when(passengerService.update(DEFAULT_ID, createPassenger, null)).thenReturn(readPassenger);

            MvcResult mvcResult = mockMvc.perform(multipart(HttpMethod.PUT, URL_WITH_ID, DEFAULT_ID)
                            .part(part)
                            .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                            .with(csrf()))
                    .andExpect(status().isOk())
                    .andReturn();

            String actualResponseBody = mvcResult.getResponse().getContentAsString();
            assertThat(actualResponseBody).isEqualToIgnoringWhitespace(
                    objectMapper.writeValueAsString(readPassenger));
        }

        @Test
        void update_whenInvalidInput_thenReturn400AndValidationResponse() throws Exception {
            PassengerCreateEditDto createPassenger = getPassengerCreateEditDtoBuilder()
                                .firstName(null)
                                .lastName(null)
                                .email(null)
                                .phone(null)
                                .build();
            MockPart part = new MockPart("dto", objectMapper.writeValueAsString(createPassenger).getBytes());
            part.getHeaders().setContentType(MediaType.APPLICATION_JSON);

            MvcResult mvcResult = mockMvc.perform(multipart(HttpMethod.PUT, URL_WITH_ID, DEFAULT_ID)
                            .part(part)
                            .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                            .with(csrf()))
                    .andExpect(status().isBadRequest())
                    .andReturn();

            ValidationResponse expectedValidationResponse = new ValidationResponse(
                    List.of(new Violation("firstName", "Name cannot be blank"),
                            new Violation("lastName", "Name cannot be blank"),
                            new Violation("email", "Email cannot be blank"),
                            new Violation("phone", "Phone cannot be blank")));
            ValidationResponse actualResponse = objectMapper.readValue(
                    mvcResult.getResponse().getContentAsString(), ValidationResponse.class);

            assertThat(actualResponse.violations()).containsExactlyInAnyOrderElementsOf(
                    expectedValidationResponse.violations());
        }

        @ParameterizedTest
        @ValueSource(strings = {"375441234567", "+37544123456", "+375551234567", "+546", "8079265"})
        void update_whenInvalidPhonePattern_thenReturn400AndValidationResponse(String number) throws Exception {
            PassengerCreateEditDto createPassenger = getPassengerCreateEditDtoBuilder()
                                .phone(number)
                                .build();
            MockPart part = new MockPart("dto", objectMapper.writeValueAsString(createPassenger).getBytes());
            part.getHeaders().setContentType(MediaType.APPLICATION_JSON);

            MvcResult mvcResult = mockMvc.perform(multipart(HttpMethod.PUT, URL_WITH_ID, DEFAULT_ID)
                            .part(part)
                            .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                            .with(csrf()))
                    .andExpect(status().isBadRequest())
                    .andReturn();

            ValidationResponse expectedValidationResponse = new ValidationResponse(
                    List.of(new Violation("phone",
                            "Invalid phone. Possible form: +375XXXXXXXXX or 80XXXXXXXXX")));
            ValidationResponse actualResponse = objectMapper.readValue(
                    mvcResult.getResponse().getContentAsString(), ValidationResponse.class);

            assertThat(actualResponse.violations()).containsExactlyInAnyOrderElementsOf(
                    expectedValidationResponse.violations());
        }

        @Test
        void update_whenInvalidEmailPattern_thenReturn400AndValidationResponse() throws Exception {
            PassengerCreateEditDto createPassenger = getPassengerCreateEditDtoBuilder()
                                .email("passenger.gmail")
                                .build();
            MockPart part = new MockPart("dto", objectMapper.writeValueAsString(createPassenger).getBytes());
            part.getHeaders().setContentType(MediaType.APPLICATION_JSON);

            MvcResult mvcResult = mockMvc.perform(multipart(HttpMethod.PUT, URL_WITH_ID, DEFAULT_ID)
                            .part(part)
                            .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                            .with(csrf()))
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
            mockMvc.perform(delete(URL_WITH_ID, DEFAULT_ID)
                            .with(csrf()))
                    .andExpect(status().isNoContent());

            verify(passengerService, times(1)).safeDelete(DEFAULT_ID);
        }
    }
}
