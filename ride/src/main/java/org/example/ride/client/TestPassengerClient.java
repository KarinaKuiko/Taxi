package org.example.ride.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Profile;

@Profile("test")
@FeignClient(name = "test-passenger", url = "http://localhost:8082")
public interface TestPassengerClient extends CommonPassengerClient {
}
