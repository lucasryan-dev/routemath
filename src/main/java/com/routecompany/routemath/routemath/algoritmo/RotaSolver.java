package com.routecompany.routemath.routemath.algoritmo;

public interface RotaSolver {

    /**
     * @param matriz   matriz de distâncias entre todos os locais
     * @param deposito índice do depósito na matriz (início e fim da rota)
     */
    RotaCalculada resolver(MatrizDistancias matriz, int deposito);
}
