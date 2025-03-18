package com.bookstore.admin.setting;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;

import com.bookstore.admin.repository.CurrencyRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.bookstore.entity.Currency;


@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class CurrencyRepositoryTests {

    @Autowired 
    private CurrencyRepository repo;

    @Test
    public void testCreateCurrencies() {
        List<Currency> listCurrencies = Arrays.asList(
            new Currency("United States Dollar", "$", "USD"), 
            new Currency("British Pound", "E", "GPB"), 
            new Currency("Japanese Yen", "Y", "JPY"), 
            new Currency("Euro", "€", "EUR"), 
            new Currency("Russian Ruble", "P", "RUB"), 
            new Currency("South Korean Won", "W", "KRW"), 
            new Currency("Chinese Yuan", "Y", "CNY"), 
            new Currency("Brazilian Real", "R$", "BRL"), 
            new Currency("Australian Dollar", "$", "AUD"), 
            new Currency ("Canadian Dollar", "$", "CAD"), 
            new Currency("Vietnamese Dong", "d", "VND"), 
            new Currency("Indian Rupee", "R", "INR"),
            new Currency("Thailand Baht", "B", "TLD")
        );

        repo.saveAll(listCurrencies);

        Iterable<Currency> iterable = repo.findAll();

        assertThat(iterable).size().isEqualTo(13);
    }

    @Test
    public void testListAllOrderByNameAsc() {
        List<Currency> currencies = repo.findAllByOrderByNameAsc();

        currencies.forEach(System.out::println);

        assertThat(currencies.size()).isGreaterThan(0);
    }

}
