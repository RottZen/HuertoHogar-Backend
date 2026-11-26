package cl.huertohogar.pedido.dto;

import cl.huertohogar.pedido.model.Pedido;

import java.time.LocalDate;
import java.util.List;

/** Fechas en formato ISO (2026-09-05), como espera formatearFecha() de Seguimiento.jsx. */
public record PedidoResponse(
        String codigo,
        Long usuarioId,
        LocalDate fechaPedido,
        LocalDate fechaEntregaEstimada,
        int estado,
        String estadoNombre,
        int total,
        int puntosGanados,
        List<ItemResponse> items) {

    private static final String[] ESTADOS = {"Pedido Confirmado", "En Preparación", "En Camino", "Entregado"};

    public static PedidoResponse from(Pedido p, int puntosGanados) {
        List<ItemResponse> items = p.getItems().stream()
                .map(d -> new ItemResponse(d.getProductoId(), d.getNombre(), d.getPrecioUnitario(),
                        d.getCantidad(), d.getPrecioUnitario() * d.getCantidad()))
                .toList();
        int estado = p.getEstado();
        return new PedidoResponse(p.getCodigo(), p.getUsuarioId(), p.getFechaPedido(),
                p.getFechaEntregaEstimada(), estado, ESTADOS[estado], p.getTotal(), puntosGanados, items);
    }
}
