package cl.huertohogar.usuario.dto;

import cl.huertohogar.usuario.model.Usuario;

/** Lo que ve el frontend. Nunca incluye la contrasena. */
public record UsuarioResponse(
        Long id,
        String name,
        String email,
        String phone,
        String address,
        String city,
        Integer puntos,
        String role) {

    public static UsuarioResponse from(Usuario u) {
        return new UsuarioResponse(u.getId(), u.getName(), u.getEmail(), u.getPhone(),
                u.getAddress(), u.getCity(), u.getPuntos(), u.getRole());
    }
}
