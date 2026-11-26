package cl.huertohogar.pedido.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record EstadoRequest(
        @Min(value = 0, message = "El estado minimo es 0")
        @Max(value = 3, message = "El estado maximo es 3") int estado) {
}
