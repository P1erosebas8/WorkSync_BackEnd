package com.WorkSync_BackEnd.domain.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class GoogleAuthRequest {
    @NotBlank(message = "El token de Google es requerido")
    private String token;
}
