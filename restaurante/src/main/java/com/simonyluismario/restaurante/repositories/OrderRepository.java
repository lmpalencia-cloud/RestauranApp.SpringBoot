package com.simonyluismario.restaurante.repositories;


import com.simonyluismario.restaurante.models.*;

import java.util.*;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    @Query("SELECT o FROM OrderEntity o WHERE o.table.id = :tableId AND o.paid = false ORDER BY o.createdAt DESC")
    List<OrderEntity> findOpenOrdersByTableId(@Param("tableId") Long tableId);

}


