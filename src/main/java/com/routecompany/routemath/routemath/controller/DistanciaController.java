package com.routecompany.routemath.routemath.controller;

import com.routecompany.routemath.routemath.dto.DistanciaRequest;
import com.routecompany.routemath.routemath.dto.DistanciaResponse;
import com.routecompany.routemath.routemath.service.DistanciaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/distancias")
public class DistanciaController {

    private final DistanciaService service;

    public DistanciaController(DistanciaService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public List<DistanciaResponse> cadastrar(@Valid @RequestBody DistanciaRequest request) {
        return service.cadastrar(request);
    }

    @GetMapping
    public List<DistanciaResponse> listar() {
        return service.listar();
    }
}
