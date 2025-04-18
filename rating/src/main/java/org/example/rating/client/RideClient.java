package org.example.rating.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Profile;

@FeignClient(name = "ride")
@Profile("!test")
public interface RideClient extends CommonFeignClient {
}
