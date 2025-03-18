package com.bookstore.admin.brand;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import com.bookstore.admin.repository.BrandRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.bookstore.entity.Brand;
import com.bookstore.entity.Category;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class BrandRepositoryTests {
	
	@Autowired
	private BrandRepository repo;
	
	@Test
	public void testCreateBrand1() {
		Category books = new Category(6);
		Brand history = new Brand("history");
		history.getCategories().add(books);

		Brand saveBrand = repo.save(history);
		
		assertThat(saveBrand).isNotNull();
		assertThat(saveBrand.getId()).isGreaterThan(0);
	}
	
	@Test
	public void testCreateBrand2() { 
		Category comics = new Category (4);
		Category tablets = new Category(7);

		Brand romance = new Brand("Romance");
		romance.getCategories().add(comics); 
		romance.getCategories().add(tablets);
		
		Brand savedBrand = repo.save(romance);

		assertThat(savedBrand).isNotNull();
		assertThat(savedBrand.getId()).isGreaterThan(0);
	}

	@Test
	public void testCreateBrand3() { 
		Brand cookbooks = new Brand("Cookbooks");
		cookbooks.getCategories().add(new Category(29)); 
		cookbooks.getCategories().add(new Category(24));
		
		Brand savedBrand = repo.save(cookbooks);

		assertThat(savedBrand).isNotNull();
		assertThat(savedBrand.getId()).isGreaterThan(0);
	}

	@Test
	public void testFindAll() {
		Iterable<Brand> brands = repo.findAll();
		brands.forEach(System.out::println);

		assertThat(brands).isNotEmpty();
	}

	@Test
	public void testGetById() {
		Brand brand = repo.findById(1).get();

		assertThat(brand.getName()).isEqualTo("Acer");
	}

	@Test
	public void testUpdateName() {
		String newName = "Samsung Electronics";
		Brand samsung = repo.findById(3).get();
		samsung.setName(newName);

		Brand savedBrand = repo.save(samsung);
		assertThat(savedBrand.getName()).isEqualTo(newName);
	}
	
	@Test
	public void testDelete() { 
		Integer id = 2;
		repo.deleteById(id);
		
		Optional<Brand> result = repo.findById(id);

		assertThat(result.isEmpty());
	}

}
