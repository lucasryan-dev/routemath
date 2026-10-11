package com.routecompany.routemath.routemath.algoritmo;

import com.routecompany.routemath.routemath.algoritmo.MatrizDistancias;
import com.routecompany.routemath.routemath.algoritmo.RotaCalculada;
import com.routecompany.routemath.routemath.algoritmo.RotaSolver;

import java.util.ArrayList;
import java.util.List;

public class VizinhoMaisProximo implements RotaSolver {

    @Override
    public RotaCalculada resolver(MatrizDistancias matriz, int deposito) {
        int n = matriz.quantidadeLocais();
        if (deposito < 0 || deposito >= n) {
            throw new IllegalArgumentException("Índice de depósito inválido: " + deposito);
        }

        boolean[] visitado = new boolean[n];
        List<Integer> rota = new ArrayList<>();

        int atual = deposito;
        visitado[atual] = true;
        rota.add(atual);

        for (int passo = 1; passo < n; passo++) {
            int proximo = -1;
            double menor = Double.MAX_VALUE;
            for (int j = 0; j < n; j++) {
                if (!visitado[j] && matriz.obterDistancia(atual, j) < menor) {
                    menor = matriz.obterDistancia(atual, j);
                    proximo = j;
                }
            }
            visitado[proximo] = true;
            rota.add(proximo);
            atual = proximo;
        }

        rota.add(deposito); // retorno ao depósito


        double total = matriz.calcularDistanciaRota(rota);
        return new RotaCalculada(rota, total, "VIZINHO_MAIS_PROXIMO", false);
    }
}
