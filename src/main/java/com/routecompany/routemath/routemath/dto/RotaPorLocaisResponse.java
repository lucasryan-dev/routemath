package com.routecompany.routemath.routemath.dto;

import java.util.List;

public record RotaPorLocaisResponse(
        List<Long> rotaIds,
        List<String> rotaNomes,
        double distanciaTotal,
        String algoritmo,
        boolean otima,
        double tempoCalculoMs) {
}
