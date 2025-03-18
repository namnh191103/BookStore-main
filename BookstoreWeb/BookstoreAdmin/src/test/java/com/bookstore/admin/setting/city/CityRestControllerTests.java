package com.bookstore.admin.setting.city;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;

import com.bookstore.admin.repository.CityRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.bookstore.entity.City;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
public class CityRestControllerTests {
    
    @Autowired MockMvc mockMvc;

    @Autowired ObjectMapper objectMapper;

    @Autowired
    CityRepository repo;

    @Test
    @WithMockUser(username = "thuythuy@gmail.com", password = "13123123", roles = "ADMIN")
    public void testListCities() throws Exception{
        String url = "/cities/list";

        MvcResult result = mockMvc.perform(get(url))
            .andExpect(status().isOk())
            .andDo(print())
            .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        City[] cities = objectMapper.readValue(jsonResponse, City[].class);

        assertThat(cities).hasSizeGreaterThan(0);
    }

    @Test
    @WithMockUser(username = "thuythuy@gmail.com", password = "13123123", roles = "ADMIN")
    public void testCreateCity() throws JsonProcessingException, Exception{
        String url = "/cities/save";
        String cityName = "Da Nang";
        String cityCode = "DN";
        City city = new City(cityName, cityCode);

        MvcResult result = mockMvc.perform(post(url).contentType("application/json")
                .content(objectMapper.writeValueAsString(city))
                .with(csrf()))
            .andDo(print())
            .andExpect(status().isOk())
            .andReturn();

        String response = result.getResponse().getContentAsString();
        Integer cityId = Integer.parseInt(response);

        Optional<City> findById = repo.findById(cityId);
        assertThat(findById.isPresent());
        City savedCity = findById.get();

        assertThat(savedCity.getName()).isEqualTo(cityName);
    }

    @Test
    @WithMockUser(username = "thuythuy@gmail.com", password = "13123123", roles = "ADMIN")
    public void testUpdateCity() throws JsonProcessingException, Exception{
        String url = "/cities/save";

        Integer cityId = 2;
        String cityName = "Da Nang1";
        String cityCode = "DN1";
        City city = new City(cityId, cityName, cityCode);

        mockMvc.perform(post(url).contentType("application/json")
                .content(objectMapper.writeValueAsString(city))
                .with(csrf()))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().string(String.valueOf(cityId)));

        Optional<City> findById = repo.findById(cityId);
        assertThat(findById.isPresent());

        City savedCity = findById.get();

        assertThat(savedCity.getName()).isEqualTo(cityName);
    }

    @Test
    @WithMockUser(username = "thuythuy@gmail.com", password = "13123123", roles = "ADMIN")
    public void testDeleteCity() throws Exception{
        Integer cityId = 2;
        String url = "/cities/delete/" + cityId;
        mockMvc.perform(get(url)).andExpect(status().isOk());

        Optional<City> findById = repo.findById(cityId);

        assertThat(findById).isNotPresent();
    }

}
