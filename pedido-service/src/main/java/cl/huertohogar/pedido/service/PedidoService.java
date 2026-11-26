package cl.huertohogar.pedido.service;

import cl.huertohogar.pedido.client.ProductoClient;
import cl.huertohogar.pedido.client.UsuarioClient;
import cl.huertohogar.pedido.dto.CrearPedidoRequest;
import cl.huertohogar.pedido.dto.ItemRequest;
import cl.huertohogar.pedido.dto.PedidoResponse;
import cl.huertohogar.pedido.dto.ProductoDto;
import cl.huertohogar.pedido.model.DetallePedido;
import cl.huertohogar.pedido.model.Pedido;
import cl.huertohogar.pedido.repository.PedidoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class PedidoService {

    private static final Logger log = LoggerFactory.getLogger(PedidoService.class);
    private static final int DIAS_ENTREGA = 4;

    private final PedidoRepository repo;
    private final ProductoClient productoClient;
    private final UsuarioClient usuarioClient;

    public PedidoService(PedidoRepository repo, ProductoClient productoClient, UsuarioClient usuarioClient) {
        this.repo = repo;
        this.productoClient = productoClient;
        this.usuarioClient = usuarioClient;
    }

    /**
     * Equivale a confirmarPedido() del carrito:
     * valida usuario y stock, calcula el total con los precios reales,
     * descuenta stock y suma el 10 % del total en HuertoPuntos.
     */
    public PedidoResponse crear(CrearPedidoRequest req) {
        usuarioClient.verificarExiste(req.usuarioId());

        // Si el mismo producto viene repetido, se suman las cantidades
        Map<Long, Integer> cantidades = new LinkedHashMap<>();
        for (ItemRequest item : req.items()) {
            cantidades.merge(item.productoId(), item.quantity(), Integer::sum);
        }

        Pedido pedido = new Pedido();
        pedido.setCodigo("TMP-" + UUID.randomUUID().toString().substring(0, 8)); // se reemplaza tras guardar
        pedido.setUsuarioId(req.usuarioId());
        pedido.setFechaPedido(LocalDate.now());
        pedido.setFechaEntregaEstimada(LocalDate.now().plusDays(DIAS_ENTREGA));
        pedido.setEstado(0);

        int total = 0;
        for (Map.Entry<Long, Integer> e : cantidades.entrySet()) {
            ProductoDto producto = productoClient.obtener(e.getKey());
            if (producto.stock() < e.getValue()) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "Stock insuficiente para " + producto.name() + " (disponible: " + producto.stock() + ")");
            }
            DetallePedido detalle = new DetallePedido();
            detalle.setProductoId(producto.id());
            detalle.setNombre(producto.name());
            detalle.setPrecioUnitario(producto.price());
            detalle.setCantidad(e.getValue());
            pedido.agregarItem(detalle);
            total += producto.price() * e.getValue();
        }
        pedido.setTotal(total);

        // Descuenta stock; si algo falla, devuelve lo que ya se habia descontado
        Map<Long, Integer> descontados = new LinkedHashMap<>();
        Pedido guardado;
        try {
            for (Map.Entry<Long, Integer> e : cantidades.entrySet()) {
                productoClient.ajustarStock(e.getKey(), -e.getValue());
                descontados.put(e.getKey(), e.getValue());
            }
            guardado = repo.save(pedido);
            guardado.setCodigo("HH-" + (1000 + guardado.getId()));
            guardado = repo.save(guardado);
        } catch (RuntimeException ex) {
            descontados.forEach((id, cant) -> {
                try {
                    productoClient.ajustarStock(id, cant);
                } catch (RuntimeException ignorada) {
                    log.error("No se pudo devolver el stock del producto {}", id);
                }
            });
            throw ex;
        }

        int puntos = total / 10;
        try {
            usuarioClient.sumarPuntos(req.usuarioId(), puntos);
        } catch (RuntimeException ex) {
            // El pedido ya esta confirmado: no se anula por no poder sumar puntos
            log.warn("No se pudieron sumar {} puntos al usuario {}", puntos, req.usuarioId());
            puntos = 0;
        }

        return PedidoResponse.from(guardado, puntos);
    }

    public PedidoResponse obtenerPorCodigo(String codigo) {
        return PedidoResponse.from(buscar(codigo), 0);
    }

    public List<PedidoResponse> historial(Long usuarioId) {
        return repo.findByUsuarioIdOrderByFechaPedidoDescIdDesc(usuarioId).stream()
                .map(p -> PedidoResponse.from(p, 0))
                .toList();
    }

    public PedidoResponse cambiarEstado(String codigo, int estado) {
        Pedido pedido = buscar(codigo);
        pedido.setEstado(estado);
        return PedidoResponse.from(repo.save(pedido), 0);
    }

    private Pedido buscar(String codigo) {
        // Acepta "HH-1001", "hh-1001" o "#HH-1001"
        String limpio = codigo.trim().toUpperCase().replace("#", "");
        return repo.findByCodigo(limpio).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe un pedido con el codigo " + limpio));
    }
}
