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
    private final ProductRepository productRepo;
    private final TableRepository tableRepo;

    public OrderService(OrderRepository repo, ProductRepository productRepo, TableRepository tableRepo){
        this.repo = repo; this.productRepo = productRepo; this.tableRepo = tableRepo;
    }

    public Optional<OrderEntity> findById(Long id){ return repo.findById(id); }
    public OrderEntity save(OrderEntity o){ return repo.save(o); }

    public Optional<OrderEntity> findOpenByTableId(Long tableId){
        return repo.findOpenByTableId(tableId);
    }

    public OrderEntity getOrCreateOpenOrder(Long tableId, User worker){
        var opt = findOpenByTableId(tableId);
        if(opt.isPresent()) return opt.get();
        TableEntity table = tableRepo.findById(tableId).orElseThrow();
        OrderEntity order = new OrderEntity();
        order.setTable(table);
        order.setWorker(worker);
        order.setItems(new ArrayList<>());
        order.setPaid(false);
        order.setTotal(0);
        // mesa ocupada y no limpia
        table.setOccupied(true);
        table.setCleaned(false);
        tableRepo.save(table);
        return repo.save(order);
    }

    public OrderItemm addItemToOrder(Long orderId, Long productId, int quantity){
        OrderEntity order = repo.findById(orderId).orElseThrow();
        Product p = productRepo.findById(productId).orElseThrow();
        OrderItemm it = new OrderItemm();
        it.setOrder(order);
        it.setProduct(p);
        it.setQuantity(Math.max(1, quantity));
        it.setPrice(p.getPrice());
        order.getItems().add(it);
        recalcTotal(order);
        repo.save(order); // cascada guarda item
        return it;
    }

    public void recalcTotal(OrderEntity order){
        double total = 0;
        if(order.getItems()!=null){
            for(var it: order.getItems()){
                total += it.getPrice() * it.getQuantity();
            }
        }
        order.setTotal(total);
    }

    public void payOrder(Long orderId){
        OrderEntity o = repo.findById(orderId).orElseThrow();
        o.setPaid(true);
        repo.save(o);
        TableEntity t = o.getTable();
        t.setOccupied(false);    // deja de estar ocupada
        t.setCleaned(false);     // sigue sin limpiar (gris)
        tableRepo.save(t);
    }
}