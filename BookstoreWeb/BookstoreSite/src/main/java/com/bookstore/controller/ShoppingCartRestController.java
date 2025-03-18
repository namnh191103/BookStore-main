package com.bookstore.controller;

import com.bookstore.exceptionhandler.ShoppingCartException;
import com.bookstore.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bookstore.config.Utility;
import com.bookstore.service.CustomerService;
import com.bookstore.entity.Customer;
import com.bookstore.exception.CustomerNotFoundException;

import jakarta.servlet.http.HttpServletRequest;

@RestController
public class ShoppingCartRestController {

    @Autowired
    private ShoppingCartService cartService;
    @Autowired
    private CustomerService customerService;

    @PostMapping("/cart/add/{productId}/{quantity}")
    public String addProductToCart(@PathVariable("productId") Integer productId,
            @PathVariable("quantity") Integer quantity, HttpServletRequest request) {
        try {
            Customer customer = getAuthenticatedCustomer(request);
            Integer updatedQuantity = cartService.addProduct(productId, quantity, customer);

            return updatedQuantity + " sản phẩm đã được thêm vào giỏ hàng"; 
        } catch (CustomerNotFoundException e) {
            return "Đăng nhập để thêm sản phẩm vào giỏ hàng";
        } catch (ShoppingCartException ex) {
            return ex.getMessage();
        }
    }

    private Customer getAuthenticatedCustomer(HttpServletRequest request) throws CustomerNotFoundException {
        String email = Utility.getEmailOfAuthenticatedCustomer(request);
        if (email == null) {
            throw new CustomerNotFoundException("Khách hàng chưa xác thực");
        }
        return customerService.getCustomerByEmail(email);
    }

    @PostMapping("/cart/update/{productId}/{quantity}")
    public String updateProductToCart(@PathVariable("productId") Integer productId,
            @PathVariable("quantity") Integer quantity, HttpServletRequest request) {
        try {
            Customer customer = getAuthenticatedCustomer(request);
            float subtotal = cartService.updateQuantity(productId, quantity, customer); 

            return String.valueOf(subtotal); 
        } catch (CustomerNotFoundException e) {
            return "Đăng nhập để thêm cập nhật số lượng sản phẩm";
        } 
    }

    @DeleteMapping("/cart/remove/{productId}") 
    public String removeProduct(@PathVariable("productId") Integer productId, HttpServletRequest request) {
        try {
            Customer customer = getAuthenticatedCustomer(request);
            cartService.removeProduct(productId, customer);

            return "Sản phẩm đã được xóa khỏi giỏ hàng";
        } catch (Exception e) {
            return "Khách hàng chưa đăng nhập"; 
        }
    }
}
