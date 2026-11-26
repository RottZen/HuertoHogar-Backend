package cl.huertohogar.pedido.controller;

import cl.huertohogar.pedido.dto.CrearPedidoRequest;
import cl.huertohogar.pedido.dto.EstadoRequest;
import cl.huertohogar.pedido.dto.PedidoResponse;
import cl.huertohogar.pedido.service.PedidoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoService service;

    public PedidoController(PedidoService service) {
        this.service = service;
    }

    // Confirmar pedido (Carrito.jsx)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PedidoResponse crear(@Valid @RequestBody CrearPedidoRequest req) {
        return service.crear(req);
    }

    // Seguimiento (Seguimiento.jsx): GET /api/pedidos/HH-1001
    @GetMapping("/{codigo}")
    public PedidoResponse obtener(@PathVariable String codigo) {
        return service.obtenerPorCodigo(codigo);
    }

    // Historial de compras (Perfil.jsx): GET /api/pedidos?usuarioId=2
    @GetMapping
    public List<PedidoResponse> historial(@RequestParam Long usuarioId) {
        return service.historial(usuarioId);
    }

    // Cambiar estado (para el administrador)
    @PutMapping("/{codigo}/estado")
    public PedidoResponse cambiarEstado(@PathVariable String codigo, @Valid @RequestBody EstadoRequest req) {
        return service.cambiarEstado(codigo, req.estado());
    }
}
