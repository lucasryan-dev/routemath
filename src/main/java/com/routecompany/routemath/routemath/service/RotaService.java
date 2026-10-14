
package com.routecompany.routemath.routemath.service;

import com.routecompany.routemath.routemath.algoritmo.CaixeiroViajante;
import com.routecompany.routemath.routemath.algoritmo.MatrizDistancias;
import com.routecompany.routemath.routemath.algoritmo.RotaCalculada;
import com.routecompany.routemath.routemath.algoritmo.RotaSolver;
import com.routecompany.routemath.routemath.algoritmo.VizinhoMaisProximo;
import com.routecompany.routemath.routemath.dto.CalcularRotaRequest;
import com.routecompany.routemath.routemath.dto.CalcularRotaResponse;
import com.routecompany.routemath.routemath.dto.ComparacaoRotaResponse;
import com.routecompany.routemath.routemath.dto.CompararRotaRequest;
import com.routecompany.routemath.routemath.dto.RotaHistoricoResponse;
import com.routecompany.routemath.routemath.exception.RecursoNaoEncontradoException;
import com.routecompany.routemath.routemath.model.Rota;
import com.routecompany.routemath.routemath.repository.RotaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RotaService {

    private final RotaRepository rotaRepository;

    public RotaService(RotaRepository rotaRepository) {
        this.rotaRepository = rotaRepository;
    }

    // ---------- cálculo ----------

    /** Calcula e salva no histórico. */
    public CalcularRotaResponse calcular(CalcularRotaRequest request) {
        return calcular(request, true);
    }

    public CalcularRotaResponse calcular(CalcularRotaRequest request, boolean salvar) {
        MatrizDistancias matriz = criarMatriz(request.matriz(), request.deposito());

        RotaSolver solver = switch (request.algoritmo()) {
            case HELD_KARP -> new CaixeiroViajante();
            case VIZINHO_MAIS_PROXIMO -> new VizinhoMaisProximo();
        };

        CalcularRotaResponse resposta = executar(solver, matriz, request.deposito());
        if (salvar) {
            salvar(resposta);
        }
        return resposta;
    }

    /** Compara os dois algoritmos e salva os dois resultados no histórico. */
    public ComparacaoRotaResponse comparar(CompararRotaRequest request) {
        return comparar(request, true);
    }

    public ComparacaoRotaResponse comparar(CompararRotaRequest request, boolean salvar) {
        MatrizDistancias matriz = criarMatriz(request.matriz(), request.deposito());

        CalcularRotaResponse otima = executar(new CaixeiroViajante(), matriz, request.deposito());
        CalcularRotaResponse gulosa = executar(new VizinhoMaisProximo(), matriz, request.deposito());

        double diferencaKm = gulosa.distanciaTotal() - otima.distanciaTotal();
        double diferencaPercentual = otima.distanciaTotal() == 0
                ? 0.0
                : diferencaKm / otima.distanciaTotal() * 100.0;

        if (salvar) {
            salvar(otima);
            salvar(gulosa);
        }
        return new ComparacaoRotaResponse(otima, gulosa, diferencaKm, diferencaPercentual);
    }

    // ---------- histórico ----------

    public List<RotaHistoricoResponse> listarHistorico() {
        return rotaRepository.findAllByOrderByDataCalculoDesc().stream()
                .map(this::paraHistorico)
                .toList();
    }

    public RotaHistoricoResponse buscarPorId(Long id) {
        return rotaRepository.findById(id)
                .map(this::paraHistorico)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Rota " + id + " não encontrada."));
    }

    // ---------- auxiliares ----------

    private MatrizDistancias criarMatriz(double[][] dados, int deposito) {
        MatrizDistancias matriz = new MatrizDistancias(dados);
        if (deposito >= matriz.quantidadeLocais()) {
            throw new IllegalArgumentException(
                    "O índice do depósito (" + deposito
                            + ") não existe na matriz de " + matriz.quantidadeLocais() + " locais.");
        }
        return matriz;
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

    private void salvar(CalcularRotaResponse resposta) {
        Rota rota = new Rota();
        rota.setAlgoritmo(resposta.algoritmo());
        rota.setSequencia(resposta.rota().stream()
                .map(String::valueOf)
                .collect(Collectors.joining(",")));
        rota.setDistanciaTotal(resposta.distanciaTotal());
        rota.setOtima(resposta.otima());
        rota.setTempoCalculoMs(resposta.tempoCalculoMs());
        rota.setDataCalculo(LocalDateTime.now());
        rotaRepository.save(rota);
    }

    private RotaHistoricoResponse paraHistorico(Rota rota) {
        List<Integer> sequencia = Arrays.stream(rota.getSequencia().split(","))
                .map(Integer::parseInt)
                .toList();
        return new RotaHistoricoResponse(
                rota.getId(),
                rota.getAlgoritmo(),
                sequencia,
                rota.getDistanciaTotal(),
                rota.getOtima(),
                rota.getTempoCalculoMs(),
                rota.getDataCalculo());
    }
}
