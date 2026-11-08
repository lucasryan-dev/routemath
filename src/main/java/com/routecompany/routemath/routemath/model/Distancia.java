package com.routecompany.routemath.routemath.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Distância em km de um local de origem até um local de destino (uma linha por sentido). */
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"origem_id", "destino_id"}))
@Getter
@Setter
@NoArgsConstructor
public class Distancia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "origem_id", nullable = false)
    private LocalEntrega origem;

    @ManyToOne(optional = false)
    @JoinColumn(name = "destino_id", nullable = false)
    private LocalEntrega destino;

    @Column(nullable = false)
    private Double distanciaKm;
}