package com.bookstore.admin.order;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.bookstore.admin.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.test.annotation.Rollback;

import com.bookstore.entity.Customer;
import com.bookstore.entity.order.Order;
import com.bookstore.entity.order.OrderDetail;
import com.bookstore.entity.order.OrderStatus;
import com.bookstore.entity.order.OrderTrack;
import com.bookstore.entity.order.PaymentMethod;
import com.bookstore.entity.product.Product;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class OrderRepositoryTest {

    @Autowired private OrderRepository repo;
	@Autowired private TestEntityManager entityManager;

    @Test
	public void testCreateNewOrderWithSingleProduct() {
		Customer customer = entityManager.find(Customer.class, 1015);
		Product product = entityManager.find(Product.class, 29);
		
		Order mainOrder = new Order();
		mainOrder.setOrderTime(new Date());
		mainOrder.setCustomer(customer);
		mainOrder.copyAddressFromCustomer();
		
		mainOrder.setShippingCost(10);
		mainOrder.setProductCost(product.getCost());
		mainOrder.setTax(0);
		mainOrder.setSubtotal(product.getPrice());
		mainOrder.setTotal(product.getPrice() + 10);
		
		mainOrder.setPaymentMethod(PaymentMethod.COD);
		mainOrder.setStatus(OrderStatus.NEW);
		mainOrder.setDeliverDate(new Date());
		mainOrder.setDeliverDays(1);
		
		OrderDetail orderDetail = new OrderDetail();
		orderDetail.setProduct(product);
		orderDetail.setOrder(mainOrder);
		orderDetail.setProductCost(product.getCost());
		orderDetail.setShippingCost(10);
		orderDetail.setQuantity(1);
		orderDetail.setSubtotal(product.getPrice());
		orderDetail.setUnitPrice(product.getPrice());
		
		mainOrder.getOrderDetails().add(orderDetail);
		
		Order savedOrder = repo.save(mainOrder);
		
		assertThat(savedOrder.getId()).isGreaterThan(0);			
	}

    @Test
    public void testCreateNewOrderWithMultipleProducts() {
		Customer customer = entityManager.find(Customer.class, 1016);
		Product product1 = entityManager.find(Product.class, 25);
		Product product2 = entityManager.find(Product.class, 30);
		
		Order mainOrder = new Order();
		mainOrder.setOrderTime(new Date());
		mainOrder.setCustomer(customer);
		mainOrder.copyAddressFromCustomer();
		
		OrderDetail orderDetail1 = new OrderDetail();
		orderDetail1.setProduct(product1);
		orderDetail1.setOrder(mainOrder);
		orderDetail1.setProductCost(product1.getCost());
		orderDetail1.setShippingCost(10);
		orderDetail1.setQuantity(1);
		orderDetail1.setSubtotal(product1.getPrice());
		orderDetail1.setUnitPrice(product1.getPrice());
		
		OrderDetail orderDetail2 = new OrderDetail();
		orderDetail2.setProduct(product2);
		orderDetail2.setOrder(mainOrder);
		orderDetail2.setProductCost(product2.getCost());
		orderDetail2.setShippingCost(20);
		orderDetail2.setQuantity(2);
		orderDetail2.setSubtotal(product2.getPrice() * 2);
		orderDetail2.setUnitPrice(product2.getPrice());
		
		mainOrder.getOrderDetails().add(orderDetail1);
		mainOrder.getOrderDetails().add(orderDetail2);
		
		mainOrder.setShippingCost(30);
		mainOrder.setProductCost(product1.getCost() + product2.getCost());
		mainOrder.setTax(0);
		float subtotal = product1.getPrice() + product2.getPrice() * 2;
		mainOrder.setSubtotal(subtotal);
		mainOrder.setTotal(subtotal + 30);
		
		mainOrder.setPaymentMethod(PaymentMethod.COD);
		mainOrder.setStatus(OrderStatus.PROCESSING);
		mainOrder.setDeliverDate(new Date());
		mainOrder.setDeliverDays(3);
		
		Order savedOrder = repo.save(mainOrder);		
		assertThat(savedOrder.getId()).isGreaterThan(0);		
	}

    @Test
	public void testListOrders() {
		Iterable<Order> orders = repo.findAll();
		
		assertThat(orders).hasSizeGreaterThan(0);
		
		orders.forEach(System.out::println);
	}

    @Test
	public void testUpdateOrder() {
		Integer orderId = 2;
		Order order = repo.findById(orderId).get();
		
		order.setStatus(OrderStatus.SHIPPING);
		order.setOrderTime(new Date());
		order.setDeliverDays(2);
		
		Order updatedOrder = repo.save(order);
		
		assertThat(updatedOrder.getStatus()).isEqualTo(OrderStatus.SHIPPING);
	}

    @Test
	public void testGetOrder() {
		Integer orderId = 2;
		Order order = repo.findById(orderId).get();
		
		assertThat(order).isNotNull();
		System.out.println(order);
	}

    @Test
	public void testDeleteOrder() {
		Integer orderId = 2;
		repo.deleteById(orderId);
		
		Optional<Order> result = repo.findById(orderId);
		assertThat(result).isNotPresent();
	}

	@Test
	public void testUpdateOrderTracks() {
		Integer orderId = 4;
		Order order = repo.findById(orderId).get();
		
		OrderTrack newTrack = new OrderTrack();
		newTrack.setOrder(order);
		newTrack.setUpdatedTime(new Date());
		newTrack.setStatus(OrderStatus.NEW);
		newTrack.setNotes(OrderStatus.NEW.defaultDescription());

		OrderTrack processingTrack = new OrderTrack();
		processingTrack.setOrder(order);
		processingTrack.setUpdatedTime(new Date());
		processingTrack.setStatus(OrderStatus.PROCESSING);
		processingTrack.setNotes(OrderStatus.PROCESSING.defaultDescription());
		
		List<OrderTrack> orderTracks = order.getOrderTracks();
		orderTracks.add(newTrack);
		orderTracks.add(processingTrack);
		
		Order updatedOrder = repo.save(order);
		
		assertThat(updatedOrder.getOrderTracks()).hasSizeGreaterThan(1);
	}


	@Test
	public void testFindByOrderTimeBetween() throws ParseException {
		SimpleDateFormat internationalDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date startTime = internationalDateFormat.parse("2024-07-25");
		Date endTime = internationalDateFormat.parse("2024-07-30");
		List<Order> listOrders = repo.findByOrderTimeBetween(startTime, endTime);
		
		assertThat(listOrders.size()).isGreaterThan(0);
		
		for (Order order : listOrders) {
			System.out.printf("%s | %s | %.2f | %.2f | %.2f \n", 
					order.getId(), order.getOrderTime(), order.getProductCost(), 
					order.getSubtotal(), order.getTotal());
		}
	}
}
