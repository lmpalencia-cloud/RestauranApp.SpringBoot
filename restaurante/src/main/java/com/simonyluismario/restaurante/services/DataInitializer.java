package com.simonyluismario.restaurante.services;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.simonyluismario.restaurante.models.TableEntity;
import com.simonyluismario.restaurante.repositories.TableRepository;
@Component
public class DataInitializer implements CommandLineRunner {

    private final TableRepository tableRepository;

    public DataInitializer(TableRepository tableRepository) {
        this.tableRepository = tableRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (tableRepository.count() == 0) {
            for (int i = 1; i <= 20; i++) {
                TableEntity mesa = new TableEntity();
                mesa.setName("Mesa " + i);
               // mesa.setNumber(i);
                mesa.setCapacity(6); // por ejemplo, 4 personas por mesa
                mesa.setOccupied(false);
                tableRepository.save(mesa);
            }
            System.out.println("Mesas inicializadas correctamente.");
        }
    }
}
