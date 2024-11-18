package org.example.ride.wireMock;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.example.ride.util.DataUtil.CONTENT_TYPE;
import static org.example.ride.util.DataUtil.DRIVER_URL;

public class DriverWireMock {
    private static final int DRIVER_PORT = 8081;
    public static WireMockServer driverWireMockServer = new WireMockServer(WireMockConfiguration.wireMockConfig()
            .port(DRIVER_PORT));

    public static void getDriver(){
        driverWireMockServer.stubFor(
                get(urlEqualTo(DRIVER_URL + "/1"))
                        .willReturn(aResponse()
                                .withHeader(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .withStatus(HttpStatus.OK.value())
                                .withBody("""
                                        {
                                            "id": 1,
                                            "name": "John Doe",
                                            "email": "john.doe@example.com",
                                            "phone": "+375441234567",
                                            "gender": "MALE",
                                            "carId": 1,
                                            "rating": 4.5
                                        }
                                        """)));

    }

    public static void getNonexistentDriver() {
        driverWireMockServer.stubFor(
                get(urlEqualTo(DRIVER_URL + "/1"))
                        .willReturn(aResponse()
                                .withHeader(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .withStatus(HttpStatus.NOT_FOUND.value())
                                .withBody("{\"message\": \"Driver was not found\"}")));
    }
}
