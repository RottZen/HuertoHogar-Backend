package cl.huertohogar.pedido.client;

import cl.huertohogar.pedido.dto.DeltaRequest;
import cl.huertohogar.pedido.dto.ProductoDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;

/** Llama a producto-service (puerto 8080). */
@Component
public class ProductoClient {

    private final RestClient client;

    public ProductoClient(@Value("${servicios.producto.url}") String baseUrl) {
        JdkClientHttpRequestFactory factory = new JdkClientHttpRequestFactory();
        factory.setReadTimeout(Duration.ofSeconds(5));
        this.client = RestClient.builder().baseUrl(baseUrl).requestFactory(factory).build();
    }

    public ProductoDto obtener(Long id) {
        try {
            return client.get().uri("/api/productos/{id}", id).retrieve().body(ProductoDto.class);
        } catch (HttpClientErrorException.NotFound e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El producto " + id + " no existe");
        } catch (RestClientException e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "producto-service no responde");
        }
    }

    /** delta negativo descuenta stock, positivo lo devuelve. */
    public void ajustarStock(Long id, int delta) {
        try {
            client.patch().uri("/api/productos/{id}/stock", id)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new DeltaRequest(delta))
                    .retrieve()
                    .toBodilessEntity();
        } catch (HttpClientErrorException.Conflict e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Stock insuficiente para el producto " + id);
        } catch (RestClientException e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "producto-service no responde");
        }
    }
}
