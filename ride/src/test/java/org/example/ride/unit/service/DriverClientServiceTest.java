package org.example.ride.unit.service;

import org.example.ride.client.DriverClient;
import org.example.ride.dto.read.DriverReadDto;
import org.example.ride.service.DriverClientService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DriverClientServiceTest {

    private static final Long DEFAULT_ID = 1L;

    @InjectMocks
    private DriverClientService driverClientService;

    @Mock
    private DriverClient driverClient;

    @Test
    void checkExistingDriver_thenReturnDriverReadDto() {
        DriverReadDto readDriver = new DriverReadDto(DEFAULT_ID, "name", "name@gmail.com", "+375441234567", "MALE", DEFAULT_ID, 5.0);

        when(driverClient.findById(DEFAULT_ID)).thenReturn(readDriver);

        assertThat(driverClientService.checkExistingDriver(DEFAULT_ID)).isNotNull();
        verify(driverClient).findById(DEFAULT_ID);
    }
}
