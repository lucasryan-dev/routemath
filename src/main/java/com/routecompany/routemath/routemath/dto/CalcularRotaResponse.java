package com.routecompany.routemath.routemath.dto;

import java.util.List;

public record CalcularRotaResponse(
        List<Integer> rota,
        double distanciaTotal,
        String algoritmo,
        boolean otima,
        double tempoCalculoMs) {
}



