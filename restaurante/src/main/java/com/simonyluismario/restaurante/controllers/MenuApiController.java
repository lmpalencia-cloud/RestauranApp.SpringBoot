package com.simonyluismario.restaurante.controllers;

import com.simonyluismario.restaurante.services.*;
import java.util.List;
import com.simonyluismario.restaurante.models.*;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
@RestController
@RequestMapping("/api")
public class MenuApiController {
  private final ProductService productService;
  public MenuApiController(ProductService productService){ this.productService = productService; }
  @GetMapping("/menu")
  public List<Product> menu(){ return productService.listAll(); }
}