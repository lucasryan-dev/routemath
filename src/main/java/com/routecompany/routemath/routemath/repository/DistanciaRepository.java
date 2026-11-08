package com.routecompany.routemath.routemath.repository;

import com.routecompany.routemath.routemath.model.Distancia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface DistanciaRepository extends JpaRepository<Distancia, Long> {

    Optional<Distancia> findByOrigem_IdAndDestino_Id(Long origemId, Long destinoId);

    /** Todas as distâncias cadastradas entre os locais informados. */
    List<Distancia> findByOrigem_IdInAndDestino_IdIn(Collection<Long> origemIds, Collection<Long> destinoIds);
}
