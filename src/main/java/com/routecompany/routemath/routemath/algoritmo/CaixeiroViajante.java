package com.routecompany.routemath.routemath.algoritmo;

import com.routecompany.routemath.routemath.algoritmo.MatrizDistancias;
import com.routecompany.routemath.routemath.algoritmo.RotaCalculada;
import com.routecompany.routemath.routemath.algoritmo.RotaSolver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Solução exata do Problema do Caixeiro-Viajante com o algoritmo Held-Karp
 * (programação dinâmica com bitmask).
 *
 * DP[mask][j] = menor distância saindo do depósito, visitando exatamente os
 * clientes de "mask" e terminando no cliente j.
 *
 * Tempo: O(n^2 * 2^n)   Memória: O(n * 2^n)
 */
public class CaixeiroViajante implements RotaSolver {

    /** Limite de locais (depósito + clientes) por causa do crescimento exponencial. */
    public static final int MAX_LOCAIS = 18;

    @Override
    public RotaCalculada resolver(MatrizDistancias matriz, int deposito) {
        int n = matriz.quantidadeLocais();
        if (deposito < 0 || deposito >= n) {
            throw new IllegalArgumentException("Índice de depósito inválido: " + deposito);
        }
        if (n > MAX_LOCAIS) {
            throw new IllegalArgumentException(
                    "Held-Karp suporta no máximo " + MAX_LOCAIS + " locais (recebido: " + n + ").");
        }

        // Só o depósito: rota trivial
        if (n == 1) {
            return new RotaCalculada(List.of(deposito, deposito), 0.0, "HELD_KARP", true);
        }

        // Índices dos clientes (todos menos o depósito), numerados de 0 a m-1
        int m = n - 1;
        int[] clientes = new int[m];
        for (int i = 0, k = 0; i < n; i++) {
            if (i != deposito) {
                clientes[k++] = i;
            }
        }

        int total = 1 << m;
        double[][] dp = new double[total][m];
        int[][] anterior = new int[total][m];
        for (int i = 0; i < total; i++) {
            Arrays.fill(dp[i], Double.POSITIVE_INFINITY);
            Arrays.fill(anterior[i], -1);
        }

        // Caso base: depósito -> cliente j
        for (int j = 0; j < m; j++) {
            dp[1 << j][j] = matriz.obterDistancia(deposito, clientes[j]);
        }

        // Transições: de (mask, j) para (mask + k, k)
        for (int mask = 1; mask < total; mask++) {
            for (int j = 0; j < m; j++) {
                if ((mask & (1 << j)) == 0 || dp[mask][j] == Double.POSITIVE_INFINITY) {
                    continue;
                }
                for (int k = 0; k < m; k++) {
                    if ((mask & (1 << k)) != 0) {
                        continue;
                    }
                    int novaMask = mask | (1 << k);
                    double custo = dp[mask][j] + matriz.obterDistancia(clientes[j], clientes[k]);
                    if (custo < dp[novaMask][k]) {
                        dp[novaMask][k] = custo;
                        anterior[novaMask][k] = j;
                    }
                }
            }
        }

        // Fecha o ciclo: último cliente -> depósito
        int todos = total - 1;
        double melhor = Double.POSITIVE_INFINITY;
        int ultimo = -1;
        for (int j = 0; j < m; j++) {
            double custo = dp[todos][j] + matriz.obterDistancia(clientes[j], deposito);
            if (custo < melhor) {
                melhor = custo;
                ultimo = j;
            }
        }

        // Reconstrói a rota de trás para frente usando os predecessores
        List<Integer> rota = new ArrayList<>();
        int mask = todos;
        int atual = ultimo;
        while (atual != -1) {
            rota.add(clientes[atual]);
            int prev = anterior[mask][atual];
            mask &= ~(1 << atual);
            atual = prev;
        }
        Collections.reverse(rota);
        rota.add(0, deposito);
        rota.add(deposito);

        return new RotaCalculada(rota, melhor, "HELD_KARP", true);
    }

}
