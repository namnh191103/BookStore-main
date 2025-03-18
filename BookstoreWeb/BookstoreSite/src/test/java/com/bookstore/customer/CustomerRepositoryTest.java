package com.bookstore.customer;

import java.util.Date;
import java.util.Optional;

import com.bookstore.entity.AuthenticationType;
import com.bookstore.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;
import static org.assertj.core.api.Assertions.assertThat;


import com.bookstore.entity.City;
import com.bookstore.entity.Customer;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class CustomerRepositoryTest {

    @Autowired private CustomerRepository repository;
    @Autowired private TestEntityManager entityManager; 

    @Test
    public void testCreateCustomer() {
        Integer cityId = 2; 
        City city = entityManager.find(City.class, cityId); 

        Customer customer = new Customer(); 

        customer.setCity(city);
        customer.setFirstName("Van A");
        customer.setLastName("Nguyen");
        customer.setPassword("password");
        customer.setEmail("nguyen@gmail.com");
        customer.setPhoneNumber("0222 222 222");
        customer.setAddressLine("123 street");
        customer.setWard("ward test");
        customer.setDistrict("district test");
        customer.setCreatedTime(new Date());

        Customer savedCustomer = repository.save(customer); 

        assertThat(savedCustomer).isNotNull(); 
        assertThat(savedCustomer.getId()).isGreaterThan(0);
    }

    @Test
    public void testCreateCustomer2() {
        Integer cityId = 2; 
        City city = entityManager.find(City.class, cityId); 

        Customer customer = new Customer(); 

        customer.setCity(city);
        customer.setFirstName("Văn B");
        customer.setLastName("Nguyễn");
        customer.setPassword("password");
        customer.setEmail("nguyen11@gmail.com");
        customer.setVerificationCode("checkout123");
        customer.setPhoneNumber("0222 222 222");
        customer.setAddressLine("đường 123");
        customer.setWard("phường 123");
        customer.setDistrict("quận 123");
        customer.setCreatedTime(new Date());

        Customer savedCustomer = repository.save(customer); 

        assertThat(savedCustomer).isNotNull(); 
        assertThat(savedCustomer.getId()).isGreaterThan(0);
    }

    @Test 
    public void testListCustomer() {
        Iterable<Customer> customers = repository.findAll(); 
        customers.forEach(System.out::println);

        assertThat(customers).hasSizeGreaterThan(1); 
    }

    @Test
    public void testUpdateCustomer() {
        Integer customerId = 1; 
        String lastName = "Nguyễn"; 

        Customer customer = repository.findById(customerId).get(); 
        customer.setLastName(lastName);
        customer.setEnabled(true);

        Customer updateCustomer = repository.save(customer); 
        assertThat(updateCustomer.getLastName()).isEqualTo(lastName); 
    }

    @Test
    public void testGetCustomer() {
        Integer customerId = 3; 
        Optional<Customer> findById = repository.findById(customerId); 

        assertThat(findById).isPresent(); 

        Customer customer = findById.get(); 
        System.out.println(customer);
    }

    @Test
    public void testDeleteCustomer() {
        Integer customerId = 3; 
        repository.deleteById(customerId);

        Optional<Customer> findById = repository.findById(customerId); 
        assertThat(findById).isNotPresent(); 
    }

    @Test
    public void testFindByEmail() {
        String email = "nguyen@gmail.com"; 
        Customer customer = repository.findByEmail(email); 

        assertThat(customer).isNotNull(); 
        System.out.println(customer);
    }

    @Test
    public void testEnableCustomer() {
        Integer customerId = 4; 
        repository.enable(customerId);

        Customer customer = repository.findById(customerId).get(); 
        assertThat(customer.isEnabled()).isTrue(); 
    }

    @Test
    public void testUpdateAuthenticationType() {
        Integer id = 1;
        repository.updateAuthenticationType(id, AuthenticationType.FACEBOOK);

        Customer customer = repository.findById(id).get();

        assertThat(customer.getAuthenticationType()).isEqualTo(AuthenticationType.FACEBOOK);
    }
}
