package com.bookstore.admin.controller;

import java.util.List;

import com.bookstore.admin.exceptionhandler.ShippingRateAlreadyExistsException;
import com.bookstore.admin.exceptionhandler.ShippingRateNotFoundException;
import com.bookstore.admin.service.ShippingRateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.bookstore.admin.paging.PagingAndSortingHelper;
import com.bookstore.admin.paging.PagingAndSortingParam;
import com.bookstore.entity.City;
import com.bookstore.entity.ShippingRate;

@Controller
public class ShippingRateController {

    private String defaultRedirectURL = "redirect:/shipping_rates/page/1?sortField=city&sortDir=asc";

    @Autowired private ShippingRateService service;

    @GetMapping("/shipping_rates")
    public String listFirstPage() {
        return defaultRedirectURL;
    }

    @GetMapping("/shipping_rates/page/{pageNum}")
    public String listByPage(@PagingAndSortingParam(listName = "shippingRates", moduleURL = "/shipping_rates") PagingAndSortingHelper helper,
                             @PathVariable(name = "pageNum") int pageNum) {
        service.listByPage(pageNum, helper);
        return "shipping_rates/shipping_rates";
    }

    @GetMapping("/shipping_rates/new")
    public String newRate(Model model) {
        List<City> listCities = service.listAllCities();
        model.addAttribute("rate", new ShippingRate());
        model.addAttribute("listCities", listCities);
        model.addAttribute("pageTitle", "New Rate");
        return "shipping_rates/shipping_rate_form";
    }

    @PostMapping("/shipping_rates/save")
    public String saveRate(ShippingRate rate, RedirectAttributes ra) {
        try {
            service.save(rate);
            ra.addFlashAttribute("message", "Phí vận chuyển đã được lưu thành công.");
        } catch (ShippingRateAlreadyExistsException ex) {
            ra.addFlashAttribute("message", ex.getMessage());
        }
        return defaultRedirectURL;
    }

    @GetMapping("/shipping_rates/edit/{id}")
    public String editRate(@PathVariable(name = "id") Integer id,
                           Model model, RedirectAttributes ra) {
        try {
            ShippingRate rate = service.get(id);
            List<City> listCities = service.listAllCities();
            model.addAttribute("listCities", listCities);
            model.addAttribute("rate", rate);
            model.addAttribute("pageTitle", "Chỉnh sửa Rate (ID: " + id + ")");
            return "shipping_rates/shipping_rate_form";
        } catch (ShippingRateNotFoundException ex) {
            ra.addFlashAttribute("message", ex.getMessage());
            return defaultRedirectURL;
        }
    }

    @GetMapping("/shipping_rates/cod/{id}/enabled/{supported}")
    public String updateCODSupport(@PathVariable(name = "id") Integer id,
                                   @PathVariable(name = "supported") Boolean supported,
                                   RedirectAttributes ra) {
        try {
            service.updateCODSupport(id, supported);
            ra.addFlashAttribute("message", "Hỗ trợ COD cho phí vận chuyển ID " + id + " đã được cập nhật!");
        } catch (ShippingRateNotFoundException ex) {
            ra.addFlashAttribute("message", ex.getMessage());
        }
        return defaultRedirectURL;
    }

    @GetMapping("/shipping_rates/delete/{id}")
    public String deleteRate(@PathVariable(name = "id") Integer id,
                             RedirectAttributes ra) {
        try {
            service.delete(id);
            ra.addFlashAttribute("message", "Phí vận chuyển ID " + id + " đã xóa thành công!");
        } catch (ShippingRateNotFoundException ex) {
            ra.addFlashAttribute("message", ex.getMessage());
        }
        return defaultRedirectURL;
    }
}
