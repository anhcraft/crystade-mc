package dev.anhcraft.crystade.common;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;

/**
 * Platform-agnostic service for pushing configuration to the Crystade integration API.
 * Follows the {@code push-config-example} specification.
 */
public final class PushService {

    static final String INTEGRATION_ENDPOINT = "https://integration.crystade.com/configuration";

    private final HttpClient httpClient;

    public PushService() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(30))
                .build();
    }

    /**
     * Pushes the YAML configuration file to Crystade.
     *
     * @param configFilePath path to the {@code crystade.yml} file on disk
     * @param apiKey         the Crystade API key (Bearer token)
     * @return the HTTP status code from the integration endpoint
     * @throws IOException          if an I/O error occurs reading the file or during the request
     * @throws InterruptedException if the HTTP request is interrupted
     */
    public int push(Path configFilePath, String apiKey) throws IOException, InterruptedException {
        String yamlContent = Files.readString(configFilePath);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(INTEGRATION_ENDPOINT))
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/yaml")
                .POST(HttpRequest.BodyPublishers.ofString(yamlContent))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return response.statusCode();
    }
}
