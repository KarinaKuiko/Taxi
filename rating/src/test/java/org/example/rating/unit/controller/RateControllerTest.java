package org.example.rating.unit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.rating.config.MessageSourceConfig;
import org.example.rating.controller.RateController;
import org.example.rating.dto.create.RateCreateEditDto;
import org.example.rating.dto.read.RateReadDto;
import org.example.rating.dto.read.ValidationResponse;
import org.example.rating.entity.enumeration.UserType;
import org.example.rating.exception.violation.Violation;
import org.example.rating.service.impl.DriverRateService;
import org.example.rating.service.impl.PassengerRateService;
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
import static org.example.rating.util.DataUtil.DEFAULT_COMMENT;
import static org.example.rating.util.DataUtil.DEFAULT_ID;
import static org.example.rating.util.DataUtil.DEFAULT_RATE;
import static org.example.rating.util.DataUtil.DRIVER_URL;
import static org.example.rating.util.DataUtil.DRIVER_URL_WITH_ID;
import static org.example.rating.util.DataUtil.LIMIT;
import static org.example.rating.util.DataUtil.LIMIT_VALUE;
import static org.example.rating.util.DataUtil.PAGE;
import static org.example.rating.util.DataUtil.PAGE_VALUE;
import static org.example.rating.util.DataUtil.PASSENGER_URL;
import static org.example.rating.util.DataUtil.PASSENGER_URL_WITH_ID;
import static org.example.rating.util.DataUtil.URL;
import static org.example.rating.util.DataUtil.URL_WITH_ID;
import static org.example.rating.util.DataUtil.getDriverRateCreateEditDtoBuilder;
import static org.example.rating.util.DataUtil.getDriverRateReadDtoBuilder;
import static org.example.rating.util.DataUtil.getPassengerRateCreateEditDtoBuilder;
import static org.example.rating.util.DataUtil.getPassengerRateReadDtoBuilder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = RateController.class)
@Import(MessageSourceConfig.class)
class RateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DriverRateService driverRateService;

    @MockBean
    private PassengerRateService passengerRateService;

    @Nested
    @DisplayName("Find all tests")
    public class findAllTests {
        @Test
        void findAllDriversRates_whenVerifyingRequestMatchingWithoutParams_thenReturn200() throws Exception {
            RateReadDto driverRateReadDto = getDriverRateReadDtoBuilder().build();
            Page<RateReadDto> ratePage = new PageImpl<>(List.of(driverRateReadDto),
                    PageRequest.of(PAGE_VALUE, LIMIT_VALUE), 1);

            when(driverRateService.findAll(PAGE_VALUE, LIMIT_VALUE)).thenReturn(ratePage);

            mockMvc.perform(get(DRIVER_URL))
                    .andExpect(status().isOk());
        }

        @Test
        void findAllDriversRates_whenCorrectParams_thenReturn200() throws Exception {
            RateReadDto driverRateReadDto = getDriverRateReadDtoBuilder().build();
            Page<RateReadDto> ratePage = new PageImpl<>(List.of(driverRateReadDto),
                    PageRequest.of(PAGE_VALUE, LIMIT_VALUE), 1);

            when(driverRateService.findAll(PAGE_VALUE, LIMIT_VALUE)).thenReturn(ratePage);

            mockMvc.perform(get(DRIVER_URL)
                            .param(PAGE, PAGE_VALUE.toString())
                            .param(LIMIT, LIMIT_VALUE.toString()))
                    .andExpect(status().isOk());
        }

        @Test
        void findAllDriversRates_whenLimitIsGreaterThanMax_thenReturn400AndValidationResponse() throws Exception {
            MvcResult mvcResult = mockMvc.perform(get(DRIVER_URL)
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
        void findAllDriversRates_whenLimitIsLessThanMin_thenReturn400AndValidationResponse() throws Exception {
            MvcResult mvcResult = mockMvc.perform(get(DRIVER_URL)
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

        @Test
        void findAllPassengersRates_whenVerifyingRequestMatchingWithoutParams_thenReturn200() throws Exception {
            RateReadDto passengerRateReadDto = getPassengerRateReadDtoBuilder().build();
            Page<RateReadDto> ratePage = new PageImpl<>(List.of(passengerRateReadDto),
                    PageRequest.of(PAGE_VALUE, LIMIT_VALUE), 1);

            when(passengerRateService.findAll(PAGE_VALUE, LIMIT_VALUE)).thenReturn(ratePage);

            mockMvc.perform(get(PASSENGER_URL))
                    .andExpect(status().isOk());
        }

        @Test
        void findAllPassengersRates_whenCorrectParams_thenReturn200() throws Exception {
            RateReadDto passengerRateReadDto = getPassengerRateReadDtoBuilder().build();
            Page<RateReadDto> ratePage = new PageImpl<>(List.of(passengerRateReadDto),
                    PageRequest.of(PAGE_VALUE, LIMIT_VALUE), 1);

            when(passengerRateService.findAll(PAGE_VALUE, LIMIT_VALUE)).thenReturn(ratePage);

            mockMvc.perform(get(PASSENGER_URL)
                            .param(PAGE, PAGE_VALUE.toString())
                            .param(LIMIT, LIMIT_VALUE.toString()))
                    .andExpect(status().isOk());
        }

        @Test
        void findAllPassengersRates_whenLimitIsGreaterThanMax_thenReturn400AndValidationResponse() throws Exception {
            MvcResult mvcResult = mockMvc.perform(get(PASSENGER_URL)
                            .param(PAGE, PAGE_VALUE.toString())
                            .param(LIMIT,  "101"))
                    .andExpect(status().isBadRequest())
                    .andReturn();

            ValidationResponse expextedValidationResponse = new ValidationResponse(
                    List.of(new Violation(LIMIT, "must be less than or equal to 100")));
            String actualResponse = mvcResult.getResponse().getContentAsString();

            assertThat(actualResponse).isEqualToIgnoringWhitespace(
                    objectMapper.writeValueAsString(expextedValidationResponse));
        }

        @Test
        void findAllPassengersRates_whenLimitIsLessThanMin_thenReturn400AndValidationResponse() throws Exception {
            MvcResult mvcResult = mockMvc.perform(get(PASSENGER_URL)
                            .param(PAGE, PAGE_VALUE.toString())
                            .param(LIMIT,  "0"))
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
        void findDriverRateById_whenVerifyingRequestMatching_thenReturn200() throws Exception {
            RateReadDto driverRateReadDto = getDriverRateReadDtoBuilder().build();

            when(driverRateService.findById(DEFAULT_ID)).thenReturn(driverRateReadDto);

            MvcResult mvcResult = mockMvc.perform(get(DRIVER_URL_WITH_ID, DEFAULT_ID))
                    .andExpect(status().isOk())
                    .andReturn();

            String actual = mvcResult.getResponse().getContentAsString();

            assertThat(actual).isEqualToIgnoringWhitespace(
                    objectMapper.writeValueAsString(driverRateReadDto));
        }

        @Test
        void findPassengerRateById_whenVerifyingRequestMatching_thenReturn200() throws Exception {
            RateReadDto passengerRateReadDto = getPassengerRateReadDtoBuilder().build();

            when(passengerRateService.findById(DEFAULT_ID)).thenReturn(passengerRateReadDto);

            MvcResult mvcResult = mockMvc.perform(get(PASSENGER_URL_WITH_ID, DEFAULT_ID))
                    .andExpect(status().isOk())
                    .andReturn();

            String actual = mvcResult.getResponse().getContentAsString();

            assertThat(actual).isEqualToIgnoringWhitespace(
                    objectMapper.writeValueAsString(passengerRateReadDto));
        }
    }

    @Nested
    @DisplayName("Create tests")
    public class createTests {
        @Test
        void create_whenVerifyingRequestMatching_thenReturn200() throws Exception {
            RateCreateEditDto driverCreateEditDto = getDriverRateCreateEditDtoBuilder().build();

            mockMvc.perform(post(URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(driverCreateEditDto)))
                    .andExpect(status().isCreated())
                    .andReturn();

            verify(driverRateService, times(1)).create(driverCreateEditDto);
            verify(passengerRateService, times(0)).create(driverCreateEditDto);
        }

        @Test
        void createDriverRate_whenValidInput_thenMapsToBusinessModel() throws Exception {
            RateCreateEditDto driverCreateEditDto = getDriverRateCreateEditDtoBuilder().build();

            mockMvc.perform(post(URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(driverCreateEditDto)))
                    .andExpect(status().isCreated())
                    .andReturn();

            ArgumentCaptor<RateCreateEditDto> rateCaptor = ArgumentCaptor.forClass(RateCreateEditDto.class);

            verify(driverRateService, times(1)).create(rateCaptor.capture());
            assertThat(rateCaptor.getValue().rideId()).isEqualTo(DEFAULT_ID);
            assertThat(rateCaptor.getValue().comment()).isEqualTo(DEFAULT_COMMENT);
            assertThat(rateCaptor.getValue().rating()).isEqualTo(DEFAULT_RATE);
            assertThat(rateCaptor.getValue().userId()).isEqualTo(DEFAULT_ID);
            assertThat(rateCaptor.getValue().userType()).isEqualTo(UserType.PASSENGER);
        }

        @Test
        void createDriverRate_whenValidInput_thenReturn201AndCarReadDto() throws Exception {
            RateReadDto driverRateReadDto = getDriverRateReadDtoBuilder().build();
            RateCreateEditDto driverCreateEditDto = getDriverRateCreateEditDtoBuilder().build();

            when(driverRateService.create(driverCreateEditDto)).thenReturn(driverRateReadDto);

            MvcResult mvcResult = mockMvc.perform(post(URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(driverCreateEditDto)))
                    .andExpect(status().isCreated())
                    .andReturn();

            String actualResponseBody = mvcResult.getResponse().getContentAsString();
            assertThat(actualResponseBody).isEqualToIgnoringWhitespace(
                    objectMapper.writeValueAsString(driverRateReadDto));
        }

        @Test
        void createPassengerRate_whenValidInput_thenMapsToBusinessModel() throws Exception {
            RateCreateEditDto passengerCreateEditDto = getPassengerRateCreateEditDtoBuilder().build();

            mockMvc.perform(post(URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(passengerCreateEditDto)))
                    .andExpect(status().isCreated())
                    .andReturn();

            ArgumentCaptor<RateCreateEditDto> rateCaptor = ArgumentCaptor.forClass(RateCreateEditDto.class);

            verify(passengerRateService, times(1)).create(rateCaptor.capture());
            assertThat(rateCaptor.getValue().rideId()).isEqualTo(DEFAULT_ID);
            assertThat(rateCaptor.getValue().comment()).isEqualTo(DEFAULT_COMMENT);
            assertThat(rateCaptor.getValue().rating()).isEqualTo(DEFAULT_RATE);
            assertThat(rateCaptor.getValue().userId()).isEqualTo(DEFAULT_ID);
            assertThat(rateCaptor.getValue().userType()).isEqualTo(UserType.DRIVER);
        }

        @Test
        void createPassengerRate_whenValidInput_thenReturn201AndCarReadDto() throws Exception {
            RateReadDto passengerRateReadDto = getPassengerRateReadDtoBuilder().build();
            RateCreateEditDto passengerCreateEditDto = getPassengerRateCreateEditDtoBuilder().build();

            when(passengerRateService.create(passengerCreateEditDto)).thenReturn(passengerRateReadDto);

            MvcResult mvcResult = mockMvc.perform(post(URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(passengerCreateEditDto)))
                    .andExpect(status().isCreated())
                    .andReturn();

            String actualResponseBody = mvcResult.getResponse().getContentAsString();
            assertThat(actualResponseBody).isEqualToIgnoringWhitespace(
                    objectMapper.writeValueAsString(passengerRateReadDto));
        }

        @Test
        void create_whenInvalidInputNullAndMax_thenReturn400AndValidationResponse() throws Exception {
            RateCreateEditDto driverCreateEditDto = getDriverRateCreateEditDtoBuilder()
                                    .rideId(null)
                                    .rating(6)
                                    .build();

            MvcResult mvcResult = mockMvc.perform(post(URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(driverCreateEditDto)))
                    .andExpect(status().isBadRequest())
                    .andReturn();

            ValidationResponse expectedValidationResponse = new ValidationResponse(
                    List.of(new Violation("rideId", "Ride can't be null"),
                            new Violation("rating", "Rate should be in range between 1 and 5")));
            ValidationResponse actualResponse = objectMapper.readValue(
                    mvcResult.getResponse().getContentAsString(), ValidationResponse.class);

            assertThat(actualResponse.violations()).containsExactlyInAnyOrderElementsOf(
                    expectedValidationResponse.violations());
        }

        @Test
        void create_whenInvalidInputMin_thenReturn400AndValidationResponse() throws Exception {
            RateCreateEditDto driverCreateEditDto = getDriverRateCreateEditDtoBuilder()
                                    .rating(0)
                                    .build();

            MvcResult mvcResult = mockMvc.perform(post(URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(driverCreateEditDto)))
                    .andExpect(status().isBadRequest())
                    .andReturn();

            ValidationResponse expectedValidationResponse = new ValidationResponse(
                    List.of(new Violation("rating", "Rate should be in range between 1 and 5")));
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
            RateCreateEditDto driverCreateEditDto = getDriverRateCreateEditDtoBuilder().build();

            mockMvc.perform(put(URL_WITH_ID, DEFAULT_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(driverCreateEditDto)))
                    .andExpect(status().isOk());

            verify(driverRateService, times(1)).update(DEFAULT_ID, driverCreateEditDto);
            verify(passengerRateService, times(0)).update(DEFAULT_ID, driverCreateEditDto);
        }

        @Test
        void updateDriverRate_whenValidInput_thenMapsToBusinessModel() throws Exception {
            RateCreateEditDto driverCreateEditDto = getDriverRateCreateEditDtoBuilder().build();

            mockMvc.perform(put(URL_WITH_ID, DEFAULT_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(driverCreateEditDto)))
                    .andExpect(status().isOk());

            ArgumentCaptor<RateCreateEditDto> rateCaptor = ArgumentCaptor.forClass(RateCreateEditDto.class);
            ArgumentCaptor<Long> idCaptor = ArgumentCaptor.forClass(Long.class);

            verify(driverRateService, times(1)).update(idCaptor.capture(), rateCaptor.capture());
            assertThat(idCaptor.getValue()).isEqualTo(DEFAULT_ID);
            assertThat(rateCaptor.getValue().rideId()).isEqualTo(DEFAULT_ID);
            assertThat(rateCaptor.getValue().comment()).isEqualTo(DEFAULT_COMMENT);
            assertThat(rateCaptor.getValue().rating()).isEqualTo(DEFAULT_RATE);
            assertThat(rateCaptor.getValue().userId()).isEqualTo(DEFAULT_ID);
            assertThat(rateCaptor.getValue().userType()).isEqualTo(UserType.PASSENGER);
        }

        @Test
        void updateDriverRate_whenValidInput_thenReturn200AndCarReadDto() throws Exception {
            RateReadDto driverRateReadDto = getDriverRateReadDtoBuilder().build();
            RateCreateEditDto driverCreateEditDto = getDriverRateCreateEditDtoBuilder().build();

            when(driverRateService.update(DEFAULT_ID, driverCreateEditDto)).thenReturn(driverRateReadDto);

            MvcResult mvcResult = mockMvc.perform(put(URL_WITH_ID, DEFAULT_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(driverCreateEditDto)))
                    .andExpect(status().isOk())
                    .andReturn();

            String actualResponseBody = mvcResult.getResponse().getContentAsString();
            assertThat(actualResponseBody).isEqualToIgnoringWhitespace(
                    objectMapper.writeValueAsString(driverRateReadDto));
        }

        @Test
        void updatePassengerRate_whenValidInput_thenMapsToBusinessModel() throws Exception {
            RateCreateEditDto passengerCreateEditDto = getPassengerRateCreateEditDtoBuilder().build();

            mockMvc.perform(put(URL_WITH_ID, DEFAULT_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(passengerCreateEditDto)))
                    .andExpect(status().isOk());

            ArgumentCaptor<RateCreateEditDto> rateCaptor = ArgumentCaptor.forClass(RateCreateEditDto.class);
            ArgumentCaptor<Long> idCaptor = ArgumentCaptor.forClass(Long.class);

            verify(passengerRateService, times(1)).update(idCaptor.capture(), rateCaptor.capture());
            assertThat(idCaptor.getValue()).isEqualTo(DEFAULT_ID);
            assertThat(rateCaptor.getValue().rideId()).isEqualTo(DEFAULT_ID);
            assertThat(rateCaptor.getValue().comment()).isEqualTo(DEFAULT_COMMENT);
            assertThat(rateCaptor.getValue().rating()).isEqualTo(DEFAULT_RATE);
            assertThat(rateCaptor.getValue().userId()).isEqualTo(DEFAULT_ID);
            assertThat(rateCaptor.getValue().userType()).isEqualTo(UserType.DRIVER);
        }

        @Test
        void updatePassengerRate_whenValidInput_thenReturn200AndCarReadDto() throws Exception {
            RateReadDto passengerRateReadDto = getPassengerRateReadDtoBuilder().build();
            RateCreateEditDto passengerCreateEditDto = getPassengerRateCreateEditDtoBuilder().build();

            when(passengerRateService.update(DEFAULT_ID, passengerCreateEditDto)).thenReturn(passengerRateReadDto);

            MvcResult mvcResult = mockMvc.perform(put(URL_WITH_ID, DEFAULT_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(passengerCreateEditDto)))
                    .andExpect(status().isOk())
                    .andReturn();

            String actualResponseBody = mvcResult.getResponse().getContentAsString();
            assertThat(actualResponseBody).isEqualToIgnoringWhitespace(
                    objectMapper.writeValueAsString(passengerRateReadDto));
        }

        @Test
        void update_whenInvalidInputNullAndMax_thenReturn400AndValidationResponse() throws Exception {
            RateCreateEditDto driverCreateEditDto = getDriverRateCreateEditDtoBuilder()
                                    .rideId(null)
                                    .rating(6)
                                    .build();

            MvcResult mvcResult = mockMvc.perform(put(URL_WITH_ID, DEFAULT_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(driverCreateEditDto)))
                    .andExpect(status().isBadRequest())
                    .andReturn();

            ValidationResponse expectedValidationResponse = new ValidationResponse(
                    List.of(new Violation("rideId", "Ride can't be null"),
                            new Violation("rating", "Rate should be in range between 1 and 5")));
            ValidationResponse actualResponse = objectMapper.readValue(
                    mvcResult.getResponse().getContentAsString(), ValidationResponse.class);

            assertThat(actualResponse.violations()).containsExactlyInAnyOrderElementsOf(
                    expectedValidationResponse.violations());
        }

        @Test
        void update_whenInvalidInputMin_thenReturn400AndValidationResponse() throws Exception {
            RateCreateEditDto driverCreateEditDto = getDriverRateCreateEditDtoBuilder()
                                    .rating(0)
                                    .build();

            MvcResult mvcResult = mockMvc.perform(put(URL_WITH_ID, DEFAULT_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(driverCreateEditDto)))
                    .andExpect(status().isBadRequest())
                    .andReturn();

            ValidationResponse expectedValidationResponse = new ValidationResponse(
                    List.of(new Violation("rating", "Rate should be in range between 1 and 5")));
            ValidationResponse actualResponse = objectMapper.readValue(
                    mvcResult.getResponse().getContentAsString(), ValidationResponse.class);

            assertThat(actualResponse.violations()).containsExactlyInAnyOrderElementsOf(
                    expectedValidationResponse.violations());
        }
    }
}
