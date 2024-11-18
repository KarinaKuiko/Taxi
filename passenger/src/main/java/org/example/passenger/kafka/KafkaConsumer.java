package org.example.passenger.kafka;

import lombok.RequiredArgsConstructor;
import org.example.passenger.constants.KafkaConstants;
import org.example.passenger.dto.read.RideReadDto;
import org.example.passenger.dto.read.UserRateDto;
import org.example.passenger.service.PassengerService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaConsumer {
    private final PassengerService passengerService;

    @KafkaListener(topics = KafkaConstants.PASSENGER_NOTIFICATION_TOPIC)
    public void consumeNotification(RideReadDto rideReadDto) {
        passengerService.notifyPassenger(rideReadDto);
    }

    @KafkaListener(topics = KafkaConstants.PASSENGER_RATING_NOTIFICATION_TOPIC)
    public void consumeRatingNotification(UserRateDto userRateDto) {
        passengerService.updateRating(userRateDto);
    }
}
