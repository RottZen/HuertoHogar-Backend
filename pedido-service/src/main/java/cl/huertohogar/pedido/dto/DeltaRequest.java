package cl.huertohogar.pedido.dto;

/** Body de PATCH .../stock y PATCH .../puntos en los otros servicios. */
public record DeltaRequest(int delta) {
}
