package com.simonyluismario.restaurante.repositories;


import com.simonyluismario.restaurante.models.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {}
