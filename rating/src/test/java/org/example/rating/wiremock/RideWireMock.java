package org.example.rating.wiremock;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.example.rating.util.DataUtil.CONTENT_TYPE;
import static org.example.rating.util.DataUtil.RIDE_URL;

public class RideWireMock {
    private static final int RIDE_PORT = 8083;
    public static WireMockServer wireMockServer = new WireMockServer(WireMockConfiguration.wireMockConfig()
                .port(RIDE_PORT));

    public static void getRide() {
        wireMockServer.stubFor(
                get(urlEqualTo(RIDE_URL + "/1"))
                        .willReturn(aResponse()
                                .withHeader(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .withStatus(HttpStatus.OK.value())
                                .withBody("""
                                        {
                                            "id": 1,
                                            "driverId": 1,
                                            "passengerId": 1,
                                            "addressFrom": "From",
                                            "addressTo": "To",
                                            "driverRideStatus": "ACCEPTED",
                                            "passengerRideStatus": "WAITING",
                                            "cost": 29.99
                                        }
                                        """)));
    }

    public static void getNonexistentRide() {
        wireMockServer.stubFor(
                get(urlEqualTo(RIDE_URL + "/1"))
                        .willReturn(aResponse()
                                .withHeader(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .withStatus(HttpStatus.NOT_FOUND.value())
                                .withBody("{\"message\": \"Ride was not found\"}")));

    }
}
