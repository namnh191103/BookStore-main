package com.bookstore.admin.review;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.bookstore.admin.repository.ReviewRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import static org.assertj.core.api.Assertions.assertThat;



import com.bookstore.entity.Customer;
import com.bookstore.entity.Review;
import com.bookstore.entity.product.Product;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class ReviewRepositoryTest {

    @Autowired private ReviewRepository repo;
	
	@Test
	public void testCreateReview() {
		Integer productId = 29;
		Product product = new Product(productId);
		
		Integer customerId = 2016;
		Customer customer = new Customer(customerId);
		
		Review review = new Review();
		review.setProduct(product);
		review.setCustomer(customer);
		review.setReviewTime(new Date());
		review.setHeadline("what the hell?");
		review.setComment("Product not nice");
		review.setRating(2);
		
		Review savedReview = repo.save(review);
		
		assertThat(savedReview).isNotNull();
		assertThat(savedReview.getId()).isGreaterThan(0);		
	}

    @Test
	public void testListReviews() {
		List<Review> listReviews = repo.findAll();
		
		assertThat(listReviews.size()).isGreaterThan(0);
		
		listReviews.forEach(System.out::println);
	}

    @Test
	public void testGetReview() {
		Integer id = 1;
		Review review = repo.findById(id).get();
		
		assertThat(review).isNotNull();
		
		System.out.println(review);
	}

    @Test
	public void testUpdateReview() {
		Integer id = 1;
		String headline = "Awesome!";
		String comment = "Product very nice";
		
		Review review = repo.findById(id).get();
		review.setHeadline(headline);
		review.setComment(comment);
		
		Review updatedReview = repo.save(review);
		
		assertThat(updatedReview.getHeadline()).isEqualTo(headline);
		assertThat(updatedReview.getComment()).isEqualTo(comment);
	}
	
    @Test
	public void testDeleteReview() {
		Integer id = 1;
		repo.deleteById(id);
		
		Optional<Review> findById = repo.findById(id);
		
		assertThat(findById).isNotPresent();
	}

	
}
