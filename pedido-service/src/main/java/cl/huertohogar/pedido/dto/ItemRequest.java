package cl.huertohogar.pedido.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/** Cada item del carrito: id del producto (item.id en CartContext) y quantity. */
public record ItemRequest(
        @NotNull(message = "productoId es obligatorio") Long productoId,
        @Min(value = 1, message = "La cantidad minima es 1") int quantity) {
}
