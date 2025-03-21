package com.bookstore.order;

import static org.assertj.core.api.Assertions.assertThat;

import com.bookstore.repository.OrderDetailRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.bookstore.entity.order.OrderStatus;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class OrderDetailRepositoryTests {

	@Autowired private OrderDetailRepository repo;
	
	@Test
	public void testCountByProductAndCustomerAndOrderStatus() {
		Integer productId = 24;
		Integer customerId = 2015;
		
		Long count = repo.countByProductAndCustomerAndOrderStatus(productId, customerId, OrderStatus.DELIVERED);
		assertThat(count).isGreaterThan(0);
	}
	
}
