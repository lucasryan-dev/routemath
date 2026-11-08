package com.routecompany.routemath.routemath.algoritmo;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

/**
 * Experimentos do RouteMath (Fase 4).
 *
 * 1) Desempenho: tempo de execução (força bruta, Held-Karp e vizinho mais próximo)
 *    em função do número de locais.
 * 2) Qualidade: diferença percentual entre a rota gulosa e a ótima em instâncias aleatórias.
 *
 * Os locais são pontos aleatórios em um plano de 100 x 100 e as distâncias são euclidianas.
 * As sementes dos geradores são fixas: o experimento é reprodutível.
 * Os resultados também são gravados em arquivos CSV (separador ";").
 */
public class Experimentos {

    private static final Locale BR = Locale.forLanguageTag("pt-BR");
    private static volatile double sink;          // evita que o compilador descarte os cálculos
    private static double melhorForcaBruta;

    @FunctionalInterface
    private interface Acao {
        void executar();
    }

    public static void main(String[] args) throws IOException {
        System.out.println("Processador: " + Runtime.getRuntime().availableProcessors()
                + " núcleos | Java " + System.getProperty("java.version"));
        System.out.println();
        aquecer();
        experimentoDesempenho();
        System.out.println();
        experimentoQualidade();
    }

    // ---------------------------------------------------------------- desempenho

    private static void experimentoDesempenho() throws IOException {
        int[] tamanhos = {5, 8, 10, 11, 12, 14, 15, 16, 17, 18};
        List<String> csv = new ArrayList<>();
        csv.add("locais;rotas_possiveis;forca_bruta_ms;held_karp_ms;vizinho_ms");

        System.out.println("## Experimento 1 - Tempo de execução (média, em ms)");
        System.out.println();
        System.out.println("| Locais | Rotas possíveis (n-1)! | Força bruta | Held-Karp | Vizinho mais próximo |");
        System.out.println("|---|---|---|---|---|");

        for (int n : tamanhos) {
            final MatrizDistancias m = aleatoria(n, new Random(100 + n));
            int rep = n <= 12 ? 10 : (n <= 15 ? 5 : 3);

            double heldKarp = tempoMedioMs(() -> {
                sink = new CaixeiroViajante().resolver(m, 0).distanciaTotal();
            }, rep);
            double vizinho = tempoMedioMs(() -> {
                sink = new VizinhoMaisProximo().resolver(m, 0).distanciaTotal();
            }, rep);

            Double bruta = null;
            if (n <= 11) {
                bruta = tempoMedioMs(() -> {
                    sink = forcaBruta(m);
                }, n <= 10 ? 3 : 1);
            }

            long rotas = fatorial(n - 1);
            System.out.println(String.format(BR, "| %d | %,d | %s | %s | %s |",
                    n, rotas, bruta == null ? "-" : fmt(bruta), fmt(heldKarp), fmt(vizinho)));
            csv.add(String.format(BR, "%d;%d;%s;%s;%s",
                    n, rotas, bruta == null ? "" : fmt(bruta), fmt(heldKarp), fmt(vizinho)));
        }

        Path arquivo = Path.of("resultados_desempenho.csv");
        Files.write(arquivo, csv, StandardCharsets.UTF_8);
        System.out.println();
        System.out.println("CSV salvo em: " + arquivo.toAbsolutePath());
    }

    // ------------------------------------------------------------------ qualidade

    private static void experimentoQualidade() throws IOException {
        int[] tamanhos = {5, 8, 10, 12};
        int instancias = 500;
        List<String> csv = new ArrayList<>();
        csv.add("locais;instancias;diferenca_media_pct;diferenca_maxima_pct;guloso_igual_otimo_pct");

        System.out.println("## Experimento 2 - Qualidade da rota gulosa em relação à ótima");
        System.out.println();
        System.out.println("Diferença (%) = (D gulosa - D ótima) / D ótima x 100");
        System.out.println();
        System.out.println("| Locais | Instâncias | Diferença média (%) | Diferença máxima (%) | Casos em que o guloso acertou (%) |");
        System.out.println("|---|---|---|---|---|");

        for (int n : tamanhos) {
            Random r = new Random(2000 + n);
            double soma = 0;
            double maxima = 0;
            int iguais = 0;

            for (int i = 0; i < instancias; i++) {
                MatrizDistancias m = aleatoria(n, r);
                double otimo = new CaixeiroViajante().resolver(m, 0).distanciaTotal();
                double guloso = new VizinhoMaisProximo().resolver(m, 0).distanciaTotal();
                double diferenca = otimo == 0 ? 0 : (guloso - otimo) / otimo * 100.0;
                soma += diferenca;
                maxima = Math.max(maxima, diferenca);
                if (diferenca < 1e-9) {
                    iguais++;
                }
            }

            double media = soma / instancias;
            double pctIguais = 100.0 * iguais / instancias;
            System.out.println(String.format(BR, "| %d | %d | %.2f | %.2f | %.1f |",
                    n, instancias, media, maxima, pctIguais));
            csv.add(String.format(BR, "%d;%d;%.2f;%.2f;%.1f", n, instancias, media, maxima, pctIguais));
        }

        Path arquivo = Path.of("resultados_qualidade.csv");
        Files.write(arquivo, csv, StandardCharsets.UTF_8);
        System.out.println();
        System.out.println("CSV salvo em: " + arquivo.toAbsolutePath());
    }

    // ------------------------------------------------------------------ auxiliares

    /** Pontos aleatórios em um plano 100 x 100, com distâncias euclidianas. */
    private static MatrizDistancias aleatoria(int n, Random r) {
        double[] x = new double[n];
        double[] y = new double[n];
        for (int i = 0; i < n; i++) {
            x[i] = r.nextDouble() * 100;
            y[i] = r.nextDouble() * 100;
        }
        double[][] d = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                d[i][j] = (i == j) ? 0 : Math.hypot(x[i] - x[j], y[i] - y[j]);
            }
        }
        return new MatrizDistancias(d);
    }

    /** Executa uma vez para aquecer a JVM e devolve a média de "repeticoes" execuções. */
    private static double tempoMedioMs(Acao acao, int repeticoes) {
        acao.executar();
        long total = 0;
        for (int i = 0; i < repeticoes; i++) {
            long inicio = System.nanoTime();
            acao.executar();
            total += System.nanoTime() - inicio;
        }
        return total / (repeticoes * 1_000_000.0);
    }

    private static void aquecer() {
        MatrizDistancias m = aleatoria(10, new Random(1));
        for (int i = 0; i < 30; i++) {
            sink = new CaixeiroViajante().resolver(m, 0).distanciaTotal();
            sink = new VizinhoMaisProximo().resolver(m, 0).distanciaTotal();
        }
    }

    /** Enumera todas as (n-1)! ordens de visita. */
    private static double forcaBruta(MatrizDistancias m) {
        int n = m.quantidadeLocais();
        int[] a = new int[n - 1];
        for (int i = 0; i < a.length; i++) {
            a[i] = i + 1;
        }
        melhorForcaBruta = Double.MAX_VALUE;
        permutar(m, a, 0);
        return melhorForcaBruta;
    }

    private static void permutar(MatrizDistancias m, int[] a, int k) {
        if (k == a.length) {
            double c = m.obterDistancia(0, a[0]);
            for (int i = 0; i < a.length - 1; i++) {
                c += m.obterDistancia(a[i], a[i + 1]);
            }
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

    private static long fatorial(int n) {
        long f = 1;
        for (int i = 2; i <= n; i++) {
            f *= i;
        }
        return f;
    }

    private static String fmt(double ms) {
        return String.format(BR, "%.3f", ms);
    }
}
