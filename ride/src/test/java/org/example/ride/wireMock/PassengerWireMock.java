package org.example.ride.wireMock;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.example.ride.util.DataUtil.PASSENGER_URL;

@Component
public class PassengerWireMock {

    public static WireMockServer passengerWireMockServer = new WireMockServer(WireMockConfiguration.wireMockConfig()
            .port(8082));

    public static void getPassenger() {
        passengerWireMockServer.stubFor(
                get(urlEqualTo(PASSENGER_URL + "/1"))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                                .withStatus(HttpStatus.OK.value())
                                .withBody("""
                                        {
                                            "id": 1,
                                            "name": "Jane Smith",
                                            "email": "jane.smith@example.com",
                                            "phone": "+375441234567",
                                            "rating": 4.8
                                        }
                                        """)));
    }



    public static void getNonexistentPassenger() {
        passengerWireMockServer.stubFor(
                get(urlEqualTo(PASSENGER_URL + "/1"))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                                .withStatus(HttpStatus.NOT_FOUND.value())
                                .withBody("{\"message\": \"Passenger was not found\"}")));
    }
}
