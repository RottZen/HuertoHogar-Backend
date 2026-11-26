package cl.huertohogar.usuario.dto;

import jakarta.validation.constraints.NotNull;

/** delta positivo = suma puntos (compra), negativo = canje. */
public record PuntosRequest(@NotNull(message = "delta es obligatorio") Integer delta) {
}
