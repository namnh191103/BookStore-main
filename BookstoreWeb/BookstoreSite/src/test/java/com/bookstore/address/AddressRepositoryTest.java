package com.bookstore.address;

import java.util.List;

import com.bookstore.repository.AddressRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.bookstore.entity.Address;
import com.bookstore.entity.City;
import com.bookstore.entity.Customer;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class AddressRepositoryTest {

    @Autowired private AddressRepository repo;

    @Test
    public void testAddNew() {
        Integer customerId = 1016; 
        Integer cityId = 2; 

        Address newAddress = new Address(); 

        newAddress.setCustomer(new Customer(customerId));
        newAddress.setCity(new City(cityId));
        newAddress.setFirstName("test");
        newAddress.setLastName("test");
        newAddress.setPhoneNumber("8888 8888");
        newAddress.setAddressLine("address line");
        newAddress.setWard("ward");
        newAddress.setDistrict("district");

        Address savedAddress = repo.save(newAddress);

        Assertions.assertThat(savedAddress).isNotNull(); 
        Assertions.assertThat(savedAddress.getId()).isGreaterThan(0); 
    }

    @Test
    public void testFindByCustomer() {
        Integer customerId = 1016; 
        List<Address> listAddresses = repo.findByCustomer(new Customer(customerId)); 
        Assertions.assertThat(listAddresses.size()).isGreaterThan(0);

        listAddresses.forEach(System.out::println);
    }

    @Test
    public void testFindByIdAndCustomer() {
        Integer addressId = 2; 
        Integer customerId = 1016; 

        Address address = repo.findByIdAndCustomer(addressId, customerId); 

        Assertions.assertThat(address).isNotNull(); 
    }

    @Test
    public void testUpdate() {
        Integer addressId = 4; 
        // String phoneNumber = "999 9999"; 

        Address address = repo.findById(addressId).get(); 
        // address.setPhoneNumber(phoneNumber); 


        address.setDefaultForShipping(true);

        Address updatedAddress = repo.save(address); 
        // Assertions.assertThat(updatedAddress.getPhoneNumber()).isEqualTo(phoneNumber);
    }

    @Test
    public void testDeleteByIdAndCustomer() {
        Integer addressId = 2; 
        Integer customerId = 1016; 

        repo.deleteByIdAndCustomer(addressId, customerId);

        Address address = repo.findByIdAndCustomer(addressId, customerId); 
        Assertions.assertThat(address).isNull();
    }

    @Test
    public void testSetDefault() {
        Integer addressId = 3; 
        repo.setDefaultAddress(addressId);

        Address address = repo.findById(addressId).get(); 

        Assertions.assertThat(address.isDefaultForShipping()).isTrue();
    }

    @Test
	public void testSetNonDefaultAddresses() {
		Integer addressId = 3;
		Integer customerId = 1016;
		repo.setNonDefaultForOthers(addressId, customerId);			
	}

    @Test
	public void testGetDefault() {
		Integer customerId = 1016;
		Address address = repo.findDefaultByCustomer(customerId);
        Assertions.assertThat(address).isNotNull();
		System.out.println(address);
	}
}

