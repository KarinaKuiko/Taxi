package org.example.driver.unit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.driver.config.MessageSourceConfig;
import org.example.driver.controller.DriverController;
import org.example.driver.dto.create.DriverCreateEditDto;
import org.example.driver.dto.read.DriverReadDto;
import org.example.driver.dto.read.ValidationResponse;
import org.example.driver.entity.enumeration.Gender;
import org.example.driver.exception.violation.Violation;
import org.example.driver.service.DriverService;
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
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.example.driver.util.DataUtil.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = DriverController.class)
@Import(MessageSourceConfig.class)
@WithMockUser
class DriverControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DriverService driverService;

    @Nested
    @DisplayName("Find all tests")
    public class findAllTests {
        @Test
        void findAll_whenVerifyingRequestMatchingWithoutParams_thenReturn200() throws Exception {
            DriverReadDto readDriver = getDriverReadDtoBuilder().build();
            Page<DriverReadDto> driverPage = new PageImpl<>(List.of(readDriver),
                    PageRequest.of(PAGE_VALUE, LIMIT_VALUE), 1);

            when(driverService.findAll(PAGE_VALUE, LIMIT_VALUE)).thenReturn(driverPage);

            mockMvc.perform(get(URL, DRIVER_ENTITY))
                    .andExpect(status().isOk());
        }

        @Test
        void findAll_whenCorrectParams_thenReturn200() throws Exception {
            DriverReadDto readDriver = getDriverReadDtoBuilder().build();
            Page<DriverReadDto> driverPage = new PageImpl<>(List.of(readDriver),
                    PageRequest.of(PAGE_VALUE, LIMIT_VALUE), 1);

            when(driverService.findAll(PAGE_VALUE, LIMIT_VALUE)).thenReturn(driverPage);

            mockMvc.perform(get(URL, DRIVER_ENTITY)
                            .param(PAGE, PAGE_VALUE.toString())
                            .param(LIMIT, LIMIT_VALUE.toString()))
                    .andExpect(status().isOk());
        }

        @Test
        void findAll_whenLimitIsGreaterThanMax_thenReturn400AndValidationResponse() throws Exception {
            MvcResult mvcResult = mockMvc.perform(get(URL, DRIVER_ENTITY)
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
            MvcResult mvcResult = mockMvc.perform(get(URL, DRIVER_ENTITY)
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
            DriverReadDto readDriver = getDriverReadDtoBuilder().build();

            when(driverService.findById(DEFAULT_ID)).thenReturn(readDriver);

            MvcResult mvcResult = mockMvc.perform(get(URL_WITH_ID, DRIVER_ENTITY, DEFAULT_ID))
                    .andExpect(status().isOk())
                    .andReturn();

            String actual = mvcResult.getResponse().getContentAsString();

            assertThat(actual).isEqualToIgnoringWhitespace(
                    objectMapper.writeValueAsString(readDriver));
        }
    }

    @Nested
    @DisplayName("Create tests")
    public class createTests {
        @Test
        void create_whenVerifyingRequestMatching_thenReturn200() throws Exception {
            DriverCreateEditDto createDriver = getDriverCreateEditDtoBuilder().build();

            mockMvc.perform(post(URL, DRIVER_ENTITY)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createDriver))
                            .with(csrf()))
                    .andExpect(status().isCreated())
                    .andReturn();

            verify(driverService, times(1)).create(createDriver);
        }

        @Test
        void create_whenValidInput_thenMapsToBusinessModel() throws Exception {
            DriverCreateEditDto createDriver = getDriverCreateEditDtoBuilder().build();

            mockMvc.perform(post(URL, DRIVER_ENTITY)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createDriver))
                            .with(csrf()))
                    .andExpect(status().isCreated())
                    .andReturn();

            ArgumentCaptor<DriverCreateEditDto> driverCaptor = ArgumentCaptor.forClass(DriverCreateEditDto.class);

            verify(driverService, times(1)).create(driverCaptor.capture());
            assertThat(driverCaptor.getValue().firstName()).isEqualTo(DEFAULT_NAME);
            assertThat(driverCaptor.getValue().lastName()).isEqualTo(DEFAULT_NAME);
            assertThat(driverCaptor.getValue().email()).isEqualTo(DEFAULT_EMAIL);
            assertThat(driverCaptor.getValue().phone()).isEqualTo(DEFAULT_PHONE);
            assertThat(driverCaptor.getValue().gender()).isEqualTo(Gender.MALE);
            assertThat(driverCaptor.getValue().carCreateEditDto()).isEqualTo(getCarCreateEditDtoBuilder().build());
        }

        @Test
        void create_whenValidInput_thenReturn201AndCarReadDto() throws Exception {
            DriverReadDto readDriver = getDriverReadDtoBuilder().build();
            DriverCreateEditDto createDriver = getDriverCreateEditDtoBuilder().build();

            when(driverService.create(createDriver)).thenReturn(readDriver);

            MvcResult mvcResult = mockMvc.perform(post(URL, DRIVER_ENTITY)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createDriver))
                            .with(csrf()))
                    .andExpect(status().isCreated())
                    .andReturn();

            String actualResponseBody = mvcResult.getResponse().getContentAsString();
            assertThat(actualResponseBody).isEqualToIgnoringWhitespace(
                    objectMapper.writeValueAsString(readDriver));
        }

        @Test
        void create_whenInvalidInput_thenReturn400AndValidationResponse() throws Exception {
            DriverCreateEditDto createDriver = getDriverCreateEditDtoBuilder()
                            .firstName(null)
                            .lastName(null)
                            .email(null)
                            .phone(null)
                            .build();

            MvcResult mvcResult = mockMvc.perform(post(URL, DRIVER_ENTITY)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createDriver))
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

        @Test
        void create_whenInvalidEmailPattern_thenReturn400AndValidationResponse() throws Exception {
            DriverCreateEditDto createDriver = getDriverCreateEditDtoBuilder()
                            .email("test.gmail")
                            .build();

            MvcResult mvcResult = mockMvc.perform(post(URL, DRIVER_ENTITY)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createDriver))
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

        @ParameterizedTest
        @ValueSource(strings = {"375441234567", "+37544123456", "+375551234567", "+546", "8079265"})
        void create_whenInvalidPhonePattern_thenReturn400AndValidationResponse(String number) throws Exception {
            DriverCreateEditDto createDriver = getDriverCreateEditDtoBuilder()
                            .phone(number)
                            .build();

            MvcResult mvcResult = mockMvc.perform(post(URL, DRIVER_ENTITY)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createDriver))
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
    }

    @Nested
    @DisplayName("Update tests")
    public class updateTests {
        @Test
        void update_whenValidInput_thenReturn200() throws Exception {
            DriverCreateEditDto createDriver = getDriverCreateEditDtoBuilder().build();

            mockMvc.perform(put(URL_WITH_ID, DRIVER_ENTITY, DEFAULT_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createDriver))
                            .with(csrf()))
                    .andExpect(status().isOk());

            verify(driverService, times(1)).update(DEFAULT_ID, createDriver);
        }

        @Test
        void update_whenValidInput_thenMapsToBusinessModel() throws Exception {
            DriverCreateEditDto createDriver = getDriverCreateEditDtoBuilder().build();

            mockMvc.perform(put(URL_WITH_ID, DRIVER_ENTITY, DEFAULT_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createDriver))
                            .with(csrf()))
                    .andExpect(status().isOk());

            ArgumentCaptor<DriverCreateEditDto> driverCaptor = ArgumentCaptor.forClass(DriverCreateEditDto.class);
            ArgumentCaptor<Long> idCaptor = ArgumentCaptor.forClass(Long.class);

            verify(driverService, times(1)).update(idCaptor.capture(), driverCaptor.capture());
            assertThat(idCaptor.getValue()).isEqualTo(DEFAULT_ID);
            assertThat(driverCaptor.getValue().firstName()).isEqualTo(DEFAULT_NAME);
            assertThat(driverCaptor.getValue().lastName()).isEqualTo(DEFAULT_NAME);
            assertThat(driverCaptor.getValue().email()).isEqualTo(DEFAULT_EMAIL);
            assertThat(driverCaptor.getValue().phone()).isEqualTo(DEFAULT_PHONE);
            assertThat(driverCaptor.getValue().gender()).isEqualTo(Gender.MALE);
            assertThat(driverCaptor.getValue().carCreateEditDto()).isEqualTo(getCarCreateEditDtoBuilder().build());
        }

        @Test
        void update_whenValidInput_thenReturn200AndCarReadDto() throws Exception {
            DriverReadDto readDriver = getDriverReadDtoBuilder().build();
            DriverCreateEditDto createDriver = getDriverCreateEditDtoBuilder().build();

            when(driverService.update(DEFAULT_ID, createDriver)).thenReturn(readDriver);

            MvcResult mvcResult = mockMvc.perform(put(URL_WITH_ID, DRIVER_ENTITY, DEFAULT_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createDriver))
                            .with(csrf()))
                    .andExpect(status().isOk())
                    .andReturn();

            String actualResponseBody = mvcResult.getResponse().getContentAsString();
            assertThat(actualResponseBody).isEqualToIgnoringWhitespace(
                    objectMapper.writeValueAsString(readDriver));
        }

        @Test
        void update_whenInvalidInput_thenReturn400AndValidationResponse() throws Exception {
            DriverCreateEditDto createDriver = getDriverCreateEditDtoBuilder()
                            .firstName(null)
                            .lastName(null)
                            .email(null)
                            .phone(null)
                            .build();

            MvcResult mvcResult = mockMvc.perform(put(URL_WITH_ID, DRIVER_ENTITY, DEFAULT_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createDriver))
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

        @Test
        void update_whenInvalidEmailPattern_thenReturn400AndValidationResponse() throws Exception {
            DriverCreateEditDto createDriver = getDriverCreateEditDtoBuilder()
                            .email("test.gmail")
                            .build();

            MvcResult mvcResult = mockMvc.perform(put(URL_WITH_ID, DRIVER_ENTITY, DEFAULT_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createDriver))
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

        @ParameterizedTest
        @ValueSource(strings = {"375441234567", "+37544123456", "+375551234567", "+546", "8079265"})
        void update_whenInvalidPhonePattern_thenReturn400AndValidationResponse(String number) throws Exception {
            DriverCreateEditDto createDriver = getDriverCreateEditDtoBuilder()
                            .phone(number)
                            .build();

            MvcResult mvcResult = mockMvc.perform(put(URL_WITH_ID, DRIVER_ENTITY, DEFAULT_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createDriver))
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
    }

    @Nested
    @DisplayName("Delete tests")
    public class deleteTests {
        @Test
        void delete_whenVerifyingRequestMatching_thenReturn401() throws Exception {
            mockMvc.perform(delete(URL_WITH_ID, DRIVER_ENTITY, DEFAULT_ID)
                            .with(csrf()))
                    .andExpect(status().isNoContent());

            verify(driverService, times(1)).safeDelete(DEFAULT_ID);
        }
    }
}
