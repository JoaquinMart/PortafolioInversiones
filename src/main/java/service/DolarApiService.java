package service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import util.HttpClientUtil;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class DolarApiService {
    private static final String URL_MEP = "https://dolarapi.com/v1/dolares/bolsa";
    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    public double getDolarMep() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(URL_MEP))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JsonNode node = mapper.readTree(response.body());
            return node.path("venta").asDouble();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return 0.0;
        }
    }
}