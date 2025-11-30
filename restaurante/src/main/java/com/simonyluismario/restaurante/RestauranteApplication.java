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
	@Bean
public CommandLineRunner init(TableRepository repo){
    return args -> {
        if(repo.count() == 0){
            for(int i=1; i<=20; i++){
                TableEntity t = new TableEntity();
                t.setName("Mesa " + i);
                t.setOccupied(false);
                repo.save(t);
            }
        }
    };
}


}
