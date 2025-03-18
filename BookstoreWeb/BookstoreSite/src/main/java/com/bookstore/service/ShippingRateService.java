package com.bookstore.service;

import com.bookstore.repository.ShippingRateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bookstore.entity.Address;
import com.bookstore.entity.Customer;
import com.bookstore.entity.ShippingRate;

@Service
public class ShippingRateService {

	@Autowired private ShippingRateRepository repo;
	
	public ShippingRate getShippingRateForCustomer(Customer customer) {
		String district = customer.getDistrict();
		if (district == null || district.isEmpty()) {
			district = customer.getWard();
		}
		
		return repo.findByCityAndDistrict(customer.getCity(), district);
	}
	
	public ShippingRate getShippingRateForAddress(Address address) {
		String district = address.getDistrict();
		if (district == null || district.isEmpty()) {
			district = address.getWard();
		}
		
		return repo.findByCityAndDistrict(address.getCity(), district);
	}	
}