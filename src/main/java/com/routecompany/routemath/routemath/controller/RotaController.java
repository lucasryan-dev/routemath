package com.routecompany.routemath.routemath.controller;

import com.routecompany.routemath.routemath.dto.CalcularRotaRequest;
import com.routecompany.routemath.routemath.dto.CalcularRotaResponse;
import com.routecompany.routemath.routemath.service.RotaService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/rotas")
public class RotaController {
    private final RotaService rotaService;

    public RotaController(RotaService rotaService) {
        this.rotaService = rotaService;
    }

    @PostMapping("/calcular")
    public CalcularRotaResponse calcular (@Valid @RequestBody CalcularRotaRequest request) {
        return rotaService.calcular(request);
    }
}


