package com.routecompany.routemath.routemath.service;

import com.routecompany.routemath.routemath.dto.CalcularPorLocaisRequest;
import com.routecompany.routemath.routemath.dto.CalcularRotaRequest;
import com.routecompany.routemath.routemath.dto.CalcularRotaResponse;
import com.routecompany.routemath.routemath.dto.RotaPorLocaisResponse;
import com.routecompany.routemath.routemath.exception.RecursoNaoEncontradoException;
import com.routecompany.routemath.routemath.model.Distancia;
import com.routecompany.routemath.routemath.model.LocalEntrega;
import com.routecompany.routemath.routemath.model.Rota;
import com.routecompany.routemath.routemath.model.TipoLocal;
import com.routecompany.routemath.routemath.repository.DistanciaRepository;
import com.routecompany.routemath.routemath.repository.LocalEntregaRepository;
import com.routecompany.routemath.routemath.repository.RotaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/** Calcula a rota a partir de locais e distâncias cadastrados no banco. */
@Service
public class RotaLocaisService {

    private static final int MAX_PARES_NA_MENSAGEM = 10;

    private final LocalEntregaRepository localRepository;
    private final DistanciaRepository distanciaRepository;
    private final RotaService rotaService;
    private final RotaRepository rotaRepository;

    public RotaLocaisService(LocalEntregaRepository localRepository,
                             DistanciaRepository distanciaRepository,
                             RotaService rotaService,
                             RotaRepository rotaRepository) {
        this.localRepository = localRepository;
        this.distanciaRepository = distanciaRepository;
        this.rotaService = rotaService;
        this.rotaRepository = rotaRepository;
    }

    public RotaPorLocaisResponse calcular(CalcularPorLocaisRequest request) {
        // posição 0 = depósito; as demais = clientes, na ordem recebida
        List<Long> ids = new ArrayList<>();
        ids.add(request.depositoId());
        ids.addAll(request.clientesIds());

        if (new HashSet<>(ids).size() != ids.size()) {
            throw new IllegalArgumentException(
                    "Há ids repetidos: o depósito não pode estar entre os clientes "
                            + "e um cliente não pode aparecer duas vezes.");
        }

        Map<Long, LocalEntrega> locais = carregarLocais(ids);
        validarTipos(request, locais);

        double[][] matriz = montarMatriz(ids, locais);

        // o cálculo em si reaproveita o RotaService (sem gravar: aqui o histórico guarda ids de locais)
        CalcularRotaResponse resultado = rotaService.calcular(
                new CalcularRotaRequest(matriz, 0, request.algoritmo()), false);

        List<Long> rotaIds = resultado.rota().stream().map(ids::get).toList();
        List<String> rotaNomes = rotaIds.stream().map(id -> locais.get(id).getNome()).toList();

        salvarHistorico(resultado, rotaIds);

        return new RotaPorLocaisResponse(
                rotaIds,
                rotaNomes,
                resultado.distanciaTotal(),
                resultado.algoritmo(),
                resultado.otima(),
                resultado.tempoCalculoMs());
    }

    private Map<Long, LocalEntrega> carregarLocais(List<Long> ids) {
        Map<Long, LocalEntrega> locais = new HashMap<>();
        for (LocalEntrega local : localRepository.findAllById(ids)) {
            locais.put(local.getId(), local);
        }
        for (Long id : ids) {
            if (!locais.containsKey(id)) {
                throw new RecursoNaoEncontradoException("Local " + id + " não encontrado.");
            }
        }
        return locais;
    }

    private void validarTipos(CalcularPorLocaisRequest request, Map<Long, LocalEntrega> locais) {
        LocalEntrega deposito = locais.get(request.depositoId());
        if (deposito.getTipo() != TipoLocal.DEPOSITO) {
            throw new IllegalArgumentException(
                    "O local " + deposito.getId() + " (" + deposito.getNome() + ") não é um DEPOSITO.");
        }
        for (Long id : request.clientesIds()) {
            LocalEntrega cliente = locais.get(id);
            if (cliente.getTipo() != TipoLocal.CLIENTE) {
                throw new IllegalArgumentException(
                        "O local " + cliente.getId() + " (" + cliente.getNome() + ") não é um CLIENTE.");
            }
        }
    }

    private double[][] montarMatriz(List<Long> ids, Map<Long, LocalEntrega> locais) {
        Map<String, Double> distancias = new HashMap<>();
        for (Distancia d : distanciaRepository.findByOrigem_IdInAndDestino_IdIn(ids, ids)) {
            distancias.put(chave(d.getOrigem().getId(), d.getDestino().getId()), d.getDistanciaKm());
        }

        int n = ids.size();
        double[][] matriz = new double[n][n];
        List<String> faltando = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i == j) {
                    continue;
                }
                Double km = distancias.get(chave(ids.get(i), ids.get(j)));
                if (km == null) {
                    faltando.add(locais.get(ids.get(i)).getNome() + " -> " + locais.get(ids.get(j)).getNome());
                } else {
                    matriz[i][j] = km;
                }
            }
        }

        if (!faltando.isEmpty()) {
            String lista = faltando.stream().limit(MAX_PARES_NA_MENSAGEM).collect(Collectors.joining("; "));
            String resto = faltando.size() > MAX_PARES_NA_MENSAGEM
                    ? " ... (e mais " + (faltando.size() - MAX_PARES_NA_MENSAGEM) + ")"
                    : "";
            throw new IllegalArgumentException("Faltam distâncias cadastradas (" + faltando.size() + "): " + lista + resto);
        }
        return matriz;
    }

    private void salvarHistorico(CalcularRotaResponse resultado, List<Long> rotaIds) {
        Rota rota = new Rota();
        rota.setAlgoritmo(resultado.algoritmo());
        rota.setSequencia(rotaIds.stream().map(String::valueOf).collect(Collectors.joining(",")));
        rota.setDistanciaTotal(resultado.distanciaTotal());
        rota.setOtima(resultado.otima());
        rota.setTempoCalculoMs(resultado.tempoCalculoMs());
        rota.setDataCalculo(LocalDateTime.now());
        rotaRepository.save(rota);
    }

    private static String chave(Long origemId, Long destinoId) {
        return origemId + ":" + destinoId;
    }
}
