package cl.huertohogar.usuario.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Campos del formulario "Actualizar Mis Datos" de Perfil.jsx. */
public record UpdateProfileRequest(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(min = 2, message = "El nombre debe tener al menos 2 caracteres")
        String name,
        String phone,
        String address,
        String city) {
}
