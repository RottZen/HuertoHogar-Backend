package cl.huertohogar.producto.controller;

import cl.huertohogar.producto.dto.StockRequest;
import cl.huertohogar.producto.model.Producto;
import cl.huertohogar.producto.service.ProductoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/productos")
public class ProductoController {

    private final ProductoService service;

    public ProductoController(ProductoService service) {
        this.service = service;
    }


    @GetMapping
    public List<Producto> listar(@RequestParam(required = false) String categoria,
                                 @RequestParam(required = false) String buscar) {
        return service.listar(categoria, buscar);
    }

    @GetMapping("/{id}")
    public Producto obtener(@PathVariable Long id) {
        return service.obtener(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Producto crear(@Valid @RequestBody Producto producto) {
        return service.crear(producto);
    }

    @PutMapping("/{id}")
    public Producto actualizar(@PathVariable Long id, @RequestBody Producto producto) {
        return service.actualizar(id, producto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        service.eliminar(id);
    }


    @PatchMapping("/{id}/stock")
    public Producto ajustarStock(@PathVariable Long id, @Valid @RequestBody StockRequest req) {
        return service.ajustarStock(id, req.delta());
    }
}
