package com.bookstore.admin.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.bookstore.entity.City;
import com.bookstore.entity.District;

public interface DistrictRepository extends CrudRepository<District, Integer>{
    
    public List<District> findByCityOrderByNameAsc(City city);
    
}
