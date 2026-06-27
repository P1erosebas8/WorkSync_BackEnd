package com.WorkSync_BackEnd.domain.dto;

import lombok.Data;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Data
public class VerifyOtpRequest {
    @NotBlank(message = "El correo es requerido")
    @Email(message = "Formato de correo inválido")
    private String correoElectronico;

    @NotBlank(message = "El código OTP es requerido")
    private String otpCode;
}
