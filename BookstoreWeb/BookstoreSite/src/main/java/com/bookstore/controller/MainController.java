package com.bookstore.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.bookstore.service.CategoryService;

import com.bookstore.entity.Category;
import com.bookstore.entity.product.Product;
import com.bookstore.service.ProductService;

@Controller
public class MainController {

	@Autowired
	private CategoryService categoryService;
	@Autowired
	private ProductService productService;

	@GetMapping("")
	public String viewHomePage(Model model) {
		List<Category> listCategories = categoryService.listNoChildrenCategories();
		model.addAttribute("listCategories", listCategories);

		List<Product> bestSellers = productService.getBestSellerProducts();
		List<List<Product>> bestSellersPartitions = partitionList(bestSellers, 4);
		model.addAttribute("bestSellersPartitions", bestSellersPartitions);

		return "index";
	}

	public List<List<Product>> partitionList(List<Product> list, int size) {
		List<List<Product>> partitions = new ArrayList<>();
		for (int i = 0; i < list.size(); i += size) {
			partitions.add(list.subList(i, Math.min(i + size, list.size())));
		}
		return partitions;
	}

	@GetMapping("/login")
	public String viewLoginPage() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || authentication instanceof AnonymousAuthenticationToken)
			return "login";

		return "redirect:/";
	}

}
