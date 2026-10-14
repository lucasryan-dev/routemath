package com.routecompany.routemath.routemath.controller;

import com.routecompany.routemath.routemath.algoritmo.TipoAlgoritmo;
import com.routecompany.routemath.routemath.dto.CalcularRotaRequest;
import com.routecompany.routemath.routemath.dto.CalcularRotaResponse;
import com.routecompany.routemath.routemath.dto.ComparacaoRotaResponse;
import com.routecompany.routemath.routemath.dto.CompararRotaRequest;
import com.routecompany.routemath.routemath.dto.RotaHistoricoResponse;
import com.routecompany.routemath.routemath.service.RotaService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
    public CalcularRotaResponse calcular(@Valid @RequestBody CalcularRotaRequest request) {
        return rotaService.calcular(request);
    }

    @PostMapping("/comparar")
    public ComparacaoRotaResponse comparar(@Valid @RequestBody CompararRotaRequest request) {
        return rotaService.comparar(request);
    }

    /** Histórico de cálculos, do mais recente para o mais antigo. */
    @GetMapping
    public List<RotaHistoricoResponse> listar() {
        return rotaService.listarHistorico();
    }

    @GetMapping("/{id}")
    public RotaHistoricoResponse buscar(@PathVariable Long id) {
        return rotaService.buscarPorId(id);
    }

    /** Demonstração (não grava no histórico). */
    @GetMapping("/exemplo")
    public ComparacaoRotaResponse exemplo() {
        return rotaService.comparar(new CompararRotaRequest(MATRIZ_EXEMPLO, 0), false);
    }

    /** Demonstração com um algoritmo (não grava no histórico). */
    @GetMapping("/exemplo/{algoritmo}")
    public CalcularRotaResponse exemploAlgoritmo(@PathVariable TipoAlgoritmo algoritmo) {
        return rotaService.calcular(new CalcularRotaRequest(MATRIZ_EXEMPLO, 0, algoritmo), false);
    }
}


