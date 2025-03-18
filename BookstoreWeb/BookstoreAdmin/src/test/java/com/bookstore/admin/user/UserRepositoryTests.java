package com.bookstore.admin.user;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import com.bookstore.admin.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.Rollback;

import com.bookstore.entity.Role;
import com.bookstore.entity.User;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace =  Replace.NONE)
@Rollback(false)
public class UserRepositoryTests {
	@Autowired
	private UserRepository repo;
	
	@Autowired
	private TestEntityManager entityManager;
	
	@Test
	public void testCreateUserWithOneRole() {
		Role roleAdmin = entityManager.find(Role.class, 1);
		User user = new User("admin@test.com", "$2a$10$d8/TBkIUzNqcOmuOB8P.PuBB7GgwvbBaGJac0LjgJCsBP4.2C8NFG", "test first", "test last");
		user.addRole(roleAdmin);
		
		
		User saveUser = repo.save(user);
		
		assertThat(saveUser.getId()).isGreaterThan(0);
	}
	
	@Test
	public void testCreateUserWithTwoRole() {
		User userQuanLA2 = new User("anhquancs202@gmail.com", "nam2020", "Quan2", "Le Anh");
		Role roleEditor = new Role(3);
		Role roleAssistant = new Role(5);

		
		userQuanLA2.addRole(roleEditor);
		userQuanLA2.addRole(roleAssistant);
		
		User saveUser = repo.save(userQuanLA2);
		
		assertThat(saveUser.getId()).isGreaterThan(0);
		
	}
	@Test 
	public void testListAllUsers(){
		Iterable<User> listUsers = repo.findAll();
		listUsers.forEach(user  -> System.out.println(user));
		
	}
	
	@Test
	public void testGetUserById() {
		 User userQuanLA =  repo.findById(1).get();
		 System.out.println(userQuanLA);
		 assertThat(userQuanLA).isNotNull();
		
	}
	@Test 
	public void testUpdateUserDetails() {
		 User userQuanLA =  repo.findById(1).get();
		 userQuanLA.setEnabled(true);
		 userQuanLA.setEmail("leanhquan@gmail.com");
		 
		 repo.save(userQuanLA);
	}
	
	@Test
	public void testUpdateUserRoles() {
		 User userQuanLA2 =  repo.findById(4).get();
			Role roleEditor = new Role(3);
			Role roleSalesperson = new Role(2);
		 userQuanLA2.getRoles().remove(roleEditor);
		 userQuanLA2.addRole(roleSalesperson);
		 
		 repo.save(userQuanLA2);
	}
	@Test
	public void testDeleteUser() {
		
		Integer userId = 16;
		repo.deleteById(userId);
		
	}
	
	@Test
	public void testGetUserByEmail() {
		String email = "leanhquan@gmail.com";
		
		User user =	repo.getUserByEmail(email);
		
		assertThat(user).isNotNull();
	}
	
	@Test
	public void testountById() {
		Integer id = 1;
		Long countbyId = repo.countById(id);
		
		assertThat(countbyId).isNotNull().isGreaterThan(0);
		
	}
	@Test
	public void testDisableUser() {
		Integer id = 45;
		repo.updateEnabledStatus(id, false);
	}
	@Test
	public void testEnabledUser() {
		Integer id = 2015;
		repo.updateEnabledStatus(id, true);
	}
	@Test
	public void testListFirstPage() {
		int pageNumber = 0;
		int pageSize = 4;
		Pageable pageable = PageRequest.of(pageNumber, pageSize);
		Page<User> page  = repo.findAll(pageable);
		
		List<User> listUsers =  page.getContent();
		listUsers.forEach(user -> System.out.println(user));
		
		assertThat(listUsers.size()).isEqualTo(pageSize);
	}
	@Test
	public void testSearchUsers() {
		String keyword = "quan";
		int pageNumber = 0;
		int pageSize = 4;
		Pageable pageable = PageRequest.of(pageNumber, pageSize);
		Page<User> page  = repo.findAll(keyword, pageable);
		
		List<User> listUsers =  page.getContent();
		listUsers.forEach(user -> System.out.println(user));
		
		assertThat(listUsers.size()).isGreaterThan(0);
		
	}
	
	
	
	
	
	
	
	
}
