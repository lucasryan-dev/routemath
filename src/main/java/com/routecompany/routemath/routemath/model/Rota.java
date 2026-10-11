package com.routecompany.routemath.routemath.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/** Histórico de um cálculo de rota. */
@Entity
@Getter
@Setter
@NoArgsConstructor
public class Rota {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String algoritmo;

    // sequência de índices separados por vírgula, ex.: "0,3,2,1,0"
    private String sequencia;

    private Double distanciaTotal;

    private Boolean otima;

    private Double tempoCalculoMs;

    private LocalDateTime dataCalculo;
}
