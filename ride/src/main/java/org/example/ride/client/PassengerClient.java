package org.example.ride.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Profile;

@FeignClient(name = "passenger")
@Profile("!test")
public interface PassengerClient extends CommonPassengerClient {
}
