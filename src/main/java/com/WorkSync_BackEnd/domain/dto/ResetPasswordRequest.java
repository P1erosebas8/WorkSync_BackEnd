package com.WorkSync_BackEnd.domain.dto;

import lombok.Data;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
public class ResetPasswordRequest {
    @NotBlank(message = "El correo es requerido")
    @Email(message = "Formato de correo inválido")
    private String correoElectronico;

    @NotBlank(message = "El código OTP es requerido")
    private String otpCode;

    @NotBlank(message = "La nueva contraseña es requerida")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String nuevaContrasena;
}
