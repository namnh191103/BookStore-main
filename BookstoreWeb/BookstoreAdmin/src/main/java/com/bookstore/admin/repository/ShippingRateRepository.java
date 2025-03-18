package com.bookstore.admin.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.bookstore.entity.ShippingRate;

@Repository
public interface ShippingRateRepository extends CrudRepository<ShippingRate, Integer>, SearchRepository<ShippingRate, Integer> {

    @Query("SELECT sr FROM ShippingRate sr WHERE sr.city.id = ?1 AND sr.district = ?2")
    public ShippingRate findByCityAndDistrict(Integer cityId, String district);

    
    @Modifying
    @Query("UPDATE ShippingRate sr SET sr.codSupported = :codSupported WHERE sr.id = :id")
    void updateCODSupport(@Param("id") Integer id, @Param("codSupported") boolean codSupported);


    @Query("SELECT sr FROM ShippingRate sr WHERE sr.city.name LIKE %?1% OR sr.district LIKE %?1%")
    public Page<ShippingRate> findAll(String keyword, Pageable pageable);

    public Long countById(Integer id);
}
