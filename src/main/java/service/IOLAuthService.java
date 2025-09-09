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
    public String login(String user, String password) throws Exception {
        String body = "username=" + user + "&password=" + password
                + "&grant_type=password&scope=read";
        String response = HttpClientUtil.post(TOKEN_URL, body, "application/x-www-form-urlencoded");

        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(response);

        this.token = node.get("access_token").asText();
        this.refreshToken = node.get("refresh_token").asText();
        this.expiresIn = node.get("expires_in").asLong();
        this.tokenExpirationTime = LocalDateTime.now().plusSeconds(expiresIn);

        return token;
    }

    /// REFRESCAR TOKEN
    public String refreshToken() throws Exception {
        if (refreshToken == null) {
            throw new Exception("No hay refresh token disponible. Necesitas login inicial.");
        }

        String body = "refresh_token=" + this.refreshToken + "&grant_type=refresh_token";
        String response = HttpClientUtil.post(TOKEN_URL, body, "application/x-www-form-urlencoded");

        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(response);

        this.token = node.get("access_token").asText();
        this.refreshToken = node.get("refresh_token").asText();
        this.expiresIn = node.get("expires_in").asLong();
        this.tokenExpirationTime = LocalDateTime.now().plusSeconds(expiresIn);

        return token;
    }

    /// TOKEN VALIDO
    public String getToken() throws Exception {
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