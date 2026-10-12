package com.routecompany.routemath.routemath.service;

import com.routecompany.routemath.routemath.algoritmo.*;
import com.routecompany.routemath.routemath.dto.CalcularRotaRequest;
import com.routecompany.routemath.routemath.dto.CalcularRotaResponse;
import com.routecompany.routemath.routemath.dto.ComparacaoRotaResponse;
import com.routecompany.routemath.routemath.dto.CompararRotaRequest;
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

        return executar (solver, matriz, request.deposito());
    }

    private MatrizDistancias criarMatriz(double[][] dados, int deposito) {
        MatrizDistancias matriz = new MatrizDistancias(dados);
        if (deposito >= matriz.quantidadeLocais()) {
            throw new IllegalArgumentException(
                    "O índice do depósito (" + deposito
                            + ") não existe na matriz de " + matriz.quantidadeLocais() + " locais.");
        }
        return matriz;
    }

    public ComparacaoRotaResponse comparar(CompararRotaRequest request) {
        MatrizDistancias matriz = criarMatriz(request.matriz(), request.deposito());

        CalcularRotaResponse otima = executar(new CaixeiroViajante(), matriz, request.deposito());
        CalcularRotaResponse gulosa = executar(new VizinhoMaisProximo(), matriz, request.deposito());

        double diferencaKm = gulosa.distanciaTotal() - otima.distanciaTotal();
        // evita divisão por zero quando a distância ótima é 0
        double diferencaPercentual = otima.distanciaTotal() == 0
                ? 0.0
                : diferencaKm / otima.distanciaTotal() * 100.0;

        return new ComparacaoRotaResponse(otima, gulosa, diferencaKm, diferencaPercentual);
    }

    private CalcularRotaResponse executar(RotaSolver solver, MatrizDistancias matriz, int deposito) {
        long inicio = System.nanoTime();
        RotaCalculada resultado = solver.resolver(matriz, deposito);
        double tempoMs = (System.nanoTime() - inicio) / 1_000_000.0;

        return new CalcularRotaResponse(
                resultado.rota(),
                resultado.distanciaTotal(),
                resultado.algoritmo(),
                resultado.otima(),
                tempoMs);
    }
}
