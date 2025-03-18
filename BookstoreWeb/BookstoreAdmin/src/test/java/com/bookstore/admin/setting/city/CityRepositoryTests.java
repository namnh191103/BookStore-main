package com.bookstore.admin.setting.city;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import com.bookstore.admin.repository.CityRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.bookstore.entity.City;


@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class CityRepositoryTests {

    @Autowired 
    private CityRepository repo;

    @Test
    public void testCreateCity() {
        City city = repo.save(new City("Da Nang", "DN"));
        assertThat(city).isNotNull();
        assertThat(city.getId()).isGreaterThan(0);
    }

    @Test
    public void testListCities() {
        List<City> listCities = repo.findAllByOrderByNameAsc();
        listCities.forEach(System.out::println);

        assertThat(listCities.size()).isGreaterThan(0);
    }

    @Test
    public void testUpdateCity() {
        Integer id = 1;
        String name = "Republic of VietNam";

        City city = repo.findById(id).get();
        city.setName(name);

        City updateCity = repo.save(city);

        assertThat(updateCity.getName()).isEqualTo(name);
    }

    @Test
    public void testGetCity() {
        Integer id = 3;
        City city = repo.findById(id).get();
        
        assertThat(city).isNotNull();
    }

    @Test
    public void testDeleteCity() {
        Integer id = 5;
        repo.deleteById(id);
        
        Optional<City> findById = repo.findById(id);
        assertThat(findById.isEmpty());
    }

}
