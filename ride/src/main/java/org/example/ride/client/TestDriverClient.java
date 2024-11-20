package org.example.ride.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Profile;

@Profile("test")
@FeignClient(name = "test-driver", url = "http://localhost:8081")
public interface TestDriverClient extends CommonDriverClient{
}
