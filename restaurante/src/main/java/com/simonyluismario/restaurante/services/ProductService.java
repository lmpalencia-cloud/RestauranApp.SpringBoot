package com.simonyluismario.restaurante.services;


import com.simonyluismario.restaurante.models.*;
import com.simonyluismario.restaurante.repositories.*;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProductService {
    private final ProductRepository repo;
    public ProductService(ProductRepository repo){ this.repo = repo; }

    public Product save(Product p){ return repo.save(p); }
    public List<Product> listAll(){ return repo.findAll(); }
    public Optional<Product> findById(Long id){ return repo.findById(id); }
    public void delete(Long id){ repo.deleteById(id); }
    public List<Product> search(String q){ return repo.findByNameContainingIgnoreCase(q); }
    
}
