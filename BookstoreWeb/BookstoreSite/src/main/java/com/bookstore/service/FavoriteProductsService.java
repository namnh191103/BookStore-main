package com.bookstore.service;

import com.bookstore.entity.product.FavoriteProducts;
import com.bookstore.entity.product.Product;
import com.bookstore.repository.FavoriteProductsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class FavoriteProductsService {
    @Autowired
    private FavoriteProductsRepository favoriteProductsRepository;
    public List<FavoriteProducts> getList(Integer cusId){
        return favoriteProductsRepository.findByCustomerId(cusId);
    }
    public void save(FavoriteProducts favoriteProducts) {
        favoriteProductsRepository.save(favoriteProducts);
    }

    public void delete(FavoriteProducts favoriteProducts) {
        favoriteProductsRepository.delete(favoriteProducts);
    }

    public FavoriteProducts checkFavorite(Integer cusId,Integer productId) {
        return favoriteProductsRepository.findByCustomerIdAndProductId(cusId,productId);
    }
}
