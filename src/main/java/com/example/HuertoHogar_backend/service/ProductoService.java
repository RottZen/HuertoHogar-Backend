package com.example.HuertoHogar_backend.service;

import com.example.HuertoHogar_backend.model.Producto;
import com.example.HuertoHogar_backend.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductoService {
    @Autowired
    private ProductoRepository productoRepository;

    public Producto guardarProducto(Producto producto) {
        return productoRepository.save(producto);
    }
}