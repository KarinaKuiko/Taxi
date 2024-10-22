package org.example.ride.kafka;

import lombok.RequiredArgsConstructor;
import org.example.ride.constants.AppConstants;
import org.example.ride.dto.read.RideReadDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class KafkaProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final static Logger logger = LoggerFactory.getLogger(KafkaProducer.class);

    public void notifyPassenger(RideReadDto rideReadDto) {
        sendMessage(AppConstants.PASSENGER_NOTIFICATION_TOPIC, generateTransactionalKey(), rideReadDto);
    }

    private void sendMessage(String topic, String key, Object message) {
        CompletableFuture<SendResult<String, Object>> future =
                kafkaTemplate.send(topic, key, message);
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                logger.info("Sent message successfully {} with offset {}",
                        message.toString(),
                        result.getRecordMetadata().offset());
            } else {
                logger.error(ex.getMessage());
            }
        });
    }

    private String generateTransactionalKey() {
        return UUID.randomUUID().toString();
    }
}
