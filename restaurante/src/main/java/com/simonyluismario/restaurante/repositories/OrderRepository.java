package com.simonyluismario.restaurante.repositories;


import com.simonyluismario.restaurante.models.*;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
        @Query("SELECT o.worker.username, SUM(o.total) as totalVendio " +
           "FROM OrderEntity o " +
           "WHERE o.paid = true " +
           "GROUP BY o.worker.username " +
           "ORDER BY totalVendio DESC")
        List<Object[]> findTopSellers();
}

