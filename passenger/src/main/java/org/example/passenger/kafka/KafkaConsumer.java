package org.example.passenger.kafka;

import lombok.RequiredArgsConstructor;
import org.example.passenger.constants.AppConstants;
import org.example.passenger.dto.read.RideReadDto;
import org.example.passenger.service.PassengerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaConsumer {
    private final PassengerService passengerService;

    private final static Logger logger = LoggerFactory.getLogger(KafkaConsumer.class);

    @KafkaListener(topics = AppConstants.PASSENGER_NOTIFICATION_TOPIC)
    public void consumeNotification(RideReadDto rideReadDto) {
        passengerService.notifyPassenger(rideReadDto);
    }


}
