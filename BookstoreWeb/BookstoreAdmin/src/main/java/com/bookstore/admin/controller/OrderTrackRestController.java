package com.bookstore.admin.controller;

import java.util.Map;

import com.bookstore.admin.service.OrderTrackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bookstore.entity.order.OrderStatus;

@RestController
@RequestMapping("/order_tracks")
public class OrderTrackRestController {

    @Autowired
    private OrderTrackService orderTrackService;

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateOrderTrackStatus(@PathVariable int id, @RequestBody Map<String, String> request) {
        String newStatus = request.get("status");
        try {
            orderTrackService.updateOrderTrackStatus(id, OrderStatus.valueOf(newStatus));
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid status");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
        }
    }
}
