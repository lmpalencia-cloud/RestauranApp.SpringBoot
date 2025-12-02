package com.simonyluismario.restaurante.controllers;

import com.simonyluismario.restaurante.models.*;
import com.simonyluismario.restaurante.services.*;
import com.simonyluismario.restaurante.repositories.*;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/worker")
public class WorkerController {
    private final TableRepository tableRepository;
    private final ProductService productService;
    private final OrderService orderService;
    private final UserRepository userRepository;

    public WorkerController(TableRepository tableRepository, ProductService productService, OrderService orderService, UserRepository userRepository){
        this.tableRepository = tableRepository;
        this.productService = productService;
        this.orderService = orderService;
        this.userRepository = userRepository;
    }

    @GetMapping("/workspace")
    public String workspace(Model model){
        model.addAttribute("tables", tableRepository.findAll());
        model.addAttribute("products", productService.listAll());
        return "worker/workspace";
    }

 @PostMapping("/order/create")
public String createOrder(
        @RequestParam Long tableId,
        @RequestParam(required = false) Long[] productIds,
        @RequestParam(required = false) Integer[] quantities,
        Authentication auth
){
    // Buscar mesa
    TableEntity table = tableRepository.findById(tableId)
            .orElseThrow(() -> new RuntimeException("Mesa no encontrada"));

    // Buscar trabajador logueado
    String username = auth != null ? auth.getName() : null;
    if (username == null) {
        throw new RuntimeException("Usuario no autenticado");
    }
    User worker = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("Trabajador no encontrado"));

    // Crear lista de ítems
    List<OrderItemm> items = new ArrayList<>();
    double total = 0;

    if (productIds != null) {
        for (int i = 0; i < productIds.length; i++) {

                Product p = productService.findById(productIds[i])
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

            int qty = (quantities != null && quantities.length > i)
                    ? quantities[i]
                    : 1;

            OrderItemm item = new OrderItemm();
            item.setProduct(p);
            item.setQuantity(qty);
            item.setPrice(p.getPrice());

            total += p.getPrice() * qty;

            items.add(item);
        }
    }

    // Crear la orden
    OrderEntity order = new OrderEntity();
    order.setTable(table);
    order.setWorker(worker);
    order.setItems(items);
    order.setTotal(total);
    order.setPaid(false);
    order.setCreatedAt(LocalDateTime.now());

    // AHORA sí puedes asignar la orden a cada ítem
    for (OrderItemm it : items) {
        it.setOrder(order);
    }

    // Guardar orden (cascade guarda los items también)
    orderService.save(order);

    // Marcar mesa ocupada
    table.setOccupied(true);
    tableRepository.save(table);

    return "redirect:/worker/workspace";
}

    @PostMapping("/order/pay/{id}")
    public String payOrder(@PathVariable Long id){
        var opt = orderService.findById(id);
        if (opt.isPresent()){
            var o = opt.get();
            o.setPaid(true);
            orderService.save(o);
            TableEntity t = o.getTable();
            t.setOccupied(false);
            tableRepository.save(t);
        }
        return "redirect:/worker/workspace";
    }
    @GetMapping("/order/view/{id}")
public String viewOrder(@PathVariable Long id, Model model){
    var order = orderService.findById(id)
            .orElseThrow(() -> new RuntimeException("Orden no encontrada"));
    model.addAttribute("order", order);
    return "worker/order_view";
}

@GetMapping("/mesa/{numMesa}")
public String verProductosMesa(@PathVariable int numMesa, Model model) {

    // Obtener todos los productos desde el servicio
    List<Product> products = productService.listAll();

    model.addAttribute("mesaId", numMesa);
    model.addAttribute("products", products);

    return "worker/table_products";
}

}