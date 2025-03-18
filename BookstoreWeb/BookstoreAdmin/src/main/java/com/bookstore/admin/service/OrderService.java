package com.bookstore.admin.service;

import java.util.List;
import java.util.NoSuchElementException;

import com.bookstore.admin.exceptionhandler.OrderNotFoundException;
import com.bookstore.admin.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;

import com.bookstore.admin.paging.PagingAndSortingHelper;
import com.bookstore.admin.repository.CityRepository;
import com.bookstore.entity.City;
import com.bookstore.entity.order.Order;

@Service
public class OrderService {
    @Autowired private CityRepository cityRepository; 
    @Autowired private OrderRepository orderRepo;


    private static final int ORDERS_PER_PAGE = 10;
    @Autowired
    private OrderRepository repo;

    public void listByPage(int pageNum, PagingAndSortingHelper helper) {
        String sortField = helper.getSortField();
        String sortDir = helper.getSortDir();
        String keyword = helper.getKeyword();

        Sort sort = null;

        if ("destination".equals(sortField)) {
            sort = Sort.by("city").and(Sort.by("district")).and(Sort.by("ward"));
        } else {
            sort = Sort.by(sortField);
        }

        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
        Pageable pageable = PageRequest.of(pageNum - 1, ORDERS_PER_PAGE, sort);

        Page<Order> page = null;

        if (keyword != null) {
            page = repo.findAll(keyword, pageable);
        } else {
            page = repo.findAll(pageable);
        }

        helper.updateModelAttributes(pageNum, page);
    }

    public Order get(Integer id) throws OrderNotFoundException {
		try {
			return repo.findById(id).get();
		} catch (NoSuchElementException ex) {
			throw new OrderNotFoundException("Không tìm thấy bất kỳ đơn hàng nào có ID: " + id);
		}
	}

    public void delete(Integer id) throws OrderNotFoundException {
		Long count = repo.countById(id);
		if (count == null || count == 0) {
			throw new OrderNotFoundException("Không tìm thấy bất kỳ đơn hàng nào có ID: " + id); 
		}
		
		repo.deleteById(id);
	}

    public List<City> listAllCities() {
        return cityRepository.findAllByOrderByNameAsc();
    }

    public void save(Order orderInForm) {
		Order orderInDB = orderRepo.findById(orderInForm.getId()).get();
		orderInForm.setOrderTime(orderInDB.getOrderTime());
		orderInForm.setCustomer(orderInDB.getCustomer());
		
		orderRepo.save(orderInForm);
	}	
}
