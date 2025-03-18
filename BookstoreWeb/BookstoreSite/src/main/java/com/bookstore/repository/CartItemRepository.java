package com.bookstore.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.bookstore.entity.CartItem;
import com.bookstore.entity.Customer;
import com.bookstore.entity.product.Product;

public interface CartItemRepository extends JpaRepository<CartItem, Integer> {

    public List<CartItem> findByCustomer (Customer customer);

    public CartItem findByCustomerAndProduct(Customer customer, Product product);
    

    @Modifying
    @Query("UPDATE CartItem c SET c.quantity = ?1 WHERE c.customer.id = ?2 AND c.product.id = ?3")
    public void updateQuantity(Integer quantity, Integer customderId, Integer productId);

    @Modifying
    @Query("DELETE FROM CartItem c WHERE c.customer.id = ?1 AND c.product.id = ?2")
    public void deleteByCustomerAndProduct(Integer customer, Integer product);

    @Modifying
	@Query("DELETE CartItem c WHERE c.customer.id = ?1")
	public void deleteByCustomer(Integer customerId);
}
