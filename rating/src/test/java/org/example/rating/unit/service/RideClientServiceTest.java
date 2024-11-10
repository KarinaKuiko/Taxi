package org.example.rating.unit.service;

import org.example.rating.client.RideClient;
import org.example.rating.dto.read.RideReadDto;
import org.example.rating.service.RideClientService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.example.rating.util.DataUtil.DEFAULT_ID;
import static org.example.rating.util.DataUtil.getRideReadDto;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RideClientServiceTest {

    @InjectMocks
    private RideClientService rideClientService;

    @Mock
    private RideClient rideClient;

    @Test
    void getRide_thenReturnRideReadDto() {
        RideReadDto readRide = getRideReadDto();
        when(rideClient.findById(DEFAULT_ID)).thenReturn(readRide);

        assertThat(rideClientService.getRide(DEFAULT_ID)).isNotNull();
        verify(rideClient).findById(DEFAULT_ID);
    }
}
