package cl.huertohogar.usuario.service;

import cl.huertohogar.usuario.dto.AuthResponse;
import cl.huertohogar.usuario.dto.LoginRequest;
import cl.huertohogar.usuario.dto.RegisterRequest;
import cl.huertohogar.usuario.dto.UpdateProfileRequest;
import cl.huertohogar.usuario.model.Usuario;
import cl.huertohogar.usuario.repository.UsuarioRepository;
import cl.huertohogar.usuario.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UsuarioService {

    private static final int PUNTOS_BIENVENIDA = 50;

    private final UsuarioRepository repo;
    private final PasswordEncoder encoder;
    private final JwtService jwtService;

    public UsuarioService(UsuarioRepository repo, PasswordEncoder encoder, JwtService jwtService) {
        this.repo = repo;
        this.encoder = encoder;
        this.jwtService = jwtService;
    }

    /** El rol siempre es "user": nunca se acepta del body. */
    public Usuario registrar(RegisterRequest req) {
        String email = normalizar(req.email());
        if (repo.existsByEmail(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe una cuenta con ese correo");
        }
        Usuario u = new Usuario();
        u.setName(req.name().trim());
        u.setEmail(email);
        u.setPassword(encoder.encode(req.password()));
        u.setPuntos(PUNTOS_BIENVENIDA);
        u.setRole("user");
        return repo.save(u);
    }

    public AuthResponse login(LoginRequest req) {
        Usuario u = repo.findByEmail(normalizar(req.email()))
                .filter(us -> encoder.matches(req.password(), us.getPassword()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                        "Correo o contrasena incorrectos"));
        String token = jwtService.issue(u.getEmail(), u.getRole());
        return new AuthResponse(token, u.getId(), u.getName(), u.getEmail(), u.getRole(), u.getPuntos());
    }

    public Usuario obtener(Long id) {
        return repo.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario " + id + " no encontrado"));
    }

    public Usuario actualizarPerfil(Long id, UpdateProfileRequest req) {
        Usuario u = obtener(id);
        u.setName(req.name().trim());
        if (req.phone() != null) u.setPhone(req.phone().trim());
        if (req.address() != null) u.setAddress(req.address().trim());
        if (req.city() != null) u.setCity(req.city());
        return repo.save(u);
    }

    @Transactional
    public Usuario ajustarPuntos(Long id, int delta) {
        Usuario u = obtener(id);
        int nuevos = u.getPuntos() + delta;
        if (nuevos < 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Puntos insuficientes");
        }
        u.setPuntos(nuevos);
        return repo.save(u);
    }

    private String normalizar(String email) {
        return email.trim().toLowerCase();
    }
}
