package com.simonyluismario.restaurante.controllers;

import com.simonyluismario.restaurante.models.*;
import com.simonyluismario.restaurante.services.*;
import com.simonyluismario.restaurante.repositories.*;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final ProductService productService;
    private final UserService userService;
    private final UserRepository userRepository;

    public AdminController(ProductService productService, UserService userService, UserRepository userRepository){
        this.productService = productService;
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @GetMapping("/menu")
    public String menu(Model model, @RequestParam(value="q", required=false) String q){
        if (q != null && !q.isBlank()) model.addAttribute("products", productService.search(q));
        else model.addAttribute("products", productService.listAll());
        return "admin/menu";
    }

    @GetMapping("/product/new")
    public String newProductForm(Model model){
        model.addAttribute("product", new Product());
        return "admin/product_form";
    }

    @PostMapping("/product/save")
    public String saveProduct(@ModelAttribute Product product){
        productService.save(product);
        return "redirect:/admin/menu";
    }

    @GetMapping("/product/edit/{id}")
    public String editProduct(@PathVariable Long id, Model model){
        productService.findById(id).ifPresent(p -> model.addAttribute("product", p));
        return "admin/product_form";
    }

    @GetMapping("/product/delete/{id}")
    public String deleteProduct(@PathVariable Long id){
        productService.delete(id);
        return "redirect:/admin/menu";
    }

    // crear empleado desde admin
    @GetMapping("/employees")
    public String employees(Model model){
    model.addAttribute("workers", userRepository.findAll()
            .stream()
            .filter(u -> u.getRole().name().equals("WORKER"))
            .toList());
    return "admin/employees";
}

    @PostMapping("/employees/create")
    public String createEmployee(@RequestParam String username, @RequestParam String password, @RequestParam String email, @RequestParam String fullName){
        userService.registerWorker(username, password, email, fullName);
        return "redirect:/admin/employees";
    }
  @DeleteMapping("/employees/{id}")
@ResponseBody
public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {

    boolean deleted = userService.deleteUser(id);

    if (deleted) {
        return ResponseEntity.ok().build();
    } else {
        return ResponseEntity.notFound().build();
    }
}
    
}
