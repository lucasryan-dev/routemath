package com.routecompany.routemath.routemath.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * "bidirecional" (padrão: true) grava também o sentido contrário com a mesma distância.
 * Use false para ruas de mão única (distâncias assimétricas).
 */
public record DistanciaRequest(
        @NotNull(message = "O id da origem é obrigatório.")
        Long origemId,

        @NotNull(message = "O id do destino é obrigatório.")
        Long destinoId,

        @NotNull(message = "A distância em km é obrigatória.")
        @Positive(message = "A distância deve ser maior que zero.")
        Double distanciaKm,

        Boolean bidirecional) {
}