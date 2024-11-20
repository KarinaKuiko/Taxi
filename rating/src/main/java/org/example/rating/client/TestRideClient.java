package org.example.rating.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Profile;

@Profile("test")
@FeignClient(name = "${client.name}", url = "${client.url}")
public interface TestRideClient extends CommonFeignClient {
}
