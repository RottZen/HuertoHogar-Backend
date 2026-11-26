package cl.huertohogar.usuario.security;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import cl.huertohogar.usuario.model.Usuario;
import cl.huertohogar.usuario.repository.UsuarioRepository;
@Service 
public class UsuarioDetailsService implements UserDetailsService {
   private final UsuarioRepository usuarioRepository;

    public UsuarioDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));
        return User.builder()
                .username(usuario.getName())
                .password(usuario.getPassword())
                .roles("USER") // Puedes asignar roles según tu lógica
                .build();
    }
}