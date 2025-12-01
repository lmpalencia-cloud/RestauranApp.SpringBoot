package com.simonyluismario.restaurante.controllers;

import com.simonyluismario.restaurante.models.*;
import com.simonyluismario.restaurante.services.*;
import com.simonyluismario.restaurante.repositories.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;

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

    @GetMapping("/orders/current/{tableId}")
@ResponseBody
public OrderEntity getCurrentOrder(@PathVariable Long tableId){
    return orderService.findOpenByTableId(tableId).orElse(null);
}

@PostMapping("/orders/current/{tableId}/ensure")
@ResponseBody
public OrderEntity ensureOpenOrder(@PathVariable Long tableId,
                                  org.springframework.security.core.Authentication auth){
    User worker = userRepository.findByUsername(auth.getName()).orElseThrow();
    return orderService.getOrCreateOpenOrder(tableId, worker);
}

@PostMapping("/orders/{orderId}/items")
@ResponseBody
public OrderItemm addItemAjax(@PathVariable Long orderId,
                              @RequestParam Long productId,
                              @RequestParam(defaultValue="1") Integer quantity){
    return orderService.addItemToOrder(orderId, productId, quantity);
}

@PostMapping("/orders/{orderId}/pay")
@ResponseBody
public void payAjax(@PathVariable Long orderId){
    orderService.payOrder(orderId);
}

@PutMapping("/tables/{id}/clean")
@ResponseBody
public TableEntity setTableClean(@PathVariable Long id, @RequestParam boolean cleaned){
    TableEntity t = tableRepository.findById(id).orElseThrow();
    t.setCleaned(cleaned);
    // si está limpia y sin orden abierta, estará verde (occupied=false)
    tableRepository.save(t);
    return t;
}

    @PostMapping("/order/create")
    public String createOrder(@RequestParam Long tableId, @RequestParam(required=false) Long[] productIds, @RequestParam(required=false) Integer[] quantities, org.springframework.security.core.Authentication auth){
        TableEntity table = tableRepository.findById(tableId).orElseThrow();
        User worker = userRepository.findByUsername(auth.getName()).orElseThrow();
        if (table.isOccupied()) {
       throw new RuntimeException("La mesa ya está ocupada.");
  }
        var items = new ArrayList<OrderItemm>();
        double total = 0;
        OrderEntity order = new OrderEntity();
        if (productIds != null) {
            for (int i = 0; i < productIds.length; i++) {
                var p = productService.findById(productIds[i]).orElseThrow();
                int qty = quantities != null && quantities.length > i ? quantities[i] : 1;
                OrderItemm it = new OrderItemm();
                it.setProduct(p);
                it.setQuantity(qty);
                it.setPrice(p.getPrice());
                total += p.getPrice() * qty;
                items.add(it);
                it.setOrder(order);

            }
        }

        

        //OrderEntity order = new OrderEntity();
        order.setTable(table);
        order.setWorker(worker);
        order.setItems(items);
        order.setTotal(total);
        order.setPaid(false);
        items.forEach(i -> i.setOrder(order)); 
        orderService.save(order);

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
@GetMapping("/workspace")
public String workspace(Model model, @RequestParam(required=false) String q){
    model.addAttribute("tables", tableRepository.findAll());
    if (q != null && !q.isBlank())
        model.addAttribute("products", productService.search(q));
    else
        model.addAttribute("products", productService.listAll());

    return "worker/workspace";
}

}