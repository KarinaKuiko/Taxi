package org.example.ride.unit.service;

import org.example.ride.client.PassengerClient;
import org.example.ride.dto.read.PassengerReadDto;
import org.example.ride.service.PassengerClientService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.example.ride.util.DataUtil.DEFAULT_ID;
import static org.example.ride.util.DataUtil.getPassengerReadDtoBuilder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PassengerClientServiceTest {

    @InjectMocks
    private PassengerClientService passengerClientService;

    @Mock
    private PassengerClient passengerClient;

    @Test
    void checkExistingPassenger_thenReturnPassengerReadDto() {
        PassengerReadDto readPassenger = getPassengerReadDtoBuilder().build();

        when(passengerClient.findById(DEFAULT_ID)).thenReturn(readPassenger);

        assertThat(passengerClientService.getPassenger(DEFAULT_ID)).isNotNull();
        verify(passengerClient).findById(DEFAULT_ID);
    }
}
