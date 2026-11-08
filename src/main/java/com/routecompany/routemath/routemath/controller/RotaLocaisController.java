package com.routecompany.routemath.routemath.controller;

import com.routecompany.routemath.routemath.dto.CalcularPorLocaisRequest;
import com.routecompany.routemath.routemath.dto.RotaPorLocaisResponse;
import com.routecompany.routemath.routemath.service.RotaLocaisService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/rotas")
public class RotaLocaisController {

    private final RotaLocaisService service;

    public RotaLocaisController(RotaLocaisService service) {
        this.service = service;
    }

    @PostMapping("/calcular-por-locais")
    public RotaPorLocaisResponse calcular(@Valid @RequestBody CalcularPorLocaisRequest request) {
        return service.calcular(request);
    }
}
