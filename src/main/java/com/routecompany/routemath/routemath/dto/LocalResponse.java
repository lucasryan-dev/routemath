package com.routecompany.routemath.routemath.dto;

import com.routecompany.routemath.routemath.model.TipoLocal;

public record LocalResponse(
        Long id,
        String nome,
        TipoLocal tipo,
        Double latitude,
        Double longitude) {
}
