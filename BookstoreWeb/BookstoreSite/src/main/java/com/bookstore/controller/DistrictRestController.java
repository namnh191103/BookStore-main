package com.bookstore.controller;

import java.util.ArrayList;
import java.util.List;

import com.bookstore.repository.DistrictRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.bookstore.entity.City;
import com.bookstore.entity.District;
import com.bookstore.entity.DistrictDTO;

@RestController
public class DistrictRestController {
    
	@Autowired private DistrictRepository repo;
	
	@GetMapping("/settings/list_districts_by_city/{id}")
	public List<DistrictDTO> listByCity(@PathVariable("id") Integer cityId) {
		List<District> listDistricts = repo.findByCityOrderByNameAsc(new City(cityId));
		List<DistrictDTO> result = new ArrayList<>();
		for (District district : listDistricts) {
			result.add(new DistrictDTO(district.getId(), district.getName()));
		}

		return result;
	}
	
}
