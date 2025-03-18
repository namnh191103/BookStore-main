package com.bookstore.admin.category;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Set;

import com.bookstore.admin.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;

import com.bookstore.entity.Category;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace =Replace.NONE)
@Rollback(false)

public class CategoryRepositoryTests {
	
	@Autowired
	private CategoryRepository repo;
	
	@Test
	public void testCreateRootCategory() {
		Category category = new Category("Books");
		Category savedCategory = repo.save(category);
		
		assertThat(savedCategory.getId()).isGreaterThan(0);
	}
	
	@Test
	public void testCreateSubCategory() {
		// ===== New 1 cái, với parent_id là 1 =============================
		Category parent = new Category(1008);
		Category subCategory = new Category("Book18", parent);
		Category savedCategory = repo.save(subCategory);
		
		assertThat(savedCategory.getId()).isGreaterThan(0);


		// ===== New 1 lúc 2 cái, với parent_id là 2 =======================
		// Category parent = new Category(2);
		// Category book2 = new Category("Book2", parent);
		// Category book3 = new Category("Book3", parent);

		// repo.saveAll(List.of(book2, book3));
	}
	
	@Test
	public void testGetCategory() {
		Category category = repo.findById(1004).get();
		System.out.println(category.getName());

		Set<Category> children = category.getChildren();

		for (Category subCategory : children){
			System.out.println(subCategory.getName());
		}

		assertThat(children.size()).isGreaterThan(0);
	}

	@Test
	public void testPrintHierarchicalCategories() {
		Iterable<Category> categories = repo.findAll();

		for (Category category : categories){
			if (category.getParent() == null){
				System.out.println(category.getName());

				Set<Category> children = category.getChildren();

				for (Category subCategory : children){
					System.out.println("--" + subCategory.getName());
					printChildren(subCategory, 1);
				}
			}
		}
	}

	private void printChildren(Category parent, int subLevel){
		int newSubLevel = subLevel + 1;
		Set<Category> children = parent.getChildren();

		for (Category subCategory : children){
			for(int i = 0; i < newSubLevel; i++){
				System.out.print("--");
			}
			System.out.println(subCategory.getName());

			printChildren(subCategory, newSubLevel);
		}

	}

	@Test
	public void testListRootCategories(){
		List<Category> rootCategories = repo.findRootCategories(Sort.by("name").ascending());
		rootCategories.forEach(cat -> System.out.println(cat.getName()));
	}

	@Test
	public void testFindByName() {
		String name = "Books";
		Category category = repo.findByName(name);

		assertThat(category).isNotNull();
		assertThat(category.getName()).isEqualTo(name);
	}

	@Test
	public void testFindByAlias() {
		String alias = "Books";
		Category category = repo.findByAlias(alias);
		
		assertThat(category).isNotNull();
		assertThat(category.getAlias()).isEqualTo(alias);
	}

}
