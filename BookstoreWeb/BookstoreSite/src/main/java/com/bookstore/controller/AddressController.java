package com.bookstore.controller;

import java.util.List;

import com.bookstore.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.bookstore.config.Utility;
import com.bookstore.service.CustomerService;
import com.bookstore.entity.Address;
import com.bookstore.entity.City;
import com.bookstore.entity.Customer;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class AddressController {

    @Autowired private AddressService addressService;
    @Autowired private CustomerService customerService;

    @GetMapping("/address_book")
    public String showAddressBook(Model model, HttpServletRequest request) {
        Customer customer = getAuthenticatedCustomer(request); 
        List<Address> listAddresses = addressService.listAddressBook(customer); 

        boolean usePrimaryAddressAsDefault = true; 
        for (Address address : listAddresses) {
            if (address.isDefaultForShipping()) {
                usePrimaryAddressAsDefault = false; 
                break; 
            }
        }

        model.addAttribute("listAddresses", listAddresses); 
        model.addAttribute("customer", customer); 
        model.addAttribute("usePrimaryAddressAsDefault", usePrimaryAddressAsDefault);  
        return "address_book/addresses"; 
    }

    private Customer getAuthenticatedCustomer(HttpServletRequest request){
        String email = Utility.getEmailOfAuthenticatedCustomer(request);
        return customerService.getCustomerByEmail(email);
    }

    @GetMapping("/address_book/new")
	public String newAddress(Model model) {
		List<City> listCities = customerService.listAllCity();
		
		model.addAttribute("listCities", listCities);
		model.addAttribute("address", new Address());
		model.addAttribute("pageTitle", "Thêm địa chỉ giao hàng mới");
		
		return "address_book/address_form";
	}

    @PostMapping("/address_book/save")
	public String saveAddress(Address address, HttpServletRequest request, RedirectAttributes ra) {
		Customer customer = getAuthenticatedCustomer(request);
		
		address.setCustomer(customer);
		addressService.save(address);

		String redirectOption = request.getParameter("redirect");
		String redirectURL = "redirect:/address_book";
		
		if ("checkout".equals(redirectOption)) {
			redirectURL += "?redirect=checkout";
		}
		
		ra.addFlashAttribute("message", "Lưu địa chỉ giao hàng thành công.");
		
		return redirectURL;
	}

    @GetMapping("/address_book/edit/{id}")
	public String editAddress(@PathVariable("id") Integer addressId, Model model,
			HttpServletRequest request) {
		Customer customer = getAuthenticatedCustomer(request);
		List<City> listCities = customerService.listAllCity();
		
		Address address = addressService.get(addressId, customer.getId());

		model.addAttribute("address", address);
		model.addAttribute("listCities", listCities);
		model.addAttribute("pageTitle", "Chỉnh sửa địa chỉ (ID: " + addressId + ")");
		
		return "address_book/address_form";
	}

    @GetMapping("/address_book/delete/{id}")
	public String deleteAddress(@PathVariable("id") Integer addressId, RedirectAttributes ra,
			HttpServletRequest request) {
		Customer customer = getAuthenticatedCustomer(request);
		addressService.delete(addressId, customer.getId());
		
		ra.addFlashAttribute("message", "Địa chỉ giao hàng ID " + addressId + " xóa thành công.");
		
		return "redirect:/address_book";
	}

	
	@GetMapping("/address_book/default/{id}")
	public String setDefaultAddress(@PathVariable("id") Integer addressId,
			HttpServletRequest request) {
		Customer customer = getAuthenticatedCustomer(request);
		addressService.setDefaultAddress(addressId, customer.getId());
		
		String redirectOption = request.getParameter("redirect");
		String redirectURL = "redirect:/address_book";
		
		if ("cart".equals(redirectOption)) {
			redirectURL = "redirect:/cart";
		} else if ("checkout".equals(redirectOption)) {
			redirectURL = "redirect:/checkout";
		}
		
		return redirectURL; 
	}
}
