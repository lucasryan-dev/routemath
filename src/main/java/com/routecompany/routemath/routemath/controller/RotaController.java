package com.routecompany.routemath.routemath.controller;

import com.routecompany.routemath.routemath.algoritmo.TipoAlgoritmo;
import com.routecompany.routemath.routemath.dto.CalcularRotaRequest;
import com.routecompany.routemath.routemath.dto.CalcularRotaResponse;
import com.routecompany.routemath.routemath.dto.ComparacaoRotaResponse;
import com.routecompany.routemath.routemath.dto.CompararRotaRequest;
import com.routecompany.routemath.routemath.service.RotaService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rotas")
public class RotaController {

    private static final double[][] MATRIZ_EXEMPLO = {
            {0, 12, 10, 19, 8, 15},
            {12, 0, 3, 7, 2, 9},
            {10, 3, 0, 6, 20, 5},
            {19, 7, 6, 0, 4, 11},
            {8, 2, 20, 4, 0, 14},
            {15, 9, 5, 11, 14, 0}
    };


    private final RotaService rotaService;

    public RotaController(RotaService rotaService) {
        this.rotaService = rotaService;
    }

    @PostMapping("/calcular")
    public CalcularRotaResponse calcular (@Valid @RequestBody CalcularRotaRequest request) {
        return rotaService.calcular(request);
    }

    /** Demonstração: abre direto no navegador. */
    @GetMapping("/exemplo")
    public ComparacaoRotaResponse exemplo(){
        return rotaService.comparar(new CompararRotaRequest(MATRIZ_EXEMPLO, 0));
    }

    @GetMapping("/exemplo/{algoritmo}")
    public CalcularRotaResponse exemploAlgoritmo(@PathVariable TipoAlgoritmo algoritmo){
        return rotaService.calcular(new CalcularRotaRequest(MATRIZ_EXEMPLO, 0, algoritmo));
    }

}


