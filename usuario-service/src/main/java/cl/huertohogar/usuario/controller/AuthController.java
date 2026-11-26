package cl.huertohogar.usuario.controller;

import cl.huertohogar.usuario.dto.AuthResponse;
import cl.huertohogar.usuario.dto.LoginRequest;
import cl.huertohogar.usuario.dto.RegisterRequest;
import cl.huertohogar.usuario.dto.UsuarioResponse;
import cl.huertohogar.usuario.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UsuarioService service;

    public AuthController(UsuarioService service) {
        this.service = service;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioResponse registrar(@Valid @RequestBody RegisterRequest req) {
        return UsuarioResponse.from(service.registrar(req));
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest req) {
        return service.login(req);
    }
}
