package service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import util.HttpClientUtil;
import java.time.LocalDateTime;

public class IOLAuthService {

    private static final String TOKEN_URL = "https://api.invertironline.com/token";

    private String token;
    private LocalDateTime tokenExpirationTime;
    private String refreshToken;
    private long expiresIn;

    private final String username = "jm951659@gmail.com";
    private final String password = "Colore2015.";

    /// LOGIN
    private static final int MAX_RETRIES = 3;
    private static final long RETRY_DELAY_MS = 2000; // 2 segundos

    public String login(String user, String password) throws Exception {
        int attempts = 0;
        while (attempts < MAX_RETRIES) {
            attempts++;
            String body = "username=" + user + "&password=" + password
                    + "&grant_type=password&scope=read";
            String response = HttpClientUtil.post(TOKEN_URL, body, "application/x-www-form-urlencoded");

            if (response != null && response.trim().startsWith("{")) {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode node = mapper.readTree(response);

                this.token = node.get("access_token").asText();
                this.refreshToken = node.get("refresh_token").asText();
                this.expiresIn = node.get("expires_in").asLong();
                this.tokenExpirationTime = LocalDateTime.now().plusSeconds(expiresIn);

                return token;
            } else {
                System.err.println("⚠ Respuesta inesperada de la API al login (intento " + attempts + "): " + response);
                Thread.sleep(RETRY_DELAY_MS);
            }
        }

        throw new Exception("Error al loguearse: respuesta inválida de la API después de " + MAX_RETRIES + " intentos.");
    }

    /// REFRESCAR TOKEN
    public String refreshToken() throws Exception {
        if (refreshToken == null) {
            throw new Exception("No hay refresh token disponible. Necesitas login inicial.");
        }

        int attempts = 0;
        while (attempts < MAX_RETRIES) {
            attempts++;
            String body = "refresh_token=" + this.refreshToken + "&grant_type=refresh_token";
            String response = HttpClientUtil.post(TOKEN_URL, body, "application/x-www-form-urlencoded");

            if (response != null && response.trim().startsWith("{")) {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode node = mapper.readTree(response);

                this.token = node.get("access_token").asText();
                this.refreshToken = node.get("refresh_token").asText();
                this.expiresIn = node.get("expires_in").asLong();
                this.tokenExpirationTime = LocalDateTime.now().plusSeconds(expiresIn);

                return token;
            } else {
                System.err.println("⚠ Respuesta inesperada de la API al refrescar token (intento " + attempts + "): " + response);
                Thread.sleep(RETRY_DELAY_MS);
            }
        }

        throw new Exception("Error al refrescar token: respuesta inválida de la API después de " + MAX_RETRIES + " intentos.");
    }

    /// TOKEN VALIDO
    public synchronized String getToken() throws Exception {
        if (token == null || tokenExpirationTime == null || LocalDateTime.now().isAfter(tokenExpirationTime.minusMinutes(1))) {
            if (refreshToken != null) {
                refreshToken();
            } else {
                login(username, password);
            }
        }
        return token;
    }
}