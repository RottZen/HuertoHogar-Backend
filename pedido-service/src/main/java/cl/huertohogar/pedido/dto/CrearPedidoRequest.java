package cl.huertohogar.pedido.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/** El precio NO viene del cliente: se lee de producto-service para que no se pueda manipular. */
public record CrearPedidoRequest(
        @NotNull(message = "usuarioId es obligatorio") Long usuarioId,
        @NotEmpty(message = "El pedido debe tener al menos un producto") List<@Valid ItemRequest> items) {
}
