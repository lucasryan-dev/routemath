package com.routecompany.routemath.routemath.service;

import com.routecompany.routemath.routemath.algoritmo.*;
import com.routecompany.routemath.routemath.dto.CalcularRotaRequest;
import com.routecompany.routemath.routemath.dto.CalcularRotaResponse;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

@Service
public class RotaService {

    public CalcularRotaResponse calcular(@Valid CalcularRotaRequest request) {
        MatrizDistancias matriz = new MatrizDistancias(request.matriz());

        if (request.deposito() >= matriz.quantidadeLocais()) {
            throw new IllegalArgumentException("O índice do depósito (" + request.deposito()
                    + ") não existe na matriz de " + matriz.quantidadeLocais() + " locais.");
        }

        RotaSolver solver = switch (request.algoritmo()) {
            case HELD_KARP -> new CaixeiroViajante();
            case VIZINHO_MAIS_PROXIMO -> new VizinhoMaisProximo();
        };

        long inicio = System.nanoTime();
        RotaCalculada resultado = solver.resolver(matriz, request.deposito());
        double tempoMs = (System.nanoTime() - inicio) / 1_000_000.0;

        return new CalcularRotaResponse(
                resultado.rota(),
                resultado.distanciaTotal(),
                resultado.algoritmo(),
                resultado.otima(),
                tempoMs);
    }
}
