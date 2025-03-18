package com.bookstore.admin.setting.district;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;

import com.bookstore.admin.repository.DistrictRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.bookstore.admin.repository.CityRepository;
import com.bookstore.entity.City;
import com.bookstore.entity.District;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
public class DistrictRestControllerTests {
    
    @Autowired MockMvc mockMvc;

    @Autowired ObjectMapper objectMapper;

    @Autowired CityRepository cityRepo;

    @Autowired
    DistrictRepository districtRepo;

    @Test
    @WithMockUser(username = "thuythuy@gmail.com", password = "13123123", roles = "ADMIN")
    public void testListByCities() throws Exception{
        Integer cityId = 2;
        String url = "/districts/list_by_city/" + cityId;

        MvcResult result = mockMvc.perform(get(url))
            .andExpect(status().isOk())
            .andDo(print())
            .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        District[] districts = objectMapper.readValue(jsonResponse, District[].class);

        assertThat(districts).hasSizeGreaterThan(1);
    }

    @Test
    @WithMockUser(username = "thuythuy@gmail.com", password = "13123123", roles = "ADMIN")
    public void testCreateDistrict() throws Exception{
        String url = "/districts/save";
        Integer cityId = 2;
        City city = cityRepo.findById(cityId).get();
        District district = new District("Cam Le", city);

        MvcResult result = mockMvc.perform(post(url).contentType("application/json")
                .content(objectMapper.writeValueAsString(district))
                .with(csrf()))
            .andDo(print())
            .andExpect(status().isOk())
            .andReturn();

        String response = result.getResponse().getContentAsString();
        Integer districtId = Integer.parseInt(response);

        Optional<District> findById = districtRepo.findById(districtId);
        assertThat(findById.isPresent());
    }

    @Test
    @WithMockUser(username = "thuythuy@gmail.com", password = "13123123", roles = "ADMIN")
    public void testUpdateDistrict() throws Exception{
        String url = "/districts/save";
        Integer districtId = 9;
        String districtName = "Thanh Khe";

        District district = districtRepo.findById(districtId).get();
        district.setName(districtName);

        mockMvc.perform(post(url).contentType("application/json")
                .content(objectMapper.writeValueAsString(district))
                .with(csrf()))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().string(String.valueOf(districtId)));

        Optional<District> findById = districtRepo.findById(districtId);
        assertThat(findById.isPresent());

        District updatedDistrict = findById.get();

        assertThat(updatedDistrict.getName()).isEqualTo(districtName);
    }

    @Test
    @WithMockUser(username = "thuythuy@gmail.com", password = "13123123", roles = "ADMIN")
    public void testDeleteDistrict() throws Exception{
        Integer districtId = 2;
        String url = "/districts/delete/" + districtId;
        
        mockMvc.perform(get(url)).andExpect(status().isOk());

        Optional<District> findById = districtRepo.findById(districtId);

        assertThat(findById).isNotPresent();
    }

}
