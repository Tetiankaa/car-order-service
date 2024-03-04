package com.example.orderservice.controller;

import com.example.orderservice.dto.OrderDTO;
import com.example.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/order")
public class OrderController {
    private final OrderService orderService;


    @PostMapping
    public ResponseEntity<OrderDTO> createOrder(@RequestBody OrderDTO order){
        return ResponseEntity.ok(orderService.create(order));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDTO> getOrder(@PathVariable Integer id){
        return ResponseEntity.ok(orderService.getOrderDetails(id));
    }
}
