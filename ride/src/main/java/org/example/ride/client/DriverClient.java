package org.example.ride.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Profile;

@FeignClient(name = "driver")
@Profile("!test")
public interface DriverClient extends CommonDriverClient {
}
