package com.routecompany.routemath.routemath.algoritmo;
import java.util.List;

public class MatrizDistancias {

    private final double[][] distancias;

    public MatrizDistancias(double[][] distancias) {
        validar(distancias);
        // cópia defensiva para a matriz não ser alterada de fora
        this.distancias = new double[distancias.length][];
        for (int i = 0; i < distancias.length; i++) {
            this.distancias[i] = distancias[i].clone();
        }
    }

    private void validar(double[][] m) {
        if (m == null || m.length == 0) {
            throw new IllegalArgumentException("A matriz não pode ser vazia.");
        }
        int n = m.length;
        for (int i = 0; i < n; i++) {
            if (m[i] == null || m[i].length != n) {
                throw new IllegalArgumentException("A matriz deve ser quadrada (n x n).");
            }
            for (int j = 0; j < n; j++) {
                double v = m[i][j];
                if (Double.isNaN(v) || Double.isInfinite(v) || v < 0) {
                    throw new IllegalArgumentException(
                            "Distância inválida em [" + i + "][" + j + "]: deve ser finita e >= 0.");
                }
            }
            if (m[i][i] != 0) {
                throw new IllegalArgumentException("A diagonal deve ser zero (posição [" + i + "][" + i + "]).");
            }
        }
    }

    public double obterDistancia(int origem, int destino) {
        return distancias[origem][destino];
    }

    public int quantidadeLocais() {
        return distancias.length;
    }

    /** Soma as distâncias de cada trecho consecutivo da rota. */
    public double calcularDistanciaRota(List<Integer> rota) {
        double total = 0;
        for (int i = 0; i < rota.size() - 1; i++) {
            total += obterDistancia(rota.get(i), rota.get(i + 1));
        }
        return total;
    }
}
