package cl.huertohogar.pedido.config;

import cl.huertohogar.pedido.model.Pedido;
import cl.huertohogar.pedido.repository.PedidoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * Pedidos de demostracion (los 3 que traia el mock de Seguimiento.jsx) para poder probar
 * el seguimiento de inmediato. Totales inventados. usuarioId = 2 es el cliente de prueba.
 */
@Component
public class DataLoader implements CommandLineRunner {

    private final PedidoRepository repo;

    public DataLoader(PedidoRepository repo) {
        this.repo = repo;
    }

    @Override
    public void run(String... args) {
        if (repo.count() > 0) {
            return;
        }
        repo.save(demo("HH-1001", 1, "2026-09-01", "2026-09-05", 12500));
        repo.save(demo("HH-1002", 3, "2026-08-28", "2026-09-02", 8900));
        repo.save(demo("HH-1003", 0, "2026-09-07", "2026-09-10", 15000));
    }

    private Pedido demo(String codigo, int estado, String fecha, String entrega, int total) {
        Pedido p = new Pedido();
        p.setCodigo(codigo);
        p.setUsuarioId(2L);
        p.setEstado(estado);
        p.setFechaPedido(LocalDate.parse(fecha));
        p.setFechaEntregaEstimada(LocalDate.parse(entrega));
        p.setTotal(total);
        return p;
    }
}
