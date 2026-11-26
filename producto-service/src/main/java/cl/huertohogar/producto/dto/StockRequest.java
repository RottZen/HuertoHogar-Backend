package cl.huertohogar.producto.dto;

import jakarta.validation.constraints.NotNull;


public record StockRequest(@NotNull(message = "delta es obligatorio") Integer delta) {
}
