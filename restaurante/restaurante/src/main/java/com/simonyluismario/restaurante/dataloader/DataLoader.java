package com.simonyluismario.restaurante.dataloader;

import com.simonyluismario.restaurante.services.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {
    private final UserService userService;

    public DataLoader(UserService userService){
        this.userService = userService;
    }

    @Override
    public void run(String... args) throws Exception {
        // Admin por defecto
        userService.createAdminIfNotExists("admin", "Admin123!", "admin@restaurant.com", "Administrador");
        System.out.println("Admin por defecto creado: usuario=admin, password=Admin123!");
    }
}
