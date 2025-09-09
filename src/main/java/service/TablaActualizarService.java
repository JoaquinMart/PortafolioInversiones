package service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import model.Inversion;
import model.InversionAgrupada;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TablaActualizarService {

    private final IOLAuthService authService;
    private final IOLApiService apiService;

    public TablaActualizarService() {
        authService = new IOLAuthService();
        apiService = new IOLApiService(authService);
    }

    public void actualizarTablaAsync(List<Inversion> inversiones, TablaCallback callback) {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                Map<String, InversionAgrupada> mapa = new HashMap<>();

                BigDecimal totalInvertidoPesos = BigDecimal.ZERO;
                BigDecimal valorActualPesos = BigDecimal.ZERO;
                double totalInvertidoDolares = 0;
                double valorActualDolares = 0;

                ObjectMapper mapper = new ObjectMapper();

                for (Inversion inv : inversiones) {
                    try {
                        String respuesta = apiService.getCotizacion(inv.getNombre());
                        JsonNode node = mapper.readTree(respuesta);

                        BigDecimal precioActual = BigDecimal.ZERO;
                        if (node.has("ultimoPrecio")) {
                            precioActual = node.get("ultimoPrecio").decimalValue();
                        }

                        inv.setPrecioAccion(precioActual);

                    } catch (Exception e) {
                        e.printStackTrace();
                        if (inv.getPrecioAccion() == null) inv.setPrecioAccion(BigDecimal.ZERO);
                    }

                    String key = inv.getNombre().toLowerCase();
                    mapa.putIfAbsent(key, new InversionAgrupada(inv.getNombre()));
                    mapa.get(key).acumular(inv);

                    totalInvertidoPesos = totalInvertidoPesos.add(inv.getTotalPesos());
                    totalInvertidoDolares = inv.getTotalEnDolares();
                    valorActualDolares = inv.getPrecioDolar();
                    valorActualPesos = valorActualPesos.add(inv.getPrecioAccion() != null ? inv.getPrecioAccion() : BigDecimal.ZERO);
                }

                BigDecimal gananciaPesos = valorActualPesos.subtract(totalInvertidoPesos);
                double gananciaDolares = valorActualDolares - totalInvertidoDolares;

                BigDecimal porcentajeGananciaPesos = BigDecimal.ZERO;
                if (totalInvertidoPesos.compareTo(BigDecimal.ZERO) != 0) {
                    porcentajeGananciaPesos = gananciaPesos.divide(totalInvertidoPesos, 4, RoundingMode.HALF_UP).multiply(new BigDecimal("100"));
                }

                double porcentajeGananciaDolares = totalInvertidoDolares != 0 ? (gananciaDolares / totalInvertidoDolares) * 100 : 0;

                BigDecimal finalTotalInvertidoPesos = totalInvertidoPesos;
                BigDecimal finalPorcentajeGananciaPesos = porcentajeGananciaPesos;
                double finalTotalInvertidoDolares = totalInvertidoDolares;
                double finalPorcentajeGananciaDolares = porcentajeGananciaDolares;

                Platform.runLater(() -> callback.onTablaActualizada(
                        mapa.values(),
                        finalTotalInvertidoPesos,
                        finalPorcentajeGananciaPesos,
                        finalTotalInvertidoDolares,
                        finalPorcentajeGananciaDolares
                ));

                return null;
            }
        };

        Thread th = new Thread(task);
        th.setDaemon(true);
        th.start();
    }

    public interface TablaCallback {
        void onTablaActualizada(
                Iterable<InversionAgrupada> inversiones,
                BigDecimal totalPesos,
                BigDecimal porcentajePesos,
                double totalDolares,
                double porcentajeDolares
        );
    }
}
