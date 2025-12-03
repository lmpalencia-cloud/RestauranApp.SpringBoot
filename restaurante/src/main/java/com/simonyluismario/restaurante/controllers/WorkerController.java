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

@PostMapping("/tables/{id}/clean")
public String setTableClean(@PathVariable Long id, @RequestParam boolean cleaned){

    TableEntity t = tableRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Mesa no encontrada"));

    t.setCleaned(cleaned);
    tableRepository.save(t);

    return "redirect:/worker/table/" + id;
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

@GetMapping("/order/pay/{id}")
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
    return "redirect:/worker/table/" + id;
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

    model.addAttribute("tables",
    tableRepository.findAll().stream()
        .filter(t -> t != null)
        .filter(t -> t.getName() != null && !t.getName().isBlank())
        .toList()
);

    return "worker/workspace";
}

@GetMapping("/table/{id}")
    public String viewTable(@PathVariable Long id, Model model) {

        TableEntity table = tableRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mesa no encontrada"));

        OrderEntity currentOrder = orderService.findOpenByTableId(id)
                .orElse(null);

        model.addAttribute("table", table);
        model.addAttribute("currentOrder", currentOrder);
        model.addAttribute("products", productService.listAll());

        return "worker/table_view";
}


}