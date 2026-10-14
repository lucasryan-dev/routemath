package com.routecompany.routemath.routemath.repository;

import com.routecompany.routemath.routemath.model.Rota;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RotaRepository extends JpaRepository<Rota, Long> {

    List<Rota> findAllByOrderByDataCalculoDesc();
}
