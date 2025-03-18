package com.bookstore.controller;

import com.bookstore.config.Utility;
import com.bookstore.entity.Customer;
import com.bookstore.entity.product.FavoriteProducts;
import com.bookstore.entity.product.Product;
import com.bookstore.service.CustomerService;
import com.bookstore.service.FavoriteProductsService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

@Controller
public class FavoriteProductsController {
    @Autowired
    private CustomerService customerService;
    @Autowired
    private FavoriteProductsService favoriteProductsService;

    @GetMapping("/favorite")
    public String viewFavorite(Model model, HttpServletRequest request) {
        Customer customer = getAuthenticatedCustomer(request);


        List<FavoriteProducts> favoriteProducts = favoriteProductsService.getList(customer.getId());
        List<Product> favorites = new ArrayList<>();
        for (FavoriteProducts p : favoriteProducts) {
            favorites.add(p.getProduct());
        }
        model.addAttribute("favorites", favorites);
        return "product/favorite_product";
    }

    private Customer getAuthenticatedCustomer(HttpServletRequest request) {
        String email = Utility.getEmailOfAuthenticatedCustomer(request);
        return customerService.getCustomerByEmail(email);
    }

    @GetMapping("/favorite/remove/{productId}")
    public ModelAndView remove(@PathVariable("productId") Integer productId,Model model, HttpServletRequest request) {
        Customer customer = getAuthenticatedCustomer(request);
        FavoriteProducts favoriteProducts = favoriteProductsService.checkFavorite(customer.getId(), productId);
        favoriteProductsService.delete(favoriteProducts);

        ModelAndView modelAndView = new ModelAndView("redirect:/favorite");
        return modelAndView;
    }
}
