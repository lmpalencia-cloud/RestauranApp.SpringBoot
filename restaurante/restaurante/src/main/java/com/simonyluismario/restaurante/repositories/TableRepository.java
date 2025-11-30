package com.simonyluismario.restaurante.repositories;
import com.simonyluismario.restaurante.models.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TableRepository extends JpaRepository<TableEntity, Long> {}