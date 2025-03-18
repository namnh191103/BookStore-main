package com.bookstore.service;

import java.util.Date;
import java.util.List;
import java.util.Set;

import com.bookstore.model.OrderReturnRequest;
import com.bookstore.repository.OrderRepository;
import com.bookstore.repository.OrderTrackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.bookstore.checkout.CheckoutInfo;
import com.bookstore.entity.Address;
import com.bookstore.entity.CartItem;
import com.bookstore.entity.Customer;
import com.bookstore.entity.order.Order;
import com.bookstore.entity.order.OrderDetail;
import com.bookstore.entity.order.OrderStatus;
import com.bookstore.entity.order.PaymentMethod;
import com.bookstore.entity.product.Product;
import com.bookstore.exception.OrderNotFoundException;
import com.bookstore.repository.ProductRepository;
import com.bookstore.entity.order.OrderTrack;

@Service
public class OrderService {
	public static final int ORDERS_PER_PAGE = 5;

    @Autowired private OrderRepository repo;
    @Autowired private OrderTrackRepository orderTrackRepo;
    @Autowired private ProductRepository productRepository; 
    
    public Order createOrder(Customer customer, Address address, List<CartItem> cartItems,
            PaymentMethod paymentMethod, CheckoutInfo checkoutInfo) {
        Order newOrder = new Order();
        newOrder.setOrderTime(new Date());
        newOrder.setStatus(OrderStatus.NEW);
        newOrder.setCustomer(customer);
        newOrder.setProductCost(checkoutInfo.getProductCost());
        newOrder.setSubtotal(checkoutInfo.getProductTotal());
        newOrder.setShippingCost(checkoutInfo.getShippingCostTotal());
        newOrder.setTax(0.0f);
        newOrder.setTotal(checkoutInfo.getPaymentTotal());
        newOrder.setPaymentMethod(paymentMethod);
        newOrder.setDeliverDays(checkoutInfo.getDeliverDays());
        newOrder.setDeliverDate(checkoutInfo.getDeliverDate());
        
        if (address == null) {
            newOrder.copyAddressFromCustomer();
        } else {
            newOrder.copyShippingAddress(address);
        }
        
        Set<OrderDetail> orderDetails = newOrder.getOrderDetails();
        
        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();
            
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(newOrder);
            orderDetail.setProduct(product);
            orderDetail.setQuantity(cartItem.getQuantity());
            orderDetail.setUnitPrice(product.getDiscountPrice());
            orderDetail.setProductCost(product.getCost() * cartItem.getQuantity());
            orderDetail.setSubtotal(cartItem.getSubtotal());
            orderDetail.setShippingCost(cartItem.getShippingCost());
            
            orderDetails.add(orderDetail);
        }
        
        Order savedOrder = repo.save(newOrder);

        for (OrderDetail orderDetail : savedOrder.getOrderDetails()) {
            Product product = orderDetail.getProduct();
            product.setQuantity(product.getQuantity() - orderDetail.getQuantity());
            
            // Kiểm tra số lượng và cập nhật trạng thái inStock
            if (product.getQuantity() <= 0) {
                product.setInStock(false);
            }

            // Lưu sản phẩm cập nhật
            productRepository.save(product);
        }


        // Thêm OrderTrack mới
        
        OrderTrack newOrderTrack = new OrderTrack();
        newOrderTrack.setOrder(savedOrder);
        newOrderTrack.setStatus(OrderStatus.NEW);
        newOrderTrack.setUpdatedTime(new Date());
        newOrderTrack.setNotes(OrderStatus.NEW.defaultDescription());
        
        orderTrackRepo.save(newOrderTrack);

        return savedOrder;
    }

    public Page<Order> listForCustomerByPage(Customer customer, int pageNum, 
			String sortField, String sortDir, String keyword) {
		Sort sort = Sort.by(sortField);
		sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
		
		Pageable pageable = PageRequest.of(pageNum - 1, ORDERS_PER_PAGE, sort);
		
		if (keyword != null) {
			return repo.findAll(keyword, customer.getId(), pageable);
		}
		
		return repo.findAll(customer.getId(), pageable);
		
	}	

    public Order getOrder(Integer id, Customer customer) {
		return repo.findByIdAndCustomer(id, customer);
	}	

    public void setOrderReturnRequested(OrderReturnRequest request, Customer customer)
			throws OrderNotFoundException {
		Order order = repo.findByIdAndCustomer(request.getOrderId(), customer);
		if (order == null) {
			throw new OrderNotFoundException("Đơn hàng ID " + request.getOrderId() + " không tìm thấy");
		}
		
		if (order.isReturnRequested()) return;
		
		OrderTrack track = new OrderTrack();
		track.setOrder(order);
		track.setUpdatedTime(new Date());
		track.setStatus(OrderStatus.RETURN_REQUESTED);
		
		String notes = "Lý do: " + request.getReason();
		if (!"".equals(request.getNote())) {
			notes += ". " + request.getNote();
		}
		
		track.setNotes(notes);
		
		order.getOrderTracks().add(track);
		order.setStatus(OrderStatus.RETURN_REQUESTED);
		
		repo.save(order);
	}
}
