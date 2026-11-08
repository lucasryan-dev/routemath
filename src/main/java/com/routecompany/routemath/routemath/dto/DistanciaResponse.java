package com.routecompany.routemath.routemath.dto;

public record DistanciaResponse(
        Long id,
        Long origemId,
        String origemNome,
        Long destinoId,
        String destinoNome,
        Double distanciaKm) {
}
