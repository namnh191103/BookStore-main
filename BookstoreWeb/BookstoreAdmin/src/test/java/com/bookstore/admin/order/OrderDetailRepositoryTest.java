package com.bookstore.admin.order;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.bookstore.admin.repository.OrderDetailRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import static org.assertj.core.api.Assertions.assertThat;


import com.bookstore.entity.order.OrderDetail;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class OrderDetailRepositoryTest {

    @Autowired private OrderDetailRepository repo;
	
	@Test
	public void testFindWithCategoryAndTimeBetween() throws ParseException {
		DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
		Date startTime = dateFormatter.parse("2024-07-25");
		Date endTime = dateFormatter.parse("2024-07-27");
		
		List<OrderDetail> listOrderDetails = repo.findWithCategoryAndTimeBetween(startTime, endTime);
		
		assertThat(listOrderDetails.size()).isGreaterThan(0);
		
		for (OrderDetail detail : listOrderDetails) {
			System.out.printf("%-30s | %d | %10.2f| %10.2f | %10.2f \n", 
					detail.getProduct().getCategory().getName(),
					detail.getQuantity(), detail.getProductCost(),
					detail.getShippingCost(), detail.getSubtotal());
		}
	}

    @Test
	public void testFindWithProductAndTimeBetween() throws ParseException {
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
		Date startTime = dateFormatter.parse("2024-07-25");
		Date endTime = dateFormatter.parse("2024-07-27");
		
		List<OrderDetail> listOrderDetails = repo.findWithProductAndTimeBetween(startTime, endTime);
		
		assertThat(listOrderDetails.size()).isGreaterThan(0);
		
		for (OrderDetail detail : listOrderDetails) {
			System.out.printf("%-70s | %d | %10.2f| %10.2f | %10.2f \n", 
					detail.getProduct().getShortName(),
					detail.getQuantity(), detail.getProductCost(),
					detail.getShippingCost(), detail.getSubtotal());
		}
	}	
}
