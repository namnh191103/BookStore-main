package com.bookstore.admin.service;

import java.util.List;
import java.util.NoSuchElementException;

import com.bookstore.admin.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;


import com.bookstore.admin.repository.CityRepository;
import com.bookstore.entity.City;
import com.bookstore.entity.Customer;
import com.bookstore.exception.CustomerNotFoundException;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class CustomerService {
    public static final int CUSTOMERS_PER_PAGE = 10;

    @Autowired private CustomerRepository customerRepository;
    @Autowired private CityRepository cityRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    public Page<Customer> listByPage(int pageNum, String sortField, String sortDir, String keyword) {
		Sort sort = Sort.by(sortField);
		sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();

		Pageable pageable = PageRequest.of(pageNum - 1, CUSTOMERS_PER_PAGE, sort);

		if (keyword != null) {
			return customerRepository.findAll(keyword, pageable);
		}

		return customerRepository.findAll(pageable);
	}

    public void updateCustomerEnabledStatus(Integer id, boolean enabled) {
		customerRepository.updateEnabledStatus(id, enabled);
	}

    public Customer get(Integer id) throws CustomerNotFoundException {
		try {
			return customerRepository.findById(id).get();
		} catch (NoSuchElementException ex) {
			throw new CustomerNotFoundException("Không tìm thấy khách hàng với ID " + id);
		}
	}

    public List<City> listAllCities() {
		return cityRepository.findAllByOrderByNameAsc();
	}

    public boolean isEmailUnique(Integer id, String email) {
		Customer existCustomer = customerRepository.findByEmail(email);

		if (existCustomer != null && !existCustomer.getId().equals(id)) {
			// Tìm thấy một khách hàng khác có cùng email
			return false;
		}

		return true;
	}


    public void save(Customer customerInForm) {
		Customer customerInDB = customerRepository.findById(customerInForm.getId()).get();
		
		if (!customerInForm.getPassword().isEmpty()) {
			String encodedPassword = passwordEncoder.encode(customerInForm.getPassword());
			customerInForm.setPassword(encodedPassword);			
		} else {
			customerInForm.setPassword(customerInDB.getPassword());
		}		
		
		customerInForm.setEnabled(customerInDB.isEnabled());
		customerInForm.setCreatedTime(customerInDB.getCreatedTime());
		customerInForm.setVerificationCode(customerInDB.getVerificationCode());
		customerInForm.setAuthenticationType(customerInDB.getAuthenticationType());
		customerInForm.setResetPasswordToken(customerInDB.getResetPasswordToken());

		customerRepository.save(customerInForm);
	}

	public void delete(Integer id) throws CustomerNotFoundException {
		Long count = customerRepository.countById(id);
		if (count == null || count == 0) {
			throw new CustomerNotFoundException("Could not find any customers with ID " + id);
		}

		customerRepository.deleteById(id);
	}
}
