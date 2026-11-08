package com.routecompany.routemath.routemath.service;

import com.routecompany.routemath.routemath.dto.DistanciaRequest;
import com.routecompany.routemath.routemath.dto.DistanciaResponse;
import com.routecompany.routemath.routemath.exception.RecursoNaoEncontradoException;
import com.routecompany.routemath.routemath.model.Distancia;
import com.routecompany.routemath.routemath.model.LocalEntrega;
import com.routecompany.routemath.routemath.repository.DistanciaRepository;
import com.routecompany.routemath.routemath.repository.LocalEntregaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class DistanciaService {

    private final DistanciaRepository distanciaRepository;
    private final LocalEntregaRepository localRepository;

    public DistanciaService(DistanciaRepository distanciaRepository, LocalEntregaRepository localRepository) {
        this.distanciaRepository = distanciaRepository;
        this.localRepository = localRepository;
    }

    /** Cadastra (ou atualiza) a distância origem -> destino e, por padrão, também destino -> origem. */
    @Transactional
    public List<DistanciaResponse> cadastrar(DistanciaRequest request) {
        if (request.origemId().equals(request.destinoId())) {
            throw new IllegalArgumentException("A origem e o destino devem ser locais diferentes.");
        }
        LocalEntrega origem = buscarLocal(request.origemId());
        LocalEntrega destino = buscarLocal(request.destinoId());

        List<DistanciaResponse> resultado = new ArrayList<>();
        resultado.add(paraResponse(salvarOuAtualizar(origem, destino, request.distanciaKm())));

        boolean bidirecional = request.bidirecional() == null || request.bidirecional();
        if (bidirecional) {
            resultado.add(paraResponse(salvarOuAtualizar(destino, origem, request.distanciaKm())));
        }
        return resultado;
    }

    public List<DistanciaResponse> listar() {
        return distanciaRepository.findAll().stream().map(this::paraResponse).toList();
    }

    private Distancia salvarOuAtualizar(LocalEntrega origem, LocalEntrega destino, Double km) {
        Distancia distancia = distanciaRepository
                .findByOrigem_IdAndDestino_Id(origem.getId(), destino.getId())
                .orElseGet(Distancia::new);
        distancia.setOrigem(origem);
        distancia.setDestino(destino);
        distancia.setDistanciaKm(km);
        return distanciaRepository.save(distancia);
    }

    private LocalEntrega buscarLocal(Long id) {
        return localRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Local " + id + " não encontrado."));
    }

    private DistanciaResponse paraResponse(Distancia d) {
        return new DistanciaResponse(
                d.getId(),
                d.getOrigem().getId(), d.getOrigem().getNome(),
                d.getDestino().getId(), d.getDestino().getNome(),
                d.getDistanciaKm());
    }
}
