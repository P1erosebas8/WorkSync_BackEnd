package com.WorkSync_BackEnd.domain.dto;

import lombok.Data;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Data
public class ForgotPasswordRequest {
    @NotBlank(message = "El correo es requerido")
    @Email(message = "Formato de correo inválido")
    private String correoElectronico;
}
