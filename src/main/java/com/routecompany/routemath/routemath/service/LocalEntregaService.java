package com.routecompany.routemath.routemath.service;

import com.routecompany.routemath.routemath.dto.LocalRequest;
import com.routecompany.routemath.routemath.dto.LocalResponse;
import com.routecompany.routemath.routemath.exception.RecursoNaoEncontradoException;
import com.routecompany.routemath.routemath.model.LocalEntrega;
import com.routecompany.routemath.routemath.repository.LocalEntregaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LocalEntregaService {

    private final LocalEntregaRepository repository;

    public LocalEntregaService(LocalEntregaRepository repository) {
        this.repository = repository;
    }

    public LocalResponse criar(LocalRequest request) {
        LocalEntrega local = new LocalEntrega();
        local.setNome(request.nome().trim());
        local.setTipo(request.tipo());
        local.setLatitude(request.latitude());
        local.setLongitude(request.longitude());
        return paraResponse(repository.save(local));
    }

    public List<LocalResponse> listar() {
        return repository.findAll().stream().map(this::paraResponse).toList();
    }

    public LocalResponse buscar(Long id) {
        return repository.findById(id)
                .map(this::paraResponse)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Local " + id + " não encontrado."));
    }

    private LocalResponse paraResponse(LocalEntrega l) {
        return new LocalResponse(l.getId(), l.getNome(), l.getTipo(), l.getLatitude(), l.getLongitude());
    }
}
