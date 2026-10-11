package com.routecompany.routemath.routemath.algoritmo;

import org.junit.jupiter.api.Test;
import java.util.Random;
import static org.junit.jupiter.api.Assertions.*;

class CaixeiroViajanteTest {

    private double melhorForcaBruta;

    private void permutar(MatrizDistancias m, int[] a, int k) {
        if (k == a.length) {
            double c = m.obterDistancia(0, a[0]);
            for (int i = 0; i < a.length - 1; i++) c += m.obterDistancia(a[i], a[i + 1]);
            c += m.obterDistancia(a[a.length - 1], 0);
            melhorForcaBruta = Math.min(melhorForcaBruta, c);
            return;
        }
        for (int i = k; i < a.length; i++) {
            int t = a[k]; a[k] = a[i]; a[i] = t;
            permutar(m, a, k + 1);
            t = a[k]; a[k] = a[i]; a[i] = t;
        }
    }

    @Test
    void deveEncontrarRotaOtimaDoExemplo() {
        double[][] d = {{0,4,7,3},{4,0,2,6},{7,2,0,5},{3,6,5,0}};
        RotaCalculada r = new CaixeiroViajante().resolver(new MatrizDistancias(d), 0);
        assertEquals(14.0, r.distanciaTotal());
        assertTrue(r.otima());
        assertEquals(0, r.rota().get(0));
        assertEquals(0, r.rota().get(r.rota().size() - 1));
    }

    @Test
    void deveCoincidirComForcaBrutaENaoPiorarQueGuloso() {
        Random rnd = new Random(1);
        for (int it = 0; it < 100; it++) {
            int n = 3 + rnd.nextInt(5);
            double[][] d = new double[n][n];
            for (int i = 0; i < n; i++)
                for (int j = 0; j < n; j++)
                    if (i != j) d[i][j] = 1 + rnd.nextInt(30);
            MatrizDistancias m = new MatrizDistancias(d);

            RotaCalculada hk = new CaixeiroViajante().resolver(m, 0);
            RotaCalculada nn = new VizinhoMaisProximo().resolver(m, 0);

            int[] outros = new int[n - 1];
            for (int i = 0; i < n - 1; i++) outros[i] = i + 1;
            melhorForcaBruta = Double.MAX_VALUE;
            permutar(m, outros, 0);

            assertEquals(melhorForcaBruta, hk.distanciaTotal(), 1e-9);
            assertEquals(hk.distanciaTotal(), m.calcularDistanciaRota(hk.rota()), 1e-9);
            assertTrue(hk.distanciaTotal() <= nn.distanciaTotal() + 1e-9);
        }
    }
}