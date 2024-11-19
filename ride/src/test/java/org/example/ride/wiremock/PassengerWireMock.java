package org.example.ride.wiremock;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.example.ride.util.DataUtil.CONTENT_TYPE;
import static org.example.ride.util.DataUtil.PASSENGER_URL;

public class PassengerWireMock {
    private static final int PASSENGER_PORT = 8082;
    public static WireMockServer passengerWireMockServer = new WireMockServer(WireMockConfiguration.wireMockConfig()
            .port(PASSENGER_PORT));

    public static void getPassenger() {
        passengerWireMockServer.stubFor(
                get(urlEqualTo(PASSENGER_URL + "/1"))
                        .willReturn(aResponse()
                                .withHeader(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
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
                                .withHeader(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .withStatus(HttpStatus.NOT_FOUND.value())
                                .withBody("{\"message\": \"Passenger was not found\"}")));
    }
}
