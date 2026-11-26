package cl.huertohogar.producto.service;

import cl.huertohogar.producto.model.Producto;
import cl.huertohogar.producto.repository.ProductoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;

@Service
public class ProductoService {

    private static final Set<String> CATEGORIAS = Set.of("frutas", "verduras", "organicos", "lacteos");

    private final ProductoRepository repo;

    public ProductoService(ProductoRepository repo) {
        this.repo = repo;
    }

    public List<Producto> listar(String categoria, String buscar) {
        boolean hayCategoria = categoria != null && !categoria.isBlank() && !categoria.equals("todos");
        boolean hayTexto = buscar != null && !buscar.isBlank();

        if (hayCategoria && hayTexto) {
            return repo.findByCategoriaAndNameContainingIgnoreCase(categoria, buscar.trim());
        }
        if (hayCategoria) {
            return repo.findByCategoria(categoria);
        }
        if (hayTexto) {
            return repo.findByNameContainingIgnoreCase(buscar.trim());
        }
        return repo.findAll();
    }

    public Producto obtener(Long id) {
        return repo.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto " + id + " no encontrado"));
    }

    public Producto crear(Producto p) {
        p.setId(null);
        p.setCodigo(p.getCodigo().trim().toUpperCase());
        p.setName(p.getName().trim());
        validarCategoria(p.getCategoria());
        if (repo.existsByCodigo(p.getCodigo())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un producto con el codigo " + p.getCodigo());
        }
        return repo.save(p);
    }

    /** Actualizacion parcial: solo se pisan los campos que vienen con valor. */
    public Producto actualizar(Long id, Producto datos) {
        Producto actual = obtener(id);

        if (datos.getCodigo() != null && !datos.getCodigo().isBlank()) {
            String nuevoCodigo = datos.getCodigo().trim().toUpperCase();
            if (!nuevoCodigo.equals(actual.getCodigo())) {
                if (repo.existsByCodigo(nuevoCodigo)) {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un producto con el codigo " + nuevoCodigo);
                }
                actual.setCodigo(nuevoCodigo);
            }
        }
        if (datos.getName() != null && !datos.getName().isBlank()) {
            actual.setName(datos.getName().trim());
        }
        if (datos.getCategoria() != null && !datos.getCategoria().isBlank()) {
            validarCategoria(datos.getCategoria());
            actual.setCategoria(datos.getCategoria());
        }
        if (datos.getPrice() != null) {
            if (datos.getPrice() < 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El precio no puede ser negativo");
            }
            actual.setPrice(datos.getPrice());
        }
        if (datos.getStock() != null) {
            if (datos.getStock() < 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El stock no puede ser negativo");
            }
            actual.setStock(datos.getStock());
        }
        if (datos.getUnidad() != null && !datos.getUnidad().isBlank()) actual.setUnidad(datos.getUnidad());
        if (datos.getImage() != null) actual.setImage(datos.getImage());
        if (datos.getOrigen() != null) actual.setOrigen(datos.getOrigen());
        if (datos.getDescripcion() != null) actual.setDescripcion(datos.getDescripcion());

        return repo.save(actual);
    }

    public void eliminar(Long id) {
        if (!repo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto " + id + " no encontrado");
        }
        repo.deleteById(id);
    }

    /** Lo usa pedido-service al confirmar una compra. */
    @Transactional
    public Producto ajustarStock(Long id, int delta) {
        Producto p = obtener(id);
        int nuevoStock = p.getStock() + delta;
        if (nuevoStock < 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Stock insuficiente para " + p.getName());
        }
        p.setStock(nuevoStock);
        return repo.save(p);
    }

    private void validarCategoria(String categoria) {
        if (!CATEGORIAS.contains(categoria)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Categoria invalida. Usa: frutas, verduras, organicos o lacteos");
        }
    }
}
