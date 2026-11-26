package cl.huertohogar.producto.repository;

import cl.huertohogar.producto.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    boolean existsByCodigo(String codigo);

    List<Producto> findByCategoria(String categoria);

    List<Producto> findByNameContainingIgnoreCase(String texto);

    List<Producto> findByCategoriaAndNameContainingIgnoreCase(String categoria, String texto);
}
