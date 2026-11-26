package cl.huertohogar.pedido.client;

import cl.huertohogar.pedido.dto.DeltaRequest;
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

/** Llama a usuario-service (puerto 8081). */
@Component
public class UsuarioClient {

    private final RestClient client;

    public UsuarioClient(@Value("${servicios.usuario.url}") String baseUrl) {
        JdkClientHttpRequestFactory factory = new JdkClientHttpRequestFactory();
        factory.setReadTimeout(Duration.ofSeconds(5));
        this.client = RestClient.builder().baseUrl(baseUrl).requestFactory(factory).build();
    }

    /** Lanza 400 si el usuario no existe. */
    public void verificarExiste(Long usuarioId) {
        try {
            client.get().uri("/api/usuarios/{id}", usuarioId).retrieve().toBodilessEntity();
        } catch (HttpClientErrorException.NotFound e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El usuario " + usuarioId + " no existe");
        } catch (RestClientException e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "usuario-service no responde");
        }
    }

    public void sumarPuntos(Long usuarioId, int puntos) {
        client.patch().uri("/api/usuarios/{id}/puntos", usuarioId)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new DeltaRequest(puntos))
                .retrieve()
                .toBodilessEntity();
    }
}
