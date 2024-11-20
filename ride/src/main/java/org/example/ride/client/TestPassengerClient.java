package org.example.ride.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Profile;

@Profile("test")
@FeignClient(name = "${client.passenger.name}", url = "${client.passenger.url}")
public interface TestPassengerClient extends CommonPassengerClient {
}
