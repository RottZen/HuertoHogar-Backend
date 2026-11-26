package cl.huertohogar.usuario.config;

import cl.huertohogar.usuario.model.Usuario;
import cl.huertohogar.usuario.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/** Usuarios de prueba (los mismos del mock del frontend). Cambia las claves antes de desplegar. */
@Component
public class DataLoader implements CommandLineRunner {

    private final UsuarioRepository repo;
    private final PasswordEncoder encoder;

    public DataLoader(UsuarioRepository repo, PasswordEncoder encoder) {
        this.repo = repo;
        this.encoder = encoder;
    }

    @Override
    public void run(String... args) {
        if (repo.count() > 0) {
            return;
        }

        Usuario admin = new Usuario();
        admin.setName("Administrador Principal");
        admin.setEmail("admin@huertohogar.cl");
        admin.setPassword(encoder.encode("admin123"));
        admin.setRole("admin");
        admin.setPuntos(0);
        repo.save(admin);

        Usuario cliente = new Usuario();
        cliente.setName("Juan Pérez");
        cliente.setEmail("cliente@correo.com");
        cliente.setPassword(encoder.encode("cliente123"));
        cliente.setPhone("+56 9 8765 4321");
        cliente.setAddress("Av. Vicuña Mackenna 4860, San Joaquín");
        cliente.setCity("Santiago");
        cliente.setPuntos(250);
        cliente.setRole("user");
        repo.save(cliente);
    }
}
