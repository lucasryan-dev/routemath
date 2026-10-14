package com.routecompany.routemath.routemath.service;

import com.routecompany.routemath.routemath.dto.ComparacaoRotaResponse;
import com.routecompany.routemath.routemath.dto.CompararRotaRequest;
import com.routecompany.routemath.routemath.repository.RotaRepository;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class RotaServiceTest {

    // repositório simulado: o teste não precisa de banco de dados
    private final RotaService service = new RotaService(mock(RotaRepository.class));

    @Test
    void deveCompararAlgoritmosNaMatrizDeSeisLocais() {
        double[][] m = {
                {0, 12, 10, 19, 8, 15},
                {12, 0, 3, 7, 2, 9},
                {10, 3, 0, 6, 20, 5},
                {19, 7, 6, 0, 4, 11},
                {8, 2, 20, 4, 0, 14},
                {15, 9, 5, 11, 14, 0}
        };

        ComparacaoRotaResponse r = service.comparar(new CompararRotaRequest(m, 0));

        assertEquals(42.0, r.otima().distanciaTotal(), 1e-9);
        assertEquals(48.0, r.gulosa().distanciaTotal(), 1e-9);
        assertTrue(r.otima().otima());
        assertFalse(r.gulosa().otima());
        assertEquals(6.0, r.diferencaKm(), 1e-9);
        assertEquals(6.0 / 42.0 * 100.0, r.diferencaPercentual(), 1e-9);
    }

    @Test
    void deveTerDiferencaZeroQuandoOsDoisCoincidem() {
        double[][] m = {{0, 4, 7, 3}, {4, 0, 2, 6}, {7, 2, 0, 5}, {3, 6, 5, 0}};

        ComparacaoRotaResponse r = service.comparar(new CompararRotaRequest(m, 0));

        assertEquals(0.0, r.diferencaKm(), 1e-9);
        assertEquals(0.0, r.diferencaPercentual(), 1e-9);
    }

    @Test
    void deveRejeitarDepositoForaDaMatriz() {
        double[][] m = {{0, 1}, {1, 0}};
        assertThrows(IllegalArgumentException.class,
                () -> service.comparar(new CompararRotaRequest(m, 5)));
    }
}