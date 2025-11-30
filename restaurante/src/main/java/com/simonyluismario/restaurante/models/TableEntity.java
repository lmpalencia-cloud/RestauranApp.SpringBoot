package com.simonyluismario.restaurante.models;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tables_rest")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class TableEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name; // e.g., Mesa 1
    private int capacity;
    private boolean occupied = false;
}
