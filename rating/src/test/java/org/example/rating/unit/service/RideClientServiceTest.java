package org.example.rating.unit.service;

import org.example.rating.client.RideClient;
import org.example.rating.dto.read.RideReadDto;
import org.example.rating.service.RideClientService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RideClientServiceTest {

    private static final Long DEFAULT_ID = 1L;

    @InjectMocks
    private RideClientService rideClientService;

    @Mock
    private RideClient rideClient;

    @Test
    void checkExistingRide_thenReturnRideReadDto() {
        RideReadDto readRide = new RideReadDto(DEFAULT_ID, DEFAULT_ID, DEFAULT_ID, "from", "to", "ACCEPTED", "WAITING", new BigDecimal("123.45"));
        when(rideClient.findById(DEFAULT_ID)).thenReturn(readRide);

        assertThat(rideClientService.checkExistingRide(DEFAULT_ID)).isNotNull();
        verify(rideClient).findById(DEFAULT_ID);

    }
}
