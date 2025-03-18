package com.bookstore.controller;

import java.util.List;

import com.bookstore.entity.product.FavoriteProducts;
import com.bookstore.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.bookstore.config.Utility;
import com.bookstore.entity.Category;
import com.bookstore.entity.Customer;
import com.bookstore.entity.Review;
import com.bookstore.entity.product.Product;
import com.bookstore.exception.CategoryNotFoundException;
import com.bookstore.exception.ProductNotFoundException;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class ProductController {

    @Autowired
    private ProductService productService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private ReviewService reviewService;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private FavoriteProductsService favoriteProductsService;
    @GetMapping("/c/{category_alias}")
    public String viewCategoryFirstPage(@PathVariable("category_alias") String alias,
            Model model) {
        return viewCategoryByPage(alias, 1, model);
    }

    @GetMapping("/c/{category_alias}/page/{pageNum}")
    public String viewCategoryByPage(@PathVariable("category_alias") String alias,
            @PathVariable("pageNum") int pageNum,
            Model model) {

        try {
            Category category = categoryService.getCategory(alias);

            List<Category> listCategoryParents = categoryService.getCategoryParents(category);

            Page<Product> pageProducts = productService.listByCategory(pageNum, category.getId());
            List<Product> listProducts = pageProducts.getContent();

            long startCount = (pageNum - 1) * ProductService.PRODUCTS_PER_PAGE + 1;
            long endCount = startCount + ProductService.PRODUCTS_PER_PAGE - 1;
            if (endCount > pageProducts.getTotalElements()) {
                endCount = pageProducts.getTotalElements();
            }

            model.addAttribute("currentPage", pageNum);
            model.addAttribute("totalPages", pageProducts.getTotalPages());
            model.addAttribute("startCount", startCount);
            model.addAttribute("endCount", endCount);
            model.addAttribute("totalItems", pageProducts.getTotalElements());
            model.addAttribute("pageTitle", category.getName());
            model.addAttribute("listCategoryParents", listCategoryParents);
            model.addAttribute("listProducts", listProducts);
            model.addAttribute("category", category);


            return "product/products_by_category";
        } catch (CategoryNotFoundException ex) {
            return "error/404";
        }
    }

    @GetMapping("/p/{product_alias}")
    public String viewProductDetail(@PathVariable("product_alias") String alias, Model model,
            HttpServletRequest request) {

        try {
            Product product = productService.getProduct(alias);
            List<Category> listCategoryParents = categoryService.getCategoryParents(product.getCategory());
            Page<Review> listReviews = reviewService.list3MostRecentReviewsByProduct(product);

            Customer customer = getAuthenticatedCustomer(request);
            boolean customerReviewed = reviewService.didCustomerReviewProduct(customer, product.getId());

            if (customerReviewed) {
                model.addAttribute("customerReviewed", customerReviewed);
            } else {
                boolean customerCanReview = reviewService.canCustomerReviewProduct(customer, product.getId());
                model.addAttribute("customerCanReview", customerCanReview);
            }

            model.addAttribute("listCategoryParents", listCategoryParents);
            model.addAttribute("product", product);
            model.addAttribute("listReviews", listReviews);
            model.addAttribute("pageTitle", product.getShortName());
            boolean check =false;
            boolean checkCus =false;
            if(!ObjectUtils.isEmpty(customer)){
                checkCus =true;
                FavoriteProducts favoriteProducts = favoriteProductsService.checkFavorite(customer.getId(),product.getId());
                check = !ObjectUtils.isEmpty(favoriteProducts);
            }
            model.addAttribute("checkCus", checkCus);
            model.addAttribute("favorite", check);
            return "product/product_detail";
        } catch (ProductNotFoundException e) {
            return "error/404";
        }
    }
    @GetMapping("/p/{product_alias}/{like}")
    public String viewProductDetailAndLike(@PathVariable("product_alias") String alias,@PathVariable("like") String like, Model model,
            HttpServletRequest request) {

        try {
            Product product = productService.getProduct(alias);
            List<Category> listCategoryParents = categoryService.getCategoryParents(product.getCategory());
            Page<Review> listReviews = reviewService.list3MostRecentReviewsByProduct(product);

            Customer customer = getAuthenticatedCustomer(request);
            boolean customerReviewed = reviewService.didCustomerReviewProduct(customer, product.getId());

            if (customerReviewed) {
                model.addAttribute("customerReviewed", customerReviewed);
            } else {
                boolean customerCanReview = reviewService.canCustomerReviewProduct(customer, product.getId());
                model.addAttribute("customerCanReview", customerCanReview);
            }

            model.addAttribute("listCategoryParents", listCategoryParents);
            model.addAttribute("product", product);
            model.addAttribute("listReviews", listReviews);
            model.addAttribute("pageTitle", product.getShortName());
            boolean check =false;
            boolean checkCus =false;
            if(!ObjectUtils.isEmpty(customer)){
                checkCus =true;
                FavoriteProducts getFavoriteProducts = favoriteProductsService.checkFavorite(customer.getId(),product.getId());
                FavoriteProducts favoriteProducts = new FavoriteProducts(product,customer);
                if("like".equals(like)){
                    if(ObjectUtils.isEmpty(getFavoriteProducts)){
                        favoriteProductsService.save(favoriteProducts);
                    }
                    check =true;
                } else if ("unlike".equals(like)) {

                    favoriteProductsService.delete(getFavoriteProducts);

                }

            }

            model.addAttribute("favorite", check);
            model.addAttribute("checkCus", checkCus);
            return "product/product_detail";
        } catch (ProductNotFoundException e) {
            return "error/404";
        }
    }


    @GetMapping("/search")
    public String searchFirstPage(@Param("keyword") String keyword, Model model) {
        return searchByPage(keyword, 1, model);
    }

    @GetMapping("/search/page/{pageNum}")
    public String searchByPage(@Param("keyword") String keyword,
            @PathVariable("pageNum") int pageNum,
            Model model) {
        Page<Product> pageProducts = productService.search(keyword, pageNum);
        List<Product> listResult = pageProducts.getContent();

        long startCount = (pageNum - 1) * ProductService.SEARCH_RESULTS_PER_PAGE + 1;
        long endCount = startCount + ProductService.SEARCH_RESULTS_PER_PAGE - 1;
        if (endCount > pageProducts.getTotalElements()) {
            endCount = pageProducts.getTotalElements();
        }

        model.addAttribute("currentPage", pageNum);
        model.addAttribute("totalPages", pageProducts.getTotalPages());
        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);
        model.addAttribute("totalItems", pageProducts.getTotalElements());
        model.addAttribute("pageTitle", keyword + " - Search Result");

        model.addAttribute("keyword", keyword);
        model.addAttribute("listResult", listResult);

        return "product/search_result";
    }

    private Customer getAuthenticatedCustomer(HttpServletRequest request) {
        String email = Utility.getEmailOfAuthenticatedCustomer(request);
        return customerService.getCustomerByEmail(email);
    }

}
