package com.example.HuertoHogar_backend.service;

import com.example.HuertoHogar_backend.model.Producto;
import com.example.HuertoHogar_backend.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    public List<Producto> obtenerTodos() {
        return productoRepository.findAll();
    }

    public Optional<Producto> obtenerPorId(String id) {
        return productoRepository.findById(id);
    }

    public List<Producto> obtenerPorCategoria(String categoria) {
        return productoRepository.findByCategoria(categoria);
    }

    public Producto guardarProducto(Producto producto) {
        if (producto.getId() == null || producto.getId().isEmpty()) {
            producto.setId(producto.getCodigo());
        }
        return productoRepository.save(producto);
    }

    public void eliminarProducto(String id) {
        productoRepository.deleteById(id);
    }
}