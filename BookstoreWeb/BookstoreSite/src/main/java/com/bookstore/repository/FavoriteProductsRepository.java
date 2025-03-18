package com.bookstore.repository;

import com.bookstore.entity.product.FavoriteProducts;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface FavoriteProductsRepository extends CrudRepository<FavoriteProducts, Integer> {
    List<FavoriteProducts> findByCustomerId(Integer customerId);

    FavoriteProducts findByCustomerIdAndProductId(Integer customerId, Integer productId);

}
