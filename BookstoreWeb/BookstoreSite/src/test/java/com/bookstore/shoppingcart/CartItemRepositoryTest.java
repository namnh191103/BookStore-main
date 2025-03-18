package com.bookstore.shoppingcart;

import java.util.List;

import com.bookstore.repository.CartItemRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import com.bookstore.entity.CartItem;
import com.bookstore.entity.Customer;
import com.bookstore.entity.product.Product;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class CartItemRepositoryTest {

    @Autowired private CartItemRepository repository;
    @Autowired private TestEntityManager entityManager; 

    @Test
    public void testSaveItem() {
        Integer customerId = 1009; 
        Integer productId = 1; 

        Customer customer = entityManager.find(Customer.class, customerId);
        Product product = entityManager.find(Product.class, productId);

        CartItem newItem = new CartItem();
        newItem.setCustomer(customer);
        newItem.setProduct(product);
        newItem.setQuantity(1);

        CartItem savedItem = repository.save(newItem);

        Assertions.assertThat(savedItem.getId()).isGreaterThan(0);
    }

    @Test
    public void testSave2Item() {
        Integer customerId = 1014; 
        Integer productId = 2; 

        Customer customer = entityManager.find(Customer.class, customerId);
        Product product = entityManager.find(Product.class, productId);

        CartItem item1 = new CartItem();
        item1.setCustomer(customer);
        item1.setProduct(product);
        item1.setQuantity(2);

        CartItem item2 = new CartItem(); 
        item2.setCustomer(new Customer(customerId));
        item2.setProduct(new Product(4));
        item2.setQuantity(3);

        Iterable<CartItem> inIterable = repository.saveAll(List.of(item1, item2));

        Assertions.assertThat(inIterable).size().isGreaterThan(0);
    }

    @Test
    public void testFindByCustomer() {
        Integer customerId = 1014; 

        List<CartItem> list = repository.findByCustomer(new Customer(customerId));
        list.forEach(System.out::println);
        Assertions.assertThat(list.size()).isEqualTo(2);
    }

    @Test
    public void testFindByCustomerAndProduct() {
        Integer customerId = 1014; 
        Integer productId = 4; 

        CartItem item = repository.findByCustomerAndProduct(new Customer(customerId), new Product(productId));

        Assertions.assertThat(item).isNotNull(); 
        
        System.out.println(item);
    }

    @Test
    public void testUpdateQuantity() {
        Integer customerId = 1014; 
        Integer productId = 4; 
        Integer quantity = 13; 

        repository.updateQuantity(quantity, customerId, productId);

        CartItem item = repository.findByCustomerAndProduct(new Customer(customerId), new Product(productId)); 

        Assertions.assertThat(item.getQuantity()).isEqualTo(13); 
    }

    @Test
    public void testDeleteByCustomerAndProduct() {
        Integer customerId = 1014; 
        Integer productId = 4; 

        repository.deleteByCustomerAndProduct(customerId, productId);

        CartItem item = repository.findByCustomerAndProduct(new Customer(customerId), new Product(productId));

        Assertions.assertThat(item).isNull();

    }
}
