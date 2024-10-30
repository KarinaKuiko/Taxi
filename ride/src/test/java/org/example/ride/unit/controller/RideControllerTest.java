package org.example.ride.unit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.ride.controller.RideController;
import org.example.ride.dto.create.DriverRideStatusDto;
import org.example.ride.dto.create.PassengerRideStatusDto;
import org.example.ride.dto.create.RideCreateEditDto;
import org.example.ride.dto.read.RideReadDto;
import org.example.ride.dto.read.ValidationResponse;
import org.example.ride.entity.enumeration.DriverRideStatus;
import org.example.ride.entity.enumeration.PassengerRideStatus;
import org.example.ride.exception.violation.Violation;
import org.example.ride.service.RideService;
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

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = RideController.class)
public class RideControllerTest {
    private static final String URL = "/api/v1/rides";
    private static final Long DEFAULT_ID = 1L;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RideService rideService;

    private List<RideReadDto> rideList;
    private Page<RideReadDto> ridePage;
    private RideReadDto defaultRide;

    @BeforeEach
    void init() {
        defaultRide = new RideReadDto(DEFAULT_ID, 1L, 1L, "From", "To", DriverRideStatus.ACCEPTED, PassengerRideStatus.WAITING, new BigDecimal(123.43));
        RideReadDto car2 = new RideReadDto(2L, 2L, 2L, "From", "To", DriverRideStatus.ON_WAY_FOR_PASSENGER, PassengerRideStatus.WAITING, new BigDecimal(789.55));
        rideList = List.of(defaultRide, car2);
        ridePage = new PageImpl<>(rideList, PageRequest.of(0, 10), 2);
    }

    @Test
    void findAll_whenVerifyingRequestMatchingWithoutParams_thenReturn200() throws Exception {
        when(rideService.findRides(null, null,0, 10)).thenReturn(ridePage);

        mockMvc.perform(get(URL))
                .andExpect(status().isOk());
    }

    @Test
    void findAll_whenCorrectParams_thenReturn200() throws Exception {
        when(rideService.findRides(null, null,0, 10)).thenReturn(ridePage);

        mockMvc.perform(get(URL)
                        .param("page", "0")
                        .param("limit", "10"))
                .andExpect(status().isOk());
    }

    @Test
    void findAll_whenCorrectParamsWithDriverId_thenReturn200() throws Exception {
        when(rideService.findRides(1L, null,0, 10)).thenReturn(ridePage);

        mockMvc.perform(get(URL)
                        .param("driverId", "1")
                        .param("page", "0")
                        .param("limit", "10"))
                .andExpect(status().isOk());
    }

    @Test
    void findAll_whenCorrectParamsWithPassengerId_thenReturn200() throws Exception {
        when(rideService.findRides(null, 1L,0, 10)).thenReturn(ridePage);

        mockMvc.perform(get(URL)
                        .param("passengerId", "1")
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
        when(rideService.findById(DEFAULT_ID)).thenReturn(rideList.get(0));

        MvcResult mvcResult = mockMvc.perform(get(URL + "/{id}", DEFAULT_ID))
                .andExpect(status().isOk())
                .andReturn();

        String actual = mvcResult.getResponse().getContentAsString();

        assertThat(actual).isEqualToIgnoringWhitespace(
                objectMapper.writeValueAsString(defaultRide));
    }

    @Test
    void findById_whenVerifyingRequestMatching_thenReturn200() throws Exception {
        when(rideService.findById(DEFAULT_ID)).thenReturn(rideList.get(0));

        MvcResult mvcResult = mockMvc.perform(get(URL + "/1"))
                .andExpect(status().isOk())
                .andReturn();

        String actual = mvcResult.getResponse().getContentAsString();

        assertThat(actual).isEqualToIgnoringWhitespace(
                objectMapper.writeValueAsString(defaultRide));
    }

    @Test
    void create_whenVerifyingRequestMatching_thenReturn200() throws Exception {
        RideCreateEditDto createRide = new RideCreateEditDto(1L, 1L, "Test", "Test");

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRide)))
                .andExpect(status().isCreated())
                .andReturn();
    }

    @Test
    void create_whenValidInput_thenMapsToBusinessModel() throws Exception {
        RideCreateEditDto createRide = new RideCreateEditDto(1L, 1L, "Test", "Test");

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRide)))
                .andExpect(status().isCreated())
                .andReturn();

        ArgumentCaptor<RideCreateEditDto> rideCaptor = ArgumentCaptor.forClass(RideCreateEditDto.class);

        verify(rideService, times(1)).create(rideCaptor.capture());
        assertThat(rideCaptor.getValue().driverId()).isEqualTo(1L);
        assertThat(rideCaptor.getValue().passengerId()).isEqualTo(1L);
        assertThat(rideCaptor.getValue().addressFrom()).isEqualTo("Test");
        assertThat(rideCaptor.getValue().addressTo()).isEqualTo("Test");
    }

    @Test
    void create_whenValidInput_thenReturn201AndCarReadDto() throws Exception {
        RideCreateEditDto createRide = new RideCreateEditDto(1L, 1L, "Test", "Test");
        RideReadDto readRide = new RideReadDto(3L, 1L, 1L, "Test", "Test", DriverRideStatus.CREATED, PassengerRideStatus.WAITING, new BigDecimal(555.10));

        when(rideService.create(createRide)).thenReturn(readRide);

        MvcResult mvcResult = mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRide)))
                .andExpect(status().isCreated())
                .andReturn();

        String actualResponseBody = mvcResult.getResponse().getContentAsString();
        assertThat(actualResponseBody).isEqualToIgnoringWhitespace(
                objectMapper.writeValueAsString(readRide));
    }

    @Test
    void create_whenInvalidInputMinAndNull_thenReturn400AndValidationResponse() throws Exception {
        RideCreateEditDto createRide = new RideCreateEditDto(0L, 0L, null, null);

        MvcResult mvcResult = mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRide)))
                .andExpect(status().isBadRequest())
                .andReturn();

        ValidationResponse expectedValidationResponse = new ValidationResponse(
                List.of(new Violation("driverId", "{id.min}"),
                        new Violation("passengerId", "{id.min}"),
                        new Violation("addressFrom", "{address.from.blank}"),
                        new Violation("addressTo", "{address.from.blank}")));
        ValidationResponse actualResponse = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ValidationResponse.class);

        assertThat(actualResponse.violations()).containsExactlyInAnyOrderElementsOf(expectedValidationResponse.violations());
    }

    @Test
    void create_whenInvalidInputNull_thenReturn400AndValidationResponse() throws Exception {
        RideCreateEditDto createRide = new RideCreateEditDto(null, null, null, null);

        MvcResult mvcResult = mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRide)))
                .andExpect(status().isBadRequest())
                .andReturn();

        ValidationResponse expectedValidationResponse = new ValidationResponse(
                List.of(new Violation("passengerId", "{passenger.null}"),
                        new Violation("addressFrom", "{address.from.blank}"),
                        new Violation("addressTo", "{address.from.blank}")));
        ValidationResponse actualResponse = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ValidationResponse.class);

        assertThat(actualResponse.violations()).containsExactlyInAnyOrderElementsOf(expectedValidationResponse.violations());
    }

    @Test
    void update_whenVerifyingRequestMatching_thenReturn200() throws Exception {
        RideCreateEditDto updateRide = new RideCreateEditDto(1L, 1L, "Test", "Test");

        mockMvc.perform(put(URL + "/{id}", DEFAULT_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRide)))
                .andExpect(status().isOk());
    }

    @Test
    void update_whenValidInput_thenReturn200() throws Exception {
        RideCreateEditDto updateRide = new RideCreateEditDto(1L, 1L, "Test", "Test");

        mockMvc.perform(put(URL + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRide)))
                .andExpect(status().isOk());
    }

    @Test
    void update_whenValidInput_thenMapsToBusinessModel() throws Exception {
        RideCreateEditDto updateRide = new RideCreateEditDto(1L, 1L, "Test", "Test");

        mockMvc.perform(put(URL + "/{id}", DEFAULT_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRide)))
                .andExpect(status().isOk());

        ArgumentCaptor<RideCreateEditDto> rideCaptor = ArgumentCaptor.forClass(RideCreateEditDto.class);
        ArgumentCaptor<Long> idCaptor = ArgumentCaptor.forClass(Long.class);

        verify(rideService, times(1)).update(idCaptor.capture(), rideCaptor.capture());
        assertThat(idCaptor.getValue()).isEqualTo(DEFAULT_ID);
        assertThat(rideCaptor.getValue().driverId()).isEqualTo(1L);
        assertThat(rideCaptor.getValue().passengerId()).isEqualTo(1L);
        assertThat(rideCaptor.getValue().addressFrom()).isEqualTo("Test");
        assertThat(rideCaptor.getValue().addressTo()).isEqualTo("Test");
    }

    @Test
    void update_whenValidInput_thenReturn200AndCarReadDto() throws Exception {
        RideCreateEditDto updateRide = new RideCreateEditDto(1L, 1L, "Test", "Test");
        RideReadDto readRide = new RideReadDto(DEFAULT_ID, 1L, 1L, "Test", "Test", DriverRideStatus.ACCEPTED, PassengerRideStatus.WAITING, new BigDecimal(123.43));

        when(rideService.update(DEFAULT_ID, updateRide)).thenReturn(readRide);

        MvcResult mvcResult = mockMvc.perform(put(URL + "/{id}", DEFAULT_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRide)))
                .andExpect(status().isOk())
                .andReturn();

        String actualResponseBody = mvcResult.getResponse().getContentAsString();
        assertThat(actualResponseBody).isEqualToIgnoringWhitespace(
                objectMapper.writeValueAsString(readRide));
    }

    @Test
    void update_whenInvalidInputMinAndNull_thenReturn400AndValidationResponse() throws Exception {
        RideCreateEditDto updateRide = new RideCreateEditDto(0L, 0L, null, null);

        MvcResult mvcResult = mockMvc.perform(put(URL + "/{id}", DEFAULT_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRide)))
                .andExpect(status().isBadRequest())
                .andReturn();

        ValidationResponse expectedValidationResponse = new ValidationResponse(
                List.of(new Violation("driverId", "{id.min}"),
                        new Violation("passengerId", "{id.min}"),
                        new Violation("addressFrom", "{address.from.blank}"),
                        new Violation("addressTo", "{address.from.blank}")));
        ValidationResponse actualResponse = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ValidationResponse.class);

        assertThat(actualResponse.violations()).containsExactlyInAnyOrderElementsOf(expectedValidationResponse.violations());
    }

    @Test
    void update_whenInvalidInputNull_thenReturn400AndValidationResponse() throws Exception {
        RideCreateEditDto updateRide = new RideCreateEditDto(null, null, null, null);

        MvcResult mvcResult = mockMvc.perform(put(URL + "/{id}", DEFAULT_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRide)))
                .andExpect(status().isBadRequest())
                .andReturn();

        ValidationResponse expectedValidationResponse = new ValidationResponse(
                List.of(new Violation("passengerId", "{passenger.null}"),
                        new Violation("addressFrom", "{address.from.blank}"),
                        new Violation("addressTo", "{address.from.blank}")));
        ValidationResponse actualResponse = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ValidationResponse.class);

        assertThat(actualResponse.violations()).containsExactlyInAnyOrderElementsOf(expectedValidationResponse.violations());
    }

    @Test
    void updateDriverStatus_whenVerifyingRequestMatching_thenReturn200() throws Exception {
        DriverRideStatusDto driverRideStatusDto = new DriverRideStatusDto(DriverRideStatus.ON_WAY_FOR_PASSENGER);

        mockMvc.perform(put(URL + "/{id}/driverStatus", DEFAULT_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(driverRideStatusDto)))
                .andExpect(status().isOk());
    }

    @Test
    void updateDriverStatus_whenValidInput_thenReturn200() throws Exception {
        DriverRideStatusDto driverRideStatusDto = new DriverRideStatusDto(DriverRideStatus.ON_WAY_FOR_PASSENGER);

        mockMvc.perform(put(URL + "/1/driverStatus")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(driverRideStatusDto)))
                .andExpect(status().isOk());
    }

    @Test
    void updateDriverStatus_whenValidInput_thenMapsToBusinessModel() throws Exception {
        DriverRideStatusDto driverRideStatusDto = new DriverRideStatusDto(DriverRideStatus.ON_WAY_FOR_PASSENGER);

        mockMvc.perform(put(URL + "/{id}/driverStatus", DEFAULT_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(driverRideStatusDto)))
                .andExpect(status().isOk());

        ArgumentCaptor<DriverRideStatusDto> driverStatusCaptor = ArgumentCaptor.forClass(DriverRideStatusDto.class);
        ArgumentCaptor<Long> idCaptor = ArgumentCaptor.forClass(Long.class);

        verify(rideService, times(1)).updateDriverStatus(idCaptor.capture(), driverStatusCaptor.capture());
        assertThat(idCaptor.getValue()).isEqualTo(DEFAULT_ID);
        assertThat(driverStatusCaptor.getValue().rideStatus()).isEqualTo(DriverRideStatus.ON_WAY_FOR_PASSENGER);
    }

    @Test
    void updateDriverStatus_whenValidInput_thenReturn200AndCarReadDto() throws Exception {
        DriverRideStatusDto driverRideStatusDto = new DriverRideStatusDto(DriverRideStatus.ON_WAY_FOR_PASSENGER);
        RideReadDto readRide = new RideReadDto(DEFAULT_ID, 1L, 1L, "From", "To", DriverRideStatus.ON_WAY_FOR_PASSENGER, PassengerRideStatus.WAITING, new BigDecimal(123.43));

        when(rideService.updateDriverStatus(DEFAULT_ID, driverRideStatusDto)).thenReturn(readRide);

        MvcResult mvcResult = mockMvc.perform(put(URL + "/{id}/driverStatus", DEFAULT_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(driverRideStatusDto)))
                .andExpect(status().isOk())
                .andReturn();

        String actualResponseBody = mvcResult.getResponse().getContentAsString();
        assertThat(actualResponseBody).isEqualToIgnoringWhitespace(
                objectMapper.writeValueAsString(readRide));
    }

    @Test
    void updatePassengerStatus_whenVerifyingRequestMatching_thenReturn200() throws Exception {
        PassengerRideStatusDto passengerRideStatusDto = new PassengerRideStatusDto(PassengerRideStatus.GETTING_OUT);

        mockMvc.perform(put(URL + "/{id}/passengerStatus", DEFAULT_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passengerRideStatusDto)))
                .andExpect(status().isOk());
    }

    @Test
    void updatePassengerStatus_whenValidInput_thenReturn200() throws Exception {
        PassengerRideStatusDto passengerRideStatusDto = new PassengerRideStatusDto(PassengerRideStatus.GETTING_OUT);

        mockMvc.perform(put(URL + "/1/passengerStatus")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passengerRideStatusDto)))
                .andExpect(status().isOk());
    }

    @Test
    void updatePassengerStatus_whenValidInput_thenMapsToBusinessModel() throws Exception {
        PassengerRideStatusDto passengerRideStatusDto = new PassengerRideStatusDto(PassengerRideStatus.GETTING_OUT);

        mockMvc.perform(put(URL + "/{id}/passengerStatus", DEFAULT_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passengerRideStatusDto)))
                .andExpect(status().isOk());

        ArgumentCaptor<PassengerRideStatusDto> passengerStatusCaptor = ArgumentCaptor.forClass(PassengerRideStatusDto.class);
        ArgumentCaptor<Long> idCaptor = ArgumentCaptor.forClass(Long.class);

        verify(rideService, times(1)).updatePassengerStatus(idCaptor.capture(), passengerStatusCaptor.capture());
        assertThat(idCaptor.getValue()).isEqualTo(DEFAULT_ID);
        assertThat(passengerStatusCaptor.getValue().rideStatus()).isEqualTo(PassengerRideStatus.GETTING_OUT);
    }

    @Test
    void updatePassengerStatus_whenValidInput_thenReturn200AndCarReadDto() throws Exception {
        PassengerRideStatusDto passengerRideStatusDto = new PassengerRideStatusDto(PassengerRideStatus.GETTING_OUT);
        RideReadDto readRide = new RideReadDto(DEFAULT_ID, 1L, 1L, "From", "To", DriverRideStatus.ACCEPTED, PassengerRideStatus.GETTING_OUT, new BigDecimal(123.43));

        when(rideService.updatePassengerStatus(DEFAULT_ID, passengerRideStatusDto)).thenReturn(readRide);

        MvcResult mvcResult = mockMvc.perform(put(URL + "/{id}/passengerStatus", DEFAULT_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passengerRideStatusDto)))
                .andExpect(status().isOk())
                .andReturn();

        String actualResponseBody = mvcResult.getResponse().getContentAsString();
        assertThat(actualResponseBody).isEqualToIgnoringWhitespace(
                objectMapper.writeValueAsString(readRide));
    }
}
