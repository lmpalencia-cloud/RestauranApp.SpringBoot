package com.simonyluismario.restaurante.services;

import com.simonyluismario.restaurante.repositories.*;
import com.simonyluismario.restaurante.models.*;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import java.util.*;

@Service
@Transactional
public class OrderService {
    
    private final OrderRepository repo;
    public OrderService(OrderRepository repo){ this.repo = repo; }

    public OrderEntity save(OrderEntity o){ return repo.save(o); }
    public Optional<OrderEntity> findById(Long id){ return repo.findById(id); }
public Optional<OrderEntity> findLastNotPaidByTable(TableEntity mesa){
    return repo.findTopByTableAndPaidFalseOrderByCreatedAtDesc(mesa);
}
}