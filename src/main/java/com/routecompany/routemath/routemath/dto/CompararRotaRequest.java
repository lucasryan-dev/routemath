package com.routecompany.routemath.routemath.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record CompararRotaRequest(
        @NotNull(message = "A matriz é obrigatória.")
        @NotEmpty(message = "A matriz não pode ser vazia.")
        double[][] matriz,

        @NotNull(message = "O índice do depósito é obrigatório.")
        @PositiveOrZero(message = "O índice do depósito não pode ser negativo.")
        Integer deposito) {

}

