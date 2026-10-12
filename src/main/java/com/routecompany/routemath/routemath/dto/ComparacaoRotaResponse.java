package com.routecompany.routemath.routemath.dto;

public record ComparacaoRotaResponse(
        CalcularRotaResponse otima,
        CalcularRotaResponse gulosa,
        double diferencaKm,
        double diferencaPercentual) {

}

