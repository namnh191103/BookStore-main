package com.bookstore.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.bookstore.entity.City;

public interface CityRepository extends CrudRepository<City, Integer>{
    
    public List<City> findAllByOrderByNameAsc();

    
    @Query("SELECT c FROM City c WHERE c.code = ?1")
    public City findByCode(String string);

    
}
