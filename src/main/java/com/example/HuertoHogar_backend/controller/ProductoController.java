package com.example.HuertoHogar_backend.controller;

import com.example.HuertoHogar_backend.model.Producto;
import com.example.HuertoHogar_backend.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
@CrossOrigin(origins = "*") // Permite peticiones desde la PWA / App Móvil
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @GetMapping
    public ResponseEntity<List<Producto>> listarTodos() {
        return ResponseEntity.ok(productoService.obtenerTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Producto> obtenerPorId(@PathVariable String id) {
        return productoService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<List<Producto>> obtenerPorCategoria(@PathVariable String categoria) {
        return ResponseEntity.ok(productoService.obtenerPorCategoria(categoria));
    }

    @PostMapping
    public ResponseEntity<Producto> crearProducto(@RequestBody Producto producto) {
        Producto nuevo = productoService.guardarProducto(producto);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Producto> actualizarProducto(@PathVariable String id, @RequestBody Producto producto) {
        return productoService.obtenerPorId(id)
                .map(existente -> {
                    producto.setId(id);
                    return ResponseEntity.ok(productoService.guardarProducto(producto));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProducto(@PathVariable String id) {
        if (productoService.obtenerPorId(id).isPresent()) {
            productoService.eliminarProducto(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}