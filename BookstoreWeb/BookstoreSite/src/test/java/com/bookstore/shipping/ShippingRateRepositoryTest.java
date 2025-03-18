package com.bookstore.shipping;

import com.bookstore.repository.ShippingRateRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.bookstore.entity.City;
import com.bookstore.entity.ShippingRate;

import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class ShippingRateRepositoryTest {

    @Autowired private ShippingRateRepository repo;
	
	@Test
	public void testFindByCityAndDistrict() {
		City dn = new City(2);
		String district = "District 1";
		ShippingRate shippingRate = repo.findByCityAndDistrict(dn, district);
		
		assertThat(shippingRate).isNotNull();
		System.out.println(shippingRate);
	}
}
