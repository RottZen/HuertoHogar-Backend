package cl.huertohogar.pedido.repository;

import cl.huertohogar.pedido.model.Pedido;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    @EntityGraph(attributePaths = "items")
    Optional<Pedido> findByCodigo(String codigo);

    @EntityGraph(attributePaths = "items")
    List<Pedido> findByUsuarioIdOrderByFechaPedidoDescIdDesc(Long usuarioId);
}
