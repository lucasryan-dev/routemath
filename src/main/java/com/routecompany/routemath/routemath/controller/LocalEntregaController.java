package com.routecompany.routemath.routemath.controller;

import com.routecompany.routemath.routemath.dto.LocalRequest;
import com.routecompany.routemath.routemath.dto.LocalResponse;
import com.routecompany.routemath.routemath.service.LocalEntregaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/locais")
public class LocalEntregaController {

    private final LocalEntregaService service;

    public LocalEntregaController(LocalEntregaService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LocalResponse criar(@Valid @RequestBody LocalRequest request) {
        return service.criar(request);
    }

    @GetMapping
    public List<LocalResponse> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public LocalResponse buscar(@PathVariable Long id) {
        return service.buscar(id);
    }
}
