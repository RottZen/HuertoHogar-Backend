package cl.huertohogar.usuario.controller;

import cl.huertohogar.usuario.dto.PuntosRequest;
import cl.huertohogar.usuario.dto.UpdateProfileRequest;
import cl.huertohogar.usuario.dto.UsuarioResponse;
import cl.huertohogar.usuario.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService service;

    public UsuarioController(UsuarioService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public UsuarioResponse obtener(@PathVariable Long id) {
        return UsuarioResponse.from(service.obtener(id));
    }

    @PutMapping("/{id}")
    public UsuarioResponse actualizarPerfil(@PathVariable Long id,
                                            @Valid @RequestBody UpdateProfileRequest req) {
        return UsuarioResponse.from(service.actualizarPerfil(id, req));
    }

    // Lo llama pedido-service al confirmar una compra
    @PatchMapping("/{id}/puntos")
    public UsuarioResponse ajustarPuntos(@PathVariable Long id, @Valid @RequestBody PuntosRequest req) {
        return UsuarioResponse.from(service.ajustarPuntos(id, req.delta()));
    }
}
