package cl.huertohogar.usuario.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Mismas reglas que Registro.jsx: nombre >= 2, correo valido, clave >= 6. */
public record RegisterRequest(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(min = 2, message = "El nombre debe tener al menos 2 caracteres")
        String name,

        @NotBlank(message = "El correo es obligatorio")
        @Email(message = "Formato de correo invalido")
        String email,

        @NotBlank(message = "La contrasena es obligatoria")
        @Size(min = 6, message = "La contrasena debe tener al menos 6 caracteres")
        String password) {
}
