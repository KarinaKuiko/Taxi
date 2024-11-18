package org.example.ride.unit.service;

import org.example.ride.client.DriverClient;
import org.example.ride.dto.read.DriverReadDto;
import org.example.ride.service.DriverClientService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.example.ride.util.DataUtil.DEFAULT_ID;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.example.ride.util.DataUtil.getDriverReadDtoBuilder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DriverClientServiceTest {

    @InjectMocks
    private DriverClientService driverClientService;

    @Mock
    private DriverClient driverClient;

    @Test
    void getDriver_thenReturnDriverReadDto() {
        DriverReadDto readDriver = getDriverReadDtoBuilder().build();

        when(driverClient.findById(DEFAULT_ID)).thenReturn(readDriver);

        assertThat(driverClientService.getDriver(DEFAULT_ID)).isNotNull();
        verify(driverClient).findById(DEFAULT_ID);
    }
}
