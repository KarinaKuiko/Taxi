package org.example.rating.unit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.rating.controller.RateController;
import org.example.rating.dto.create.RateCreateEditDto;
import org.example.rating.dto.read.RateReadDto;
import org.example.rating.dto.read.ValidationResponse;
import org.example.rating.entity.enumeration.UserType;
import org.example.rating.exception.violation.Violation;
import org.example.rating.service.impl.DriverRateService;
import org.example.rating.service.impl.PassengerRateService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = RateController.class)
public class RateControllerTest {
    private static final String URL = "/api/v1/rates";
    private static final String DRIVER_URL = "/driver";
    private static final String PASSENGER_URL = "/passenger";
    private static final Long DEFAULT_ID_PASSENGER = 1L;
    private static final Long DEFAULT_ID_DRIVER = 2L;


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DriverRateService driverRateService;

    @MockBean
    private PassengerRateService passengerRateService;

    private List<RateReadDto> rateList;
    private Page<RateReadDto> ratePage;
    private RateReadDto defaultPassengerRate;
    private RateReadDto defaultDriverRate;

    @BeforeEach
    void init() {
        defaultPassengerRate = new RateReadDto(DEFAULT_ID_PASSENGER, DEFAULT_ID_PASSENGER, "comment", 5, DEFAULT_ID_PASSENGER, UserType.DRIVER);
        defaultDriverRate = new RateReadDto(DEFAULT_ID_DRIVER, DEFAULT_ID_PASSENGER, "comment", 4, DEFAULT_ID_PASSENGER, UserType.PASSENGER);
        rateList = List.of(defaultPassengerRate, defaultDriverRate);
        ratePage = new PageImpl<>(rateList, PageRequest.of(0, 10), 2);
    }

    @Test
    void findAllDriversRates_whenVerifyingRequestMatchingWithoutParams_thenReturn200() throws Exception {
        when(driverRateService.findAll(0, 10)).thenReturn(ratePage);

        mockMvc.perform(get(URL + DRIVER_URL))
                .andExpect(status().isOk());
    }

    @Test
    void findAllDriversRates_whenCorrectParams_thenReturn200() throws Exception {
        when(driverRateService.findAll(0, 10)).thenReturn(ratePage);

        mockMvc.perform(get(URL + DRIVER_URL)
                        .param("page", "0")
                        .param("limit", "10"))
                .andExpect(status().isOk());
    }

    @Test
    void findAllDriversRates_whenLimitIsGreaterThanMax_thenReturn400AndValidationResponse() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get(URL + DRIVER_URL)
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
    void findAllDriversRates_whenLimitIsLessThanMin_thenReturn400AndValidationResponse() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get(URL + DRIVER_URL)
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
    void findAllPassengersRates_whenVerifyingRequestMatchingWithoutParams_thenReturn200() throws Exception {
        when(passengerRateService.findAll(0, 10)).thenReturn(ratePage);

        mockMvc.perform(get(URL + PASSENGER_URL))
                .andExpect(status().isOk());
    }

    @Test
    void findAllPassengersRates_whenCorrectParams_thenReturn200() throws Exception {
        when(passengerRateService.findAll(0, 10)).thenReturn(ratePage);

        mockMvc.perform(get(URL + PASSENGER_URL)
                        .param("page", "0")
                        .param("limit", "10"))
                .andExpect(status().isOk());
    }

    @Test
    void findAllPassengersRates_whenLimitIsGreaterThanMax_thenReturn400AndValidationResponse() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get(URL + PASSENGER_URL)
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
    void findAllPassengersRates_whenLimitIsLessThanMin_thenReturn400AndValidationResponse() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get(URL + PASSENGER_URL)
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
    void findByDriverId_whenValidInput_thenReturn200() throws Exception {
        when(driverRateService.findById(DEFAULT_ID_DRIVER)).thenReturn(rateList.get(1));

        MvcResult mvcResult = mockMvc.perform(get(URL + DRIVER_URL + "/{id}", DEFAULT_ID_DRIVER))
                .andExpect(status().isOk())
                .andReturn();

        String actual = mvcResult.getResponse().getContentAsString();

        assertThat(actual).isEqualToIgnoringWhitespace(
                objectMapper.writeValueAsString(defaultDriverRate));
    }

    @Test
    void findByDriverId_whenVerifyingRequestMatching_thenReturn200() throws Exception {
        when(driverRateService.findById(DEFAULT_ID_DRIVER)).thenReturn(rateList.get(1));

        MvcResult mvcResult = mockMvc.perform(get(URL + DRIVER_URL + "/2"))
                .andExpect(status().isOk())
                .andReturn();

        String actual = mvcResult.getResponse().getContentAsString();

        assertThat(actual).isEqualToIgnoringWhitespace(
                objectMapper.writeValueAsString(defaultDriverRate));
    }

    @Test
    void findByPassengerId_whenValidInput_thenReturn200() throws Exception {
        when(passengerRateService.findById(DEFAULT_ID_PASSENGER)).thenReturn(rateList.get(0));

        MvcResult mvcResult = mockMvc.perform(get(URL + PASSENGER_URL + "/{id}", DEFAULT_ID_PASSENGER))
                .andExpect(status().isOk())
                .andReturn();

        String actual = mvcResult.getResponse().getContentAsString();

        assertThat(actual).isEqualToIgnoringWhitespace(
                objectMapper.writeValueAsString(defaultPassengerRate));
    }

    @Test
    void findByPassengerId_whenVerifyingRequestMatching_thenReturn200() throws Exception {
        when(passengerRateService.findById(DEFAULT_ID_PASSENGER)).thenReturn(rateList.get(0));

        MvcResult mvcResult = mockMvc.perform(get(URL + PASSENGER_URL + "/1"))
                .andExpect(status().isOk())
                .andReturn();

        String actual = mvcResult.getResponse().getContentAsString();

        assertThat(actual).isEqualToIgnoringWhitespace(
                objectMapper.writeValueAsString(defaultPassengerRate));
    }

    @Test
    void create_whenVerifyingRequestMatching_thenReturn200() throws Exception {
        RateCreateEditDto createRate = new RateCreateEditDto(2L, "Good", 4, 2L, UserType.PASSENGER);

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRate)))
                .andExpect(status().isCreated())
                .andReturn();
    }

    @Test
    void createDriverRate_whenValidInput_thenMapsToBusinessModel() throws Exception {
        RateCreateEditDto createRate = new RateCreateEditDto(2L, "Good", 4, 2L, UserType.PASSENGER);

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRate)))
                .andExpect(status().isCreated())
                .andReturn();

        ArgumentCaptor<RateCreateEditDto> rateCaptor = ArgumentCaptor.forClass(RateCreateEditDto.class);

        verify(driverRateService, times(1)).create(rateCaptor.capture());
        assertThat(rateCaptor.getValue().rideId()).isEqualTo(2L);
        assertThat(rateCaptor.getValue().comment()).isEqualTo("Good");
        assertThat(rateCaptor.getValue().rating()).isEqualTo(4);
        assertThat(rateCaptor.getValue().userId()).isEqualTo(2L);
        assertThat(rateCaptor.getValue().userType()).isEqualTo(UserType.PASSENGER);
    }

    @Test
    void createDriverRate_whenValidInput_thenReturn201AndCarReadDto() throws Exception {
        RateCreateEditDto createRate = new RateCreateEditDto(2L, "Good", 4, 2L, UserType.PASSENGER);
        RateReadDto readRate = new RateReadDto(3L, 2L, "Good", 4, 2L, UserType.PASSENGER);

        when(driverRateService.create(createRate)).thenReturn(readRate);

        MvcResult mvcResult = mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRate)))
                .andExpect(status().isCreated())
                .andReturn();

        String actualResponseBody = mvcResult.getResponse().getContentAsString();
        assertThat(actualResponseBody).isEqualToIgnoringWhitespace(
                objectMapper.writeValueAsString(readRate));
    }

    @Test
    void createPassengerRate_whenValidInput_thenMapsToBusinessModel() throws Exception {
        RateCreateEditDto createRate = new RateCreateEditDto(2L, "Good", 4, 2L, UserType.DRIVER);

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRate)))
                .andExpect(status().isCreated())
                .andReturn();

        ArgumentCaptor<RateCreateEditDto> rateCaptor = ArgumentCaptor.forClass(RateCreateEditDto.class);

        verify(passengerRateService, times(1)).create(rateCaptor.capture());
        assertThat(rateCaptor.getValue().rideId()).isEqualTo(2L);
        assertThat(rateCaptor.getValue().comment()).isEqualTo("Good");
        assertThat(rateCaptor.getValue().rating()).isEqualTo(4);
        assertThat(rateCaptor.getValue().userId()).isEqualTo(2L);
        assertThat(rateCaptor.getValue().userType()).isEqualTo(UserType.DRIVER);
    }

    @Test
    void createPassengerRate_whenValidInput_thenReturn201AndCarReadDto() throws Exception {
        RateCreateEditDto createRate = new RateCreateEditDto(2L, "Good", 4, 2L, UserType.DRIVER);
        RateReadDto readRate = new RateReadDto(3L, 2L, "Good", 4, 2L, UserType.DRIVER);

        when(passengerRateService.create(createRate)).thenReturn(readRate);

        MvcResult mvcResult = mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRate)))
                .andExpect(status().isCreated())
                .andReturn();

        String actualResponseBody = mvcResult.getResponse().getContentAsString();
        assertThat(actualResponseBody).isEqualToIgnoringWhitespace(
                objectMapper.writeValueAsString(readRate));
    }

    @Test
    void create_whenInvalidInputNullAndMax_thenReturn400AndValidationResponse() throws Exception {
        RateCreateEditDto createRate = new RateCreateEditDto(null, "Good", 6, 2L, UserType.PASSENGER);

        MvcResult mvcResult = mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRate)))
                .andExpect(status().isBadRequest())
                .andReturn();

        ValidationResponse expectedValidationResponse = new ValidationResponse(
                List.of(new Violation("rideId", "{ride.null}"),
                        new Violation("rating", "{rating.range}")));
        ValidationResponse actualResponse = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ValidationResponse.class);

        assertThat(actualResponse.violations()).containsExactlyInAnyOrderElementsOf(expectedValidationResponse.violations());
    }

    @Test
    void create_whenInvalidInputNullAndMin_thenReturn400AndValidationResponse() throws Exception {
        RateCreateEditDto createRate = new RateCreateEditDto(null, "Good", 0, 2L, UserType.PASSENGER);

        MvcResult mvcResult = mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRate)))
                .andExpect(status().isBadRequest())
                .andReturn();

        ValidationResponse expectedValidationResponse = new ValidationResponse(
                List.of(new Violation("rideId", "{ride.null}"),
                        new Violation("rating", "{rating.range}")));
        ValidationResponse actualResponse = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ValidationResponse.class);

        assertThat(actualResponse.violations()).containsExactlyInAnyOrderElementsOf(expectedValidationResponse.violations());
    }

    @Test
    void update_whenVerifyingRequestMatching_thenReturn200() throws Exception {
        RateCreateEditDto updateRate = new RateCreateEditDto(1L, "Good", 4, 1L, UserType.PASSENGER);

        mockMvc.perform(put(URL + "/{id}", DEFAULT_ID_PASSENGER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRate)))
                .andExpect(status().isOk());
    }

    @Test
    void update_whenValidInput_thenReturn200() throws Exception {
        RateCreateEditDto updateRate = new RateCreateEditDto(1L, "Good", 4, 1L, UserType.PASSENGER);

        mockMvc.perform(put(URL + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRate)))
                .andExpect(status().isOk());
    }

    @Test
    void updateDriverRate_whenValidInput_thenMapsToBusinessModel() throws Exception {
        RateCreateEditDto updateRate = new RateCreateEditDto(1L, "Good", 4, 1L, UserType.PASSENGER);

        mockMvc.perform(put(URL + "/{id}", DEFAULT_ID_DRIVER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRate)))
                .andExpect(status().isOk());

        ArgumentCaptor<RateCreateEditDto> rateCaptor = ArgumentCaptor.forClass(RateCreateEditDto.class);
        ArgumentCaptor<Long> idCaptor = ArgumentCaptor.forClass(Long.class);

        verify(driverRateService, times(1)).update(idCaptor.capture(), rateCaptor.capture());
        assertThat(idCaptor.getValue()).isEqualTo(DEFAULT_ID_DRIVER);
        assertThat(rateCaptor.getValue().rideId()).isEqualTo(1L);
        assertThat(rateCaptor.getValue().comment()).isEqualTo("Good");
        assertThat(rateCaptor.getValue().rating()).isEqualTo(4);
        assertThat(rateCaptor.getValue().userId()).isEqualTo(1L);
        assertThat(rateCaptor.getValue().userType()).isEqualTo(UserType.PASSENGER);
    }

    @Test
    void updateDriverRate_whenValidInput_thenReturn200AndCarReadDto() throws Exception {
        RateCreateEditDto updateRate = new RateCreateEditDto(1L, "Good", 4, 1L, UserType.PASSENGER);
        RateReadDto readRate = new RateReadDto(DEFAULT_ID_DRIVER, 1L, "Good", 4, 1L, UserType.PASSENGER);

        when(driverRateService.update(DEFAULT_ID_DRIVER, updateRate)).thenReturn(readRate);

        MvcResult mvcResult = mockMvc.perform(put(URL + "/{id}", DEFAULT_ID_DRIVER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRate)))
                .andExpect(status().isOk())
                .andReturn();

        String actualResponseBody = mvcResult.getResponse().getContentAsString();
        assertThat(actualResponseBody).isEqualToIgnoringWhitespace(
                objectMapper.writeValueAsString(readRate));
    }

    @Test
    void updatePassengerRate_whenValidInput_thenMapsToBusinessModel() throws Exception {
        RateCreateEditDto updateRate = new RateCreateEditDto(1L, "Good", 4, 1L, UserType.DRIVER);

        mockMvc.perform(put(URL + "/{id}", DEFAULT_ID_PASSENGER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRate)))
                .andExpect(status().isOk());

        ArgumentCaptor<RateCreateEditDto> rateCaptor = ArgumentCaptor.forClass(RateCreateEditDto.class);
        ArgumentCaptor<Long> idCaptor = ArgumentCaptor.forClass(Long.class);

        verify(passengerRateService, times(1)).update(idCaptor.capture(), rateCaptor.capture());
        assertThat(idCaptor.getValue()).isEqualTo(DEFAULT_ID_PASSENGER);
        assertThat(rateCaptor.getValue().rideId()).isEqualTo(1L);
        assertThat(rateCaptor.getValue().comment()).isEqualTo("Good");
        assertThat(rateCaptor.getValue().rating()).isEqualTo(4);
        assertThat(rateCaptor.getValue().userId()).isEqualTo(1L);
        assertThat(rateCaptor.getValue().userType()).isEqualTo(UserType.DRIVER);
    }

    @Test
    void updatePassengerRate_whenValidInput_thenReturn200AndCarReadDto() throws Exception {
        RateCreateEditDto updateRate = new RateCreateEditDto(1L, "Good", 4, 1L, UserType.DRIVER);
        RateReadDto readRate = new RateReadDto(DEFAULT_ID_PASSENGER, 1L, "Good", 4, 1L, UserType.DRIVER);

        when(passengerRateService.update(DEFAULT_ID_PASSENGER, updateRate)).thenReturn(readRate);

        MvcResult mvcResult = mockMvc.perform(put(URL + "/{id}", DEFAULT_ID_PASSENGER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRate)))
                .andExpect(status().isOk())
                .andReturn();

        String actualResponseBody = mvcResult.getResponse().getContentAsString();
        assertThat(actualResponseBody).isEqualToIgnoringWhitespace(
                objectMapper.writeValueAsString(readRate));
    }

    @Test
    void update_whenInvalidInputNullAndMax_thenReturn400AndValidationResponse() throws Exception {
        RateCreateEditDto updateRate = new RateCreateEditDto(null, "Good", 6, 2L, UserType.PASSENGER);

        MvcResult mvcResult = mockMvc.perform(put(URL + "/{id}", DEFAULT_ID_PASSENGER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRate)))
                .andExpect(status().isBadRequest())
                .andReturn();

        ValidationResponse expectedValidationResponse = new ValidationResponse(
                List.of(new Violation("rideId", "{ride.null}"),
                        new Violation("rating", "{rating.range}")));
        ValidationResponse actualResponse = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ValidationResponse.class);

        assertThat(actualResponse.violations()).containsExactlyInAnyOrderElementsOf(expectedValidationResponse.violations());
    }

    @Test
    void update_whenInvalidInputNullAndMin_thenReturn400AndValidationResponse() throws Exception {
        RateCreateEditDto updateRate = new RateCreateEditDto(null, "Good", 0, 2L, UserType.PASSENGER);

        MvcResult mvcResult = mockMvc.perform(put(URL + "/{id}", DEFAULT_ID_PASSENGER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRate)))
                .andExpect(status().isBadRequest())
                .andReturn();

        ValidationResponse expectedValidationResponse = new ValidationResponse(
                List.of(new Violation("rideId", "{ride.null}"),
                        new Violation("rating", "{rating.range}")));
        ValidationResponse actualResponse = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ValidationResponse.class);

        assertThat(actualResponse.violations()).containsExactlyInAnyOrderElementsOf(expectedValidationResponse.violations());
    }
}
