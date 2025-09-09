package service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import util.HttpClientUtil;

import java.math.BigDecimal;

public class IOLApiService {

    private final IOLAuthService authService;

    public IOLApiService(IOLAuthService authService) {
        this.authService = authService;
    }

    public String getEstadoCuenta() throws Exception {
        String url = "https://api.invertironline.com/api/v2/estadocuenta";
        return HttpClientUtil.get(url, authService.getToken());
    }

    public String getCotizacion(String simbolo) throws Exception {
        String url = "https://api.invertironline.com/api/v2/BCBA/Titulos/" + simbolo + "/Cotizacion";
        return HttpClientUtil.get(url, authService.getToken());
    }

    public BigDecimal getUltimoValor(String simbolo) throws Exception {
        String respuesta = getCotizacion(simbolo);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(respuesta);
        if (node.has("ultimoPrecio")) {
            return node.get("ultimoPrecio").decimalValue();
        }
        return BigDecimal.ZERO;
    }
}
