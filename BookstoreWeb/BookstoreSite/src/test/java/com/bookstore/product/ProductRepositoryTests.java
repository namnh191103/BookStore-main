package com.bookstore.product;

import static org.assertj.core.api.Assertions.assertThat;

import com.bookstore.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.bookstore.entity.product.Product;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class ProductRepositoryTests {
	
	@Autowired
    ProductRepository repo;
	
	@Test
	public void testFindByAlias() {
		String alias = "something";
		Product product = repo.findByAlias(alias);

		assertThat(product).isNotNull();
	}
	
}
