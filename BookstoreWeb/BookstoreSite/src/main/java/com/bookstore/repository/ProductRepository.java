package com.bookstore.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.bookstore.entity.product.Product;

public interface ProductRepository extends JpaRepository<Product, Integer> {

        @Query("SELECT p FROM Product p WHERE p.enabled = true "
                        + "AND (p.category.id = ?1 OR p.category.allParentIDs LIKE %?2%)"
                        + " ORDER BY p.name ASC")
        public Page<Product> listByCategory(Integer categoryId, String categoryIDMatch, Pageable pageable);

        public Product findByAlias(String alias);

        @Query(value = "SELECT * FROM products WHERE enabled = 1 AND "
                        + "(name LIKE %?1% OR short_description LIKE %?1% OR full_description LIKE %?1%)", nativeQuery = true)
        public Page<Product> search(String keyword, Pageable pageable);

        @Query("Update Product p SET p.averageRating = COALESCE((SELECT AVG(r.rating) FROM Review r WHERE r.product.id = ?1), 0),"
                        + " p.reviewCount = (SELECT COUNT(r.id) FROM Review r WHERE r.product.id =?1) "
                        + "WHERE p.id = ?1")
        @Modifying
        public void updateReviewCountAndAverageRating(Integer productId);

        @Query("SELECT p FROM Product p WHERE p.discountPercent > 30 ORDER BY p.discountPercent DESC")
        List<Product> findBestSellerProducts(Pageable pageable);
}