package com.bookstore.service;

import java.util.List;
import java.util.NoSuchElementException;

import com.bookstore.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bookstore.entity.product.Product;
import com.bookstore.exception.ProductNotFoundException;

@Service
@Transactional
public class ProductService {
    public static final int PRODUCTS_PER_PAGE = 10;
    public static final int SEARCH_RESULTS_PER_PAGE = 10;

    @Autowired private ProductRepository repo;

    public Page<Product> listByCategory(int pageNum, Integer categoryId){
        String categoryIdMatch = "-" + String.valueOf(categoryId) + "-";
        Pageable pageable = PageRequest.of(pageNum -1, PRODUCTS_PER_PAGE);

        return repo.listByCategory(categoryId, categoryIdMatch, pageable);
    }
    
    public Product getProduct(String alias) throws ProductNotFoundException {
        Product product = repo.findByAlias(alias);
        if (product == null) {
            throw new ProductNotFoundException("Could not find any product with alias " + alias);
        }

        return product;
    }

    public Product findById(Integer id) {
        return repo.findById(id).orElse(null);
    }

    public Product getProduct(Integer id) throws ProductNotFoundException {
		try {
			Product product = repo.findById(id).get();
			return product;
		} catch (NoSuchElementException ex) {
			throw new ProductNotFoundException("Could not find any product with ID " + id);
		}
	}	

    public Page<Product> search(String keyword, int pageNum){
        Pageable pageable = PageRequest.of(pageNum -1, SEARCH_RESULTS_PER_PAGE);

        return repo.search(keyword, pageable);
    }

    public List<Product> getBestSellerProducts() {
        Pageable pageable = PageRequest.of(0, 12);
        return repo.findBestSellerProducts(pageable);
    }

    public void save(Product product) {
        repo.save(product);
    }

}