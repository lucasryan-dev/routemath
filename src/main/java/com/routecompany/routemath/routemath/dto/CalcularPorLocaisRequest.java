package com.routecompany.routemath.routemath.dto;

import com.routecompany.routemath.routemath.algoritmo.TipoAlgoritmo;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CalcularPorLocaisRequest(
        @NotNull(message = "O id do depósito é obrigatório.")
        Long depositoId,

        @NotNull(message = "A lista de clientes é obrigatória.")
        @NotEmpty(message = "Informe pelo menos um cliente.")
        List<Long> clientesIds,

        @NotNull(message = "O algoritmo é obrigatório (VIZINHO_MAIS_PROXIMO ou HELD_KARP).")
        TipoAlgoritmo algoritmo) {
}