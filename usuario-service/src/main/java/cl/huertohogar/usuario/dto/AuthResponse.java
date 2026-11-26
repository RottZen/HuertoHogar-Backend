package cl.huertohogar.usuario.dto;

public record AuthResponse(
        String token,
        Long id,
        String name,
        String email,
        String role,
        Integer puntos) {
}
