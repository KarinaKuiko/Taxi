package org.example.rating.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Profile;

@Profile("test")
@FeignClient(name = "test-ride", url = "http://localhost:8083")
public interface TestRideClient extends CommonFeignClient {
}
