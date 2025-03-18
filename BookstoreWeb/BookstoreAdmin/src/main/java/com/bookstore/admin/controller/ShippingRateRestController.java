package com.bookstore.admin.controller;

import com.bookstore.admin.service.ShippingRateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ShippingRateRestController {

	@Autowired private ShippingRateService service;
	
	@PostMapping("/get_shipping_cost")
	public int getShippingCost(Integer productId, Integer cityId, String district) {
		return 0;
	}
}

