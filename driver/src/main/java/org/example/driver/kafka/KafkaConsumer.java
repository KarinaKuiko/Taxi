package org.example.driver.kafka;

import lombok.RequiredArgsConstructor;
import org.example.driver.constants.KafkaConstants;
import org.example.driver.dto.read.RideReadDto;
import org.example.driver.service.DriverService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaConsumer {
    private final DriverService driverService;

    @KafkaListener(topics = KafkaConstants.DRIVER_NOTIFICATION_TOPIC)
    public void consumeNotification(RideReadDto rideReadDto) {
        driverService.notifyDriver(rideReadDto);
    }
}
