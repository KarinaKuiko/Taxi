package org.example.ride.unit.controller;

import com.example.exceptionhandlerstarter.dto.ValidationResponse;
import com.example.exceptionhandlerstarter.dto.Violation;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.ride.config.MessageSourceConfig;
import org.example.ride.controller.RideController;
import org.example.ride.dto.create.DriverRideStatusDto;
import org.example.ride.dto.create.PassengerRideStatusDto;
import org.example.ride.dto.create.RideCreateEditDto;
import org.example.ride.dto.read.RideReadDto;
import org.example.ride.entity.enumeration.DriverRideStatus;
import org.example.ride.entity.enumeration.PassengerRideStatus;
import org.example.ride.service.RideService;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.example.ride.util.DataUtil.DEFAULT_ADDRESS_FROM;
import static org.example.ride.util.DataUtil.DEFAULT_ADDRESS_TO;
import static org.example.ride.util.DataUtil.DEFAULT_ID;
import static org.example.ride.util.DataUtil.DRIVER_STATUS;
import static org.example.ride.util.DataUtil.LIMIT;
import static org.example.ride.util.DataUtil.LIMIT_VALUE;
import static org.example.ride.util.DataUtil.PAGE;
import static org.example.ride.util.DataUtil.PAGE_VALUE;
import static org.example.ride.util.DataUtil.PASSENGER_STATUS;
import static org.example.ride.util.DataUtil.URL;
import static org.example.ride.util.DataUtil.URL_WITH_ID;
import static org.example.ride.util.DataUtil.getRideCreateEditDtoBuilder;
import static org.example.ride.util.DataUtil.getRideReadDtoBuilder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = RideController.class)
@Import(MessageSourceConfig.class)
@WithMockUser
class RideControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RideService rideService;

    @Nested
    @DisplayName("Find all tests")
    public class findAllTests {
        @Test
        void findAll_whenVerifyingRequestMatchingWithoutParams_thenReturn200() throws Exception {
            RideReadDto readRide = getRideReadDtoBuilder().build();
            Page<RideReadDto> ridePage = new PageImpl<>(List.of(readRide),
                    PageRequest.of(PAGE_VALUE, LIMIT_VALUE), 1);

            when(rideService.findRides(null, null, PAGE_VALUE, LIMIT_VALUE)).thenReturn(ridePage);

            mockMvc.perform(get(URL))
                    .andExpect(status().isOk());
        }

        @Test
        void findAll_whenCorrectParams_thenReturn200() throws Exception {
            RideReadDto readRide = getRideReadDtoBuilder().build();
            Page<RideReadDto> ridePage = new PageImpl<>(List.of(readRide),
                    PageRequest.of(PAGE_VALUE, LIMIT_VALUE), 1);

            when(rideService.findRides(null, null, PAGE_VALUE, LIMIT_VALUE)).thenReturn(ridePage);

            mockMvc.perform(get(URL)
                            .param(PAGE, PAGE_VALUE.toString())
                            .param(LIMIT, LIMIT_VALUE.toString()))
                    .andExpect(status().isOk());
        }

        @Test
        void findAll_whenCorrectParamsWithDriverId_thenReturn200() throws Exception {
            RideReadDto readRide = getRideReadDtoBuilder().build();
            Page<RideReadDto> ridePage = new PageImpl<>(List.of(readRide),
                    PageRequest.of(PAGE_VALUE, LIMIT_VALUE), 1);

            when(rideService.findRides(1L, null, PAGE_VALUE, LIMIT_VALUE)).thenReturn(ridePage);

            mockMvc.perform(get(URL)
                            .param("driverId", "1")
                            .param(PAGE, PAGE_VALUE.toString())
                            .param(LIMIT, LIMIT_VALUE.toString()))
                    .andExpect(status().isOk());
        }

        @Test
        void findAll_whenCorrectParamsWithPassengerId_thenReturn200() throws Exception {
            RideReadDto readRide = getRideReadDtoBuilder().build();
            Page<RideReadDto> ridePage = new PageImpl<>(List.of(readRide),
                    PageRequest.of(PAGE_VALUE, LIMIT_VALUE), 1);

            when(rideService.findRides(null, 1L, PAGE_VALUE, LIMIT_VALUE)).thenReturn(ridePage);

            mockMvc.perform(get(URL)
                            .param("passengerId", "1")
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
    public class FindByIdTests {
        @Test
        void findById_whenVerifyingRequestMatching_thenReturn200() throws Exception {
            RideReadDto readRide = getRideReadDtoBuilder().build();

            when(rideService.findById(DEFAULT_ID)).thenReturn(readRide);

            MvcResult mvcResult = mockMvc.perform(get(URL_WITH_ID, DEFAULT_ID))
                    .andExpect(status().isOk())
                    .andReturn();

            String actual = mvcResult.getResponse().getContentAsString();

            assertThat(actual).isEqualToIgnoringWhitespace(
                    objectMapper.writeValueAsString(readRide));
            verify(rideService, times(1)).findById(DEFAULT_ID);
        }
    }

    @Nested
    @DisplayName("Create tests")
    public class CreateTests {
        @Test
        void create_whenVerifyingRequestMatching_thenReturn200() throws Exception {
            RideCreateEditDto createRide = getRideCreateEditDtoBuilder().build();

            mockMvc.perform(post(URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createRide))
                            .with(csrf()))
                    .andExpect(status().isCreated())
                    .andReturn();

            verify(rideService, times(1)).create(createRide);
        }

        @Test
        void create_whenValidInput_thenMapsToBusinessModel() throws Exception {
            RideCreateEditDto createRide = getRideCreateEditDtoBuilder().build();

            mockMvc.perform(post(URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createRide))
                            .with(csrf()))
                    .andExpect(status().isCreated())
                    .andReturn();

            ArgumentCaptor<RideCreateEditDto> rideCaptor = ArgumentCaptor.forClass(RideCreateEditDto.class);

            verify(rideService, times(1)).create(rideCaptor.capture());
            assertThat(rideCaptor.getValue().driverId()).isEqualTo(DEFAULT_ID);
            assertThat(rideCaptor.getValue().passengerId()).isEqualTo(DEFAULT_ID);
            assertThat(rideCaptor.getValue().addressFrom()).isEqualTo(DEFAULT_ADDRESS_FROM);
            assertThat(rideCaptor.getValue().addressTo()).isEqualTo(DEFAULT_ADDRESS_TO);
        }

        @Test
        void create_whenValidInput_thenReturn201AndCarReadDto() throws Exception {
            RideReadDto readRide = getRideReadDtoBuilder().build();
            RideCreateEditDto createRide = getRideCreateEditDtoBuilder().build();

            when(rideService.create(createRide)).thenReturn(readRide);

            MvcResult mvcResult = mockMvc.perform(post(URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createRide))
                            .with(csrf()))
                    .andExpect(status().isCreated())
                    .andReturn();

            String actualResponseBody = mvcResult.getResponse().getContentAsString();
            assertThat(actualResponseBody).isEqualToIgnoringWhitespace(
                    objectMapper.writeValueAsString(readRide));
        }

        @Test
        void create_whenInvalidInputMinAndNull_thenReturn400AndValidationResponse() throws Exception {
            RideCreateEditDto createRide = getRideCreateEditDtoBuilder()
                            .driverId(0L)
                            .passengerId(0L)
                            .addressFrom(null)
                            .addressTo(null)
                            .build();

            MvcResult mvcResult = mockMvc.perform(post(URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createRide))
                            .with(csrf()))
                    .andExpect(status().isBadRequest())
                    .andReturn();

            ValidationResponse expectedValidationResponse = new ValidationResponse(
                    List.of(new Violation("driverId", "Id should be 1 and more"),
                            new Violation("passengerId", "Id should be 1 and more"),
                            new Violation("addressFrom", "Address from cannot be blank"),
                            new Violation("addressTo", "Address to cannot be blank")));
            ValidationResponse actualResponse = objectMapper.readValue(
                    mvcResult.getResponse().getContentAsString(), ValidationResponse.class);

            assertThat(actualResponse.violations()).containsExactlyInAnyOrderElementsOf(
                    expectedValidationResponse.violations());
        }

        @Test
        void create_whenInvalidInputNull_thenReturn400AndValidationResponse() throws Exception {
            RideCreateEditDto createRide = getRideCreateEditDtoBuilder()
                            .passengerId(null)
                            .build();

            MvcResult mvcResult = mockMvc.perform(post(URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createRide))
                            .with(csrf()))
                    .andExpect(status().isBadRequest())
                    .andReturn();

            ValidationResponse expectedValidationResponse = new ValidationResponse(
                    List.of(new Violation("passengerId", "Passenger cannot be null")));
            ValidationResponse actualResponse = objectMapper.readValue(
                    mvcResult.getResponse().getContentAsString(), ValidationResponse.class);

            assertThat(actualResponse.violations()).containsExactlyInAnyOrderElementsOf(
                    expectedValidationResponse.violations());
        }
    }

    @Nested
    @DisplayName("Update tests")
    public class UpdateTests {
        @Test
        void update_whenVerifyingRequestMatching_thenReturn200() throws Exception {
            RideCreateEditDto createRide = getRideCreateEditDtoBuilder().build();

            mockMvc.perform(put(URL_WITH_ID, DEFAULT_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createRide))
                            .with(csrf()))
                    .andExpect(status().isOk());

            verify(rideService, times(1)).update(DEFAULT_ID, createRide);
        }

        @Test
        void update_whenValidInput_thenMapsToBusinessModel() throws Exception {
            RideCreateEditDto createRide = getRideCreateEditDtoBuilder().build();

            mockMvc.perform(put(URL_WITH_ID, DEFAULT_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createRide))
                            .with(csrf()))
                    .andExpect(status().isOk());

            ArgumentCaptor<RideCreateEditDto> rideCaptor = ArgumentCaptor.forClass(RideCreateEditDto.class);
            ArgumentCaptor<Long> idCaptor = ArgumentCaptor.forClass(Long.class);

            verify(rideService, times(1)).update(idCaptor.capture(), rideCaptor.capture());
            assertThat(idCaptor.getValue()).isEqualTo(DEFAULT_ID);
            assertThat(rideCaptor.getValue().driverId()).isEqualTo(DEFAULT_ID);
            assertThat(rideCaptor.getValue().passengerId()).isEqualTo(DEFAULT_ID);
            assertThat(rideCaptor.getValue().addressFrom()).isEqualTo(DEFAULT_ADDRESS_FROM);
            assertThat(rideCaptor.getValue().addressTo()).isEqualTo(DEFAULT_ADDRESS_TO);
        }

        @Test
        void update_whenValidInput_thenReturn200AndCarReadDto() throws Exception {
            RideReadDto readRide = getRideReadDtoBuilder().build();
            RideCreateEditDto createRide = getRideCreateEditDtoBuilder().build();

            when(rideService.update(DEFAULT_ID, createRide)).thenReturn(readRide);

            MvcResult mvcResult = mockMvc.perform(put(URL_WITH_ID, DEFAULT_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createRide))
                            .with(csrf()))
                    .andExpect(status().isOk())
                    .andReturn();

            String actualResponseBody = mvcResult.getResponse().getContentAsString();
            assertThat(actualResponseBody).isEqualToIgnoringWhitespace(
                    objectMapper.writeValueAsString(readRide));
        }

        @Test
        void update_whenInvalidInputMinAndNull_thenReturn400AndValidationResponse() throws Exception {
            RideCreateEditDto createRide = getRideCreateEditDtoBuilder()
                            .driverId(0L)
                            .passengerId(0L)
                            .addressFrom(null)
                            .addressTo(null)
                            .build();

            MvcResult mvcResult = mockMvc.perform(put(URL_WITH_ID, DEFAULT_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createRide))
                            .with(csrf()))
                    .andExpect(status().isBadRequest())
                    .andReturn();

            ValidationResponse expectedValidationResponse = new ValidationResponse(
                    List.of(new Violation("driverId", "Id should be 1 and more"),
                            new Violation("passengerId", "Id should be 1 and more"),
                            new Violation("addressFrom", "Address from cannot be blank"),
                            new Violation("addressTo", "Address to cannot be blank")));
            ValidationResponse actualResponse = objectMapper.readValue(
                    mvcResult.getResponse().getContentAsString(), ValidationResponse.class);

            assertThat(actualResponse.violations()).containsExactlyInAnyOrderElementsOf(
                    expectedValidationResponse.violations());
        }

        @Test
        void update_whenInvalidInputNull_thenReturn400AndValidationResponse() throws Exception {
            RideCreateEditDto createRide = getRideCreateEditDtoBuilder()
                            .driverId(null)
                            .passengerId(null)
                            .build();

            MvcResult mvcResult = mockMvc.perform(put(URL_WITH_ID, DEFAULT_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createRide))
                            .with(csrf()))
                    .andExpect(status().isBadRequest())
                    .andReturn();

            ValidationResponse expectedValidationResponse = new ValidationResponse(
                    List.of(new Violation("passengerId", "Passenger cannot be null")));
            ValidationResponse actualResponse = objectMapper.readValue(
                    mvcResult.getResponse().getContentAsString(), ValidationResponse.class);

            assertThat(actualResponse.violations()).containsExactlyInAnyOrderElementsOf(
                    expectedValidationResponse.violations());
        }

        @Test
        void updateDriverStatus_whenVerifyingRequestMatching_thenReturn200() throws Exception {
            DriverRideStatusDto driverRideStatusDto = new DriverRideStatusDto(DriverRideStatus.ON_WAY_FOR_PASSENGER);

            mockMvc.perform(put(URL_WITH_ID + DRIVER_STATUS, DEFAULT_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(driverRideStatusDto))
                            .with(csrf()))
                    .andExpect(status().isOk());

            verify(rideService, times(1)).updateDriverStatus(DEFAULT_ID, driverRideStatusDto);
        }

        @Test
        void updateDriverStatus_whenValidInput_thenMapsToBusinessModel() throws Exception {
            DriverRideStatusDto driverRideStatusDto = new DriverRideStatusDto(DriverRideStatus.ON_WAY_FOR_PASSENGER);

            mockMvc.perform(put(URL_WITH_ID + DRIVER_STATUS, DEFAULT_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(driverRideStatusDto))
                            .with(csrf()))
                    .andExpect(status().isOk());

            ArgumentCaptor<DriverRideStatusDto> driverStatusCaptor = ArgumentCaptor.forClass(DriverRideStatusDto.class);
            ArgumentCaptor<Long> idCaptor = ArgumentCaptor.forClass(Long.class);

            verify(rideService, times(1)).updateDriverStatus(
                    idCaptor.capture(), driverStatusCaptor.capture());
            assertThat(idCaptor.getValue()).isEqualTo(DEFAULT_ID);
            assertThat(driverStatusCaptor.getValue().rideStatus())
                    .isEqualTo(DriverRideStatus.ON_WAY_FOR_PASSENGER);
        }

        @Test
        void updateDriverStatus_whenValidInput_thenReturn200AndCarReadDto() throws Exception {
            DriverRideStatusDto driverRideStatusDto = new DriverRideStatusDto(DriverRideStatus.ON_WAY_FOR_PASSENGER);
            RideReadDto readRide = getRideReadDtoBuilder().build();

            when(rideService.updateDriverStatus(DEFAULT_ID, driverRideStatusDto)).thenReturn(readRide);

            MvcResult mvcResult = mockMvc.perform(put(URL_WITH_ID + DRIVER_STATUS, DEFAULT_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(driverRideStatusDto))
                            .with(csrf()))
                    .andExpect(status().isOk())
                    .andReturn();

            String actualResponseBody = mvcResult.getResponse().getContentAsString();
            assertThat(actualResponseBody).isEqualToIgnoringWhitespace(
                    objectMapper.writeValueAsString(readRide));
        }

        @Test
        void updatePassengerStatus_whenVerifyingRequestMatching_thenReturn200() throws Exception {
            PassengerRideStatusDto passengerRideStatusDto = new PassengerRideStatusDto(PassengerRideStatus.GETTING_OUT);

            mockMvc.perform(put(URL_WITH_ID + PASSENGER_STATUS, DEFAULT_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(passengerRideStatusDto))
                            .with(csrf()))
                    .andExpect(status().isOk());

            verify(rideService, times(1)).updatePassengerStatus(
                    DEFAULT_ID, passengerRideStatusDto);
        }

        @Test
        void updatePassengerStatus_whenValidInput_thenMapsToBusinessModel() throws Exception {
            PassengerRideStatusDto passengerRideStatusDto = new PassengerRideStatusDto(PassengerRideStatus.GETTING_OUT);

            mockMvc.perform(put(URL_WITH_ID + PASSENGER_STATUS, DEFAULT_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(passengerRideStatusDto))
                            .with(csrf()))
                    .andExpect(status().isOk());

            ArgumentCaptor<PassengerRideStatusDto> passengerStatusCaptor =
                    ArgumentCaptor.forClass(PassengerRideStatusDto.class);
            ArgumentCaptor<Long> idCaptor = ArgumentCaptor.forClass(Long.class);

            verify(rideService, times(1))
                    .updatePassengerStatus(idCaptor.capture(), passengerStatusCaptor.capture());
            assertThat(idCaptor.getValue()).isEqualTo(DEFAULT_ID);
            assertThat(passengerStatusCaptor.getValue().rideStatus())
                    .isEqualTo(PassengerRideStatus.GETTING_OUT);
        }

        @Test
        void updatePassengerStatus_whenValidInput_thenReturn200AndCarReadDto() throws Exception {
            PassengerRideStatusDto passengerRideStatusDto = new PassengerRideStatusDto(PassengerRideStatus.GETTING_OUT);
            RideReadDto readRide = getRideReadDtoBuilder().build();

            when(rideService.updatePassengerStatus(DEFAULT_ID, passengerRideStatusDto)).thenReturn(readRide);

            MvcResult mvcResult = mockMvc.perform(put(URL_WITH_ID + PASSENGER_STATUS, DEFAULT_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(passengerRideStatusDto))
                            .with(csrf()))
                    .andExpect(status().isOk())
                    .andReturn();

            String actualResponseBody = mvcResult.getResponse().getContentAsString();
            assertThat(actualResponseBody).isEqualToIgnoringWhitespace(
                    objectMapper.writeValueAsString(readRide));
        }
    }
}
