package com.bookstore.service;

import java.util.List;

import com.bookstore.exceptionhandler.ShoppingCartException;
import com.bookstore.repository.CartItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bookstore.entity.CartItem;
import com.bookstore.entity.Customer;
import com.bookstore.entity.product.Product;
import com.bookstore.repository.ProductRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class ShoppingCartService {

    @Autowired private CartItemRepository cartItemRepository;
    @Autowired private ProductRepository productRepository; 

    public Integer addProduct(Integer productId, Integer quantity, Customer customer) throws ShoppingCartException {
        Integer updateQuantity = quantity; 
        Product product = new Product(productId); 

        CartItem cartItem = cartItemRepository.findByCustomerAndProduct(customer, product); 

        if (cartItem != null) {
            updateQuantity = cartItem.getQuantity() + quantity; 
            if (updateQuantity > 5) {
                throw new ShoppingCartException("Không thể thêm hơn " + quantity + " sản phảm");
            }
        } else {
            cartItem = new CartItem(); 
            cartItem.setCustomer(customer);
            cartItem.setProduct(product);
        }

        cartItem.setQuantity(updateQuantity);
        cartItemRepository.save(cartItem);
        return updateQuantity;
    }

    public List<CartItem> listByCartItems(Customer customer) {
        return cartItemRepository.findByCustomer(customer); 
    }


    public float updateQuantity(Integer productId, Integer quantity, Customer customer) {
        cartItemRepository.updateQuantity(quantity, customer.getId(), productId);
        Product product = productRepository.findById(productId).get(); 
        float subtotal = product.getDiscountPrice() * quantity; 
        return  subtotal; 
    }

    public void removeProduct(Integer productId, Customer customer) {
        cartItemRepository.deleteByCustomerAndProduct(customer.getId(), productId);
    }

    public void deleteByCustomer(Customer customer) {
		cartItemRepository.deleteByCustomer(customer.getId());
	}

}
