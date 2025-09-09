package util;

import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.*;
import org.apache.http.util.EntityUtils;
import java.io.IOException;

public class HttpClientUtil {

    public static String post(String url, String body, String contentType) throws IOException {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(url);
            post.setHeader("Content-Type", contentType);
            post.setEntity(new StringEntity(body));

            try (CloseableHttpResponse response = client.execute(post)) {
                return EntityUtils.toString(response.getEntity());
            }
        }
    }

    public static String get(String url, String bearerToken) throws IOException {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet get = new HttpGet(url);
            get.setHeader("Authorization", "Bearer " + bearerToken);
            get.setHeader("Accept", "application/json");

            try (CloseableHttpResponse response = client.execute(get)) {
                int statusCode = response.getStatusLine().getStatusCode();
                String body = EntityUtils.toString(response.getEntity());

                if (statusCode != 200) {
                    throw new IOException("Error en GET " + url + " - Status: " + statusCode + " - Body: " + body);
                }

                return body;
            }
        }
    }
}
