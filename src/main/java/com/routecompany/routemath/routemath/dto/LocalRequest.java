package com.routecompany.routemath.routemath.dto;

import com.routecompany.routemath.routemath.model.TipoLocal;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record LocalRequest(
        @NotBlank(message = "O nome é obrigatório.")
        @Size(max = 120, message = "O nome deve ter no máximo 120 caracteres.")
        String nome,

        @NotNull(message = "O tipo é obrigatório (DEPOSITO ou CLIENTE).")
        TipoLocal tipo,

        @DecimalMin(value = "-90.0", message = "Latitude deve estar entre -90 e 90.")
        @DecimalMax(value = "90.0", message = "Latitude deve estar entre -90 e 90.")
        Double latitude,

        @DecimalMin(value = "-180.0", message = "Longitude deve estar entre -180 e 180.")
        @DecimalMax(value = "180.0", message = "Longitude deve estar entre -180 e 180.")
        Double longitude) {
}