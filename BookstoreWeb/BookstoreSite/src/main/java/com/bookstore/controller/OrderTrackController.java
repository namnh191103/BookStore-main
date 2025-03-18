package com.bookstore.controller;

import java.util.Map;

import com.bookstore.service.OrderTrackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bookstore.entity.order.OrderStatus;

@RestController
@RequestMapping("/order_tracks")
public class OrderTrackController {

    @Autowired
    private OrderTrackService orderTrackService;

    @PutMapping("/{id}/status")
    public ResponseEntity<Void> updateOrderTrackStatus(@PathVariable int id, @RequestBody Map<String, String> statusUpdate) {
        String statusStr = statusUpdate.get("status");
        try {
            OrderStatus newStatus = OrderStatus.valueOf(statusStr);
            orderTrackService.updateOrderTrackStatus(id, newStatus);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
