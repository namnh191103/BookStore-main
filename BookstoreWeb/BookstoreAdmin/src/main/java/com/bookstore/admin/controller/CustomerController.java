package com.bookstore.admin.controller;

import java.util.List;

import com.bookstore.admin.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.bookstore.entity.City;
import com.bookstore.entity.Customer;
import com.bookstore.exception.CustomerNotFoundException;

@Controller
public class CustomerController {

    @Autowired private CustomerService customerService;

    @GetMapping("/customers")
	public String listFirstPage(Model model) {
		return listByPage(model, 1, "firstName", "asc", null);
	}

    @GetMapping("/customers/page/{pageNum}")
	public String listByPage(Model model, 
						@PathVariable(name = "pageNum") int pageNum,
						@Param("sortField") String sortField,
						@Param("sortDir") String sortDir,
						@Param("keyword") String keyword
			) {
		
		Page<Customer> page = customerService.listByPage(pageNum, sortField, sortDir, keyword);
		List<Customer> listCustomers = page.getContent();
		
		long startCount = (pageNum - 1) * CustomerService.CUSTOMERS_PER_PAGE + 1;
		model.addAttribute("startCount", startCount);
		
		long endCount = startCount + CustomerService.CUSTOMERS_PER_PAGE - 1;
		if (endCount > page.getTotalElements()) {
			endCount = page.getTotalElements();
		}
		
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("totalItems", page.getTotalElements());
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("listCustomers", listCustomers);
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("keyword", keyword);
		model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
		model.addAttribute("endCount", endCount);
		
		return "customers/customers";
	}

    @GetMapping("/customers/{id}/enabled/{status}")
	public String updateCustomerEnabledStatus(@PathVariable("id") Integer id,
			@PathVariable("status") boolean enabled, RedirectAttributes redirectAttributes) {
		customerService.updateCustomerEnabledStatus(id, enabled);
		String status = enabled ? "kích hoạt" : "hủy kích hoạt";
		String message = "Tài khoản khách hàng id: " + id + " đã " + status;
		redirectAttributes.addFlashAttribute("message", message);
		
		return "redirect:/customers";
	}	

    @GetMapping("/customers/detail/{id}")
	public String viewCustomer(@PathVariable("id") Integer id, Model model, RedirectAttributes ra) {
		try {
			Customer customer = customerService.get(id);
			model.addAttribute("customer", customer);
			
			return "customers/customer_detail_modal";
		} catch (CustomerNotFoundException ex) {
			ra.addFlashAttribute("message", ex.getMessage());
			return "redirect:/customers";			
		}
	}

    @GetMapping("/customers/edit/{id}")
	public String editCustomer(@PathVariable("id") Integer id, Model model, RedirectAttributes ra) {
		try {
			Customer customer = customerService.get(id);
			List<City> cities = customerService.listAllCities();
			
			model.addAttribute("listCities", cities);			
			model.addAttribute("customer", customer);
			model.addAttribute("pageTitle", String.format("Chỉnh sửa khách hàng (ID: %d)", id));
			
			return "customers/customer_form";
			
		} catch (CustomerNotFoundException ex) {
			ra.addFlashAttribute("message", ex.getMessage());
			return "redirect:/customers";
		}
	}

    @PostMapping("/customers/save")
	public String saveCustomer(Customer customer, Model model, RedirectAttributes ra) {
		customerService.save(customer);
		ra.addFlashAttribute("message", "Khách hàng Id: " + customer.getId() + " cập nhật thành công.");
		return "redirect:/customers";
	}

    @GetMapping("/customers/delete/{id}")
	public String deleteCustomer(@PathVariable Integer id, RedirectAttributes ra) {
		try {
			customerService.delete(id);			
			ra.addFlashAttribute("message", "Khách hàng ID " + id + " xóa thành công.");
			
		} catch (CustomerNotFoundException ex) {
			ra.addFlashAttribute("message", ex.getMessage());
		}
		
		return "redirect:/customers";
	}


}
