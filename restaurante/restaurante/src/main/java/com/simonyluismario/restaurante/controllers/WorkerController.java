package com.simonyluismario.restaurante.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.simonyluismario.restaurante.models.*;
import com.simonyluismario.restaurante.services.*;

import jakarta.transaction.Transactional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.simonyluismario.restaurante.repositories.*;

import java.util.Optional; 
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    // ✅ Workspace
    @GetMapping("/workspace")
    public String workspace(Model model){
        model.addAttribute("tables", tableRepository.findAll());
        return "worker/workspace";
    }

    // ✅ Ver productos de la mesa
    @GetMapping("/mesa/{id}")
    public String verProductosMesa(@PathVariable Long id, Model model){
        TableEntity mesa = tableRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mesa no encontrada"));

        List<Product> products = productService.listAll();
        model.addAttribute("mesa", mesa);
        model.addAttribute("products", products);

        return "worker/table_products";
    }

    // ✅ Guardar pedido
    @PostMapping("/pedido/guardar")
    public String guardarPedido(
              @RequestParam Long mesaId,
        @RequestParam String pedidoJson,
        Principal principal) {

    TableEntity mesa = tableRepository.findById(mesaId)
            .orElseThrow(() -> new RuntimeException("Mesa no encontrada"));

    User worker = userRepository.findByUsername(principal.getName())
            .orElseThrow(() -> new RuntimeException("Trabajador no encontrado"));

    ObjectMapper mapper = new ObjectMapper();
    Map<String, Map<String,Object>> pedidoMap;
    try {
        pedidoMap = mapper.readValue(pedidoJson, new TypeReference<>() {});
    } catch (Exception e) {
        throw new RuntimeException("Error leyendo JSON del pedido");
    }

    // Obtener pedido activo o crear uno nuevo
    OrderEntity order = orderService.findLastNotPaidByTable(mesa)
            .orElseGet(() -> {
                OrderEntity o = new OrderEntity();
                o.setTable(mesa);
                o.setWorker(worker);
                o.setPaid(false);
                o.setCreatedAt(LocalDateTime.now());
                o.setItems(new ArrayList<>());
                return o;
            });

    double total = order.getTotal();
    List<OrderItemm> items = order.getItems();

    for (String idStr : pedidoMap.keySet()) {
        Long productId = Long.parseLong(idStr);
        Map<String,Object> data = pedidoMap.get(idStr);
        int qty = (int) data.get("cantidad");
        double price = Double.parseDouble(data.get("precio").toString());

        Product producto = productService.findById(productId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        // Ver si el producto ya está en el pedido
        Optional<OrderItemm> existing = items.stream()
                .filter(it -> it.getProduct().getId().equals(productId))
                .findFirst();

        if (existing.isPresent()) {
            // Sumar cantidad
            OrderItemm item = existing.get();
            item.setQuantity(item.getQuantity() + qty);
            item.setPrice(price); // opcional: actualizar precio por unidad
        } else {
            OrderItemm item = new OrderItemm();
            item.setOrder(order);
            item.setProduct(producto);
            item.setQuantity(qty);
            item.setPrice(price);
            items.add(item);
        }

        total += qty * price;
    }

    order.setItems(items);
    order.setTotal(total);
    orderService.save(order);

    // Asegurar que la mesa sigue ocupada
    mesa.setOccupied(true);
    tableRepository.save(mesa);

    return "redirect:/worker/mesa/" + mesa.getId();
}
    @PostMapping("/mesa/liberar/{id}")
public String liberarMesa(@PathVariable Long id, Model model){ 
       TableEntity mesa = tableRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Mesa no encontrada"));

    // Obtener el último pedido no pagado de esta mesa
    OrderEntity order = orderService.findLastNotPaidByTable(mesa)
            .orElseThrow(() -> new RuntimeException("No hay pedidos activos para esta mesa"));

    model.addAttribute("order", order);
    return "worker/order_view"; // Mostrar factura antes de liberar
}

  


 // Pagar pedido y liberar mesa
   @PostMapping("/mesa/pagar/{orderId}")
   @Transactional
public String pagarPedido(@PathVariable Long orderId) {
    // Obtener pedido
    OrderEntity order = orderService.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

    // Marcar pedido como pagado
    order.setPaid(true);
    orderService.save(order); // Guarda el pedido

    // Liberar la mesa asociada
    TableEntity mesa = order.getTable();
    mesa.setOccupied(false);
    tableRepository.save(mesa); // Guardar la mesa

    return "redirect:/worker/workspace";
}
  // ------------------------------
    // Mostrar factura antes de liberar mesa
    // ------------------------------
    @PostMapping("/mesa/factura/{id}")
    public String mostrarFactura(@PathVariable Long id, Model model) {
        TableEntity mesa = tableRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mesa no encontrada"));

        // MARCADO: obtener el último pedido no pagado
        OrderEntity order = orderService.findLastNotPaidByTable(mesa)
                .orElseThrow(() -> new RuntimeException("No hay pedidos pendientes en esta mesa"));

        model.addAttribute("order", order);
        return "worker/order_view";
    }

}
