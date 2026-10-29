package com.example.HuertoHogar_backend.controller;

import com.example.HuertoHogar_backend.model.Producto;
import com.example.HuertoHogar_backend.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {
    
    @Autowired
    private ProductoService productoService;

    @PostMapping
    public Producto crearProducto(@RequestBody Producto producto) {
        return productoService.guardarProducto(producto);
    }
}