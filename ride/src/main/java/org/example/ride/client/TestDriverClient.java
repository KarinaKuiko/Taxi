package org.example.ride.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Profile;

@Profile("test")
@FeignClient(name = "${client.driver.name}", url = "${client.driver.url}")
public interface TestDriverClient extends CommonDriverClient{
}
