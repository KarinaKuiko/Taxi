package org.example.ride.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ride.constants.KafkaConstants;
import org.example.ride.dto.read.RideReadDto;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void notifyPassenger(RideReadDto rideReadDto) {
        sendMessage(KafkaConstants.PASSENGER_NOTIFICATION_TOPIC, generateTransactionalKey(), rideReadDto);
    }

    private void sendMessage(String topic, String key, Object message) {
        CompletableFuture<SendResult<String, Object>> future =
                kafkaTemplate.send(topic, key, message);
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Sent message successfully {} with offset {}",
                        message.toString(),
                        result.getRecordMetadata().offset());
            } else {
                log.error(ex.getMessage());
            }
        });
    }

    private String generateTransactionalKey() {
        return UUID.randomUUID().toString();
    }
}
