package com.routecompany.routemath.routemath.service;

import com.routecompany.routemath.routemath.algoritmo.TipoAlgoritmo;
import com.routecompany.routemath.routemath.dto.CalcularPorLocaisRequest;
import com.routecompany.routemath.routemath.dto.RotaPorLocaisResponse;
import com.routecompany.routemath.routemath.exception.RecursoNaoEncontradoException;
import com.routecompany.routemath.routemath.model.Distancia;
import com.routecompany.routemath.routemath.model.LocalEntrega;
import com.routecompany.routemath.routemath.model.TipoLocal;
import com.routecompany.routemath.routemath.repository.DistanciaRepository;
import com.routecompany.routemath.routemath.repository.LocalEntregaRepository;
import com.routecompany.routemath.routemath.repository.RotaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RotaLocaisServiceTest {

    private LocalEntregaRepository localRepository;
    private DistanciaRepository distanciaRepository;
    private RotaLocaisService service;

    @BeforeEach
    void preparar() {
        localRepository = mock(LocalEntregaRepository.class);
        distanciaRepository = mock(DistanciaRepository.class);
        RotaRepository rotaRepository = mock(RotaRepository.class);
        RotaService rotaService = new RotaService(rotaRepository);
        service = new RotaLocaisService(localRepository, distanciaRepository, rotaService, rotaRepository);
    }

    private LocalEntrega local(long id, String nome, TipoLocal tipo) {
        LocalEntrega l = new LocalEntrega();
        l.setId(id);
        l.setNome(nome);
        l.setTipo(tipo);
        return l;
    }

    private void par(List<Distancia> lista, LocalEntrega a, LocalEntrega b, double km) {
        lista.add(distancia(a, b, km));
        lista.add(distancia(b, a, km));
    }

    private Distancia distancia(LocalEntrega origem, LocalEntrega destino, double km) {
        Distancia d = new Distancia();
        d.setOrigem(origem);
        d.setDestino(destino);
        d.setDistanciaKm(km);
        return d;
    }

    // mesma matriz do exemplo: D=1, A=2, B=3, C=4  (rota ótima = 14 km)
    private final LocalEntrega d = local(1, "Depósito", TipoLocal.DEPOSITO);
    private final LocalEntrega a = local(2, "Cliente A", TipoLocal.CLIENTE);
    private final LocalEntrega b = local(3, "Cliente B", TipoLocal.CLIENTE);
    private final LocalEntrega c = local(4, "Cliente C", TipoLocal.CLIENTE);

    private List<Distancia> todasAsDistancias() {
        List<Distancia> lista = new ArrayList<>();
        par(lista, d, a, 4);
        par(lista, d, b, 7);
        par(lista, d, c, 3);
        par(lista, a, b, 2);
        par(lista, a, c, 6);
        par(lista, b, c, 5);
        return lista;
    }

    private void cadastrar(List<LocalEntrega> locais, List<Distancia> distancias) {
        when(localRepository.findAllById(any())).thenReturn(locais);
        when(distanciaRepository.findByOrigem_IdInAndDestino_IdIn(any(), any())).thenReturn(distancias);
    }

    @Test
    void deveCalcularARotaPelosLocaisCadastrados() {
        cadastrar(List.of(d, a, b, c), todasAsDistancias());

        RotaPorLocaisResponse r = service.calcular(
                new CalcularPorLocaisRequest(1L, List.of(2L, 3L, 4L), TipoAlgoritmo.HELD_KARP));

        assertEquals(14.0, r.distanciaTotal(), 1e-9);
        assertTrue(r.otima());
        assertEquals(5, r.rotaIds().size());
        assertEquals(1L, r.rotaIds().get(0));
        assertEquals(1L, r.rotaIds().get(4));
        assertEquals("Depósito", r.rotaNomes().get(0));
    }

    @Test
    void deveInformarDistanciasFaltando() {
        List<Distancia> semBC = new ArrayList<>(todasAsDistancias());
        semBC.removeIf(x -> x.getOrigem() == b && x.getDestino() == c);
        cadastrar(List.of(d, a, b, c), semBC);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                service.calcular(new CalcularPorLocaisRequest(1L, List.of(2L, 3L, 4L), TipoAlgoritmo.HELD_KARP)));

        assertTrue(ex.getMessage().contains("Faltam distâncias"));
        assertTrue(ex.getMessage().contains("Cliente B -> Cliente C"));
    }

    @Test
    void deveRejeitarDepositoQueNaoEDeposito() {
        cadastrar(List.of(d, a, b, c), todasAsDistancias());

        assertThrows(IllegalArgumentException.class, () ->
                service.calcular(new CalcularPorLocaisRequest(2L, List.of(1L, 3L), TipoAlgoritmo.HELD_KARP)));
    }

    @Test
    void deveRejeitarLocalInexistente() {
        cadastrar(List.of(d, a, b), todasAsDistancias());   // o local 4 não existe

        assertThrows(RecursoNaoEncontradoException.class, () ->
                service.calcular(new CalcularPorLocaisRequest(1L, List.of(2L, 3L, 4L), TipoAlgoritmo.HELD_KARP)));
    }

    @Test
    void deveRejeitarClienteRepetido() {
        cadastrar(List.of(d, a, b, c), todasAsDistancias());

        assertThrows(IllegalArgumentException.class, () ->
                service.calcular(new CalcularPorLocaisRequest(1L, List.of(2L, 2L, 3L), TipoAlgoritmo.HELD_KARP)));
    }
}
