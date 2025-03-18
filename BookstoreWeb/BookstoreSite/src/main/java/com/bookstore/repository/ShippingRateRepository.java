package com.bookstore.repository;

import org.springframework.data.repository.CrudRepository;

import com.bookstore.entity.City;
import com.bookstore.entity.ShippingRate;

public interface ShippingRateRepository extends CrudRepository<ShippingRate, Integer> {
	
	public ShippingRate findByCityAndDistrict(City city, String state);
}
