package com.routecompany.routemath.routemath.algoritmo;


import java.util.List;

/**
 * Resultado de um algoritmo de rota.
 * "otima" só deve ser true quando o algoritmo garante a solução ótima (ex.: Held-Karp).
 */
public record RotaCalculada(
        List<Integer> rota,
        double distanciaTotal,
        String algoritmo,
        boolean otima) {
}


