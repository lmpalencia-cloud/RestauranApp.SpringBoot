package com.simonyluismario.restaurante.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.simonyluismario.restaurante.models.TableEntity;
import com.simonyluismario.restaurante.repositories.TableRepository;
@Component
public class DataInitializer  {

    private final TableRepository tableRepository;

    public DataInitializer(TableRepository tableRepository) {
        this.tableRepository = tableRepository;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void initializeTables() {
        long count = tableRepository.count();
        if (count == 0) {
            List<TableEntity> mesas = new ArrayList<>();

            for (int i = 1; i <= 20; i++) {
                TableEntity mesa = new TableEntity();
                mesa.setName("Mesa " + i);
                mesa.setCapacity(6);
                mesa.setOccupied(false);
                mesas.add(mesa);
            }

            tableRepository.saveAll(mesas);
            System.out.println("Mesas inicializadas correctamente después del arranque.");
        }
    }
}
