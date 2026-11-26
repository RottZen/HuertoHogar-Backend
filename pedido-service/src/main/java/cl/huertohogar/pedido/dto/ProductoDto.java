package cl.huertohogar.pedido.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** Subconjunto de lo que devuelve producto-service. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ProductoDto(Long id, String codigo, String name, int price, int stock) {
}
