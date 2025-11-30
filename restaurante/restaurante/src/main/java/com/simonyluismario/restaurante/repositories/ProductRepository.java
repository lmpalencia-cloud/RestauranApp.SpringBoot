package com.simonyluismario.restaurante.repositories;

import com.simonyluismario.restaurante.models.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
  List<Product> findByNameContainingIgnoreCase(String q);
}