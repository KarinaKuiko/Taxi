package org.example.rating.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.rating.constants.KafkaConstants;
import org.example.rating.dto.read.UserRateDto;
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

    public void notifyPassenger(UserRateDto userRateDto) {
        sendMessage(KafkaConstants.PASSENGER_RATING_NOTIFICATION_TOPIC, generateTransactionalKey(), userRateDto);
    }

    public void notifyDriver(UserRateDto userRateDto) {
        sendMessage(KafkaConstants.DRIVER_RATING_NOTIFICATION_TOPIC, generateTransactionalKey(), userRateDto);
    }

    private void sendMessage(String topic, String key, Object message) {
        CompletableFuture<SendResult<String, Object>> future =
                kafkaTemplate.send(topic, key, message);
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Sent message successfully {} to {} with offset {}",
                        message.toString(),
                        topic,
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
