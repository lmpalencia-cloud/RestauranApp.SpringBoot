package com.simonyluismario.restaurante.repositories;


import com.simonyluismario.restaurante.models.*;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
     // Último pedido no pagado de una mesa
    Optional<OrderEntity> findTopByTableAndPaidFalseOrderByCreatedAtDesc(TableEntity table);

}
