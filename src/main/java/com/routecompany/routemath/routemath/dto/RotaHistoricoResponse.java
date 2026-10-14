package com.routecompany.routemath.routemath.dto;

import java.time.LocalDateTime;
import java.util.List;

public record RotaHistoricoResponse(
        Long id,
        String algoritmo,
        List<Integer> rota,
        double distanciaTotal,
        boolean otima,
        double tempoCalculoMs,
        LocalDateTime dataCalculo) {

}
