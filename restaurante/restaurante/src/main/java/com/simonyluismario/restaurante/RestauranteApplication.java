package com.simonyluismario.restaurante;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.simonyluismario.restaurante.models.TableEntity;
import com.simonyluismario.restaurante.repositories.TableRepository;

@SpringBootApplication
public class RestauranteApplication {

	public static void main(String[] args) {
		SpringApplication.run(RestauranteApplication.class, args);
	
	}
	

}
