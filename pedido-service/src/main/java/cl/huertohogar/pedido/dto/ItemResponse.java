package cl.huertohogar.pedido.dto;

public record ItemResponse(Long productoId, String name, int price, int quantity, int subtotal) {
}
