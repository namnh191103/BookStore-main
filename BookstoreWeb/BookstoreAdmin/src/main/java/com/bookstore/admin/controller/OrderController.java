package com.bookstore.admin.controller;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Set;

import com.bookstore.admin.exceptionhandler.OrderNotFoundException;
import com.bookstore.admin.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.bookstore.admin.paging.PagingAndSortingHelper;
import com.bookstore.admin.paging.PagingAndSortingParam;
import com.bookstore.admin.service.ProductService;
import com.bookstore.admin.service.SettingService;
import com.bookstore.entity.City;
import com.bookstore.entity.order.Order;
import com.bookstore.entity.order.OrderDetail;
import com.bookstore.entity.order.OrderStatus;
import com.bookstore.entity.order.OrderTrack;
import com.bookstore.entity.product.Product;
import com.bookstore.entity.setting.Setting;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class OrderController {

	private String defaultRedirectURL = "redirect:/orders/page/1?sortField=orderTime&sortDir=desc";

	@Autowired
	private OrderService orderService;
	@Autowired
	private SettingService settingService;
	@Autowired
	private ProductService productService;

	@GetMapping("/orders")
	public String listFirstPage() {
		return defaultRedirectURL;
	}

	@GetMapping("/orders/page/{pageNum}")
	public String listByPage(
			@PagingAndSortingParam(listName = "listOrders", moduleURL = "/orders") PagingAndSortingHelper helper,
			@PathVariable(name = "pageNum") int pageNum,
			HttpServletRequest request) {

		orderService.listByPage(pageNum, helper);
		loadCurrencySetting(request);

		return "orders/orders";
	}

	private void loadCurrencySetting(HttpServletRequest request) {
		List<Setting> currencySettings = settingService.getCurrencySettings();

		for (Setting setting : currencySettings) {
			request.setAttribute(setting.getKey(), setting.getValue());
		}
	}

	@GetMapping("/orders/detail/{id}")
	public String viewOrderDetails(@PathVariable("id") Integer id, Model model,
			RedirectAttributes ra, HttpServletRequest request) {
		try {
			Order order = orderService.get(id);
			loadCurrencySetting(request);
			model.addAttribute("order", order);

			return "orders/order_details_modal";
		} catch (OrderNotFoundException ex) {
			ra.addFlashAttribute("message", ex.getMessage());
			return defaultRedirectURL;
		}

	}

	@GetMapping("/orders/delete/{id}")
	public String deleteOrder(@PathVariable("id") Integer id, Model model, RedirectAttributes ra) {
		try {
			orderService.delete(id);
			;
			ra.addFlashAttribute("message", "Mã đơn hàng " + id + " xóa thành công!.");
		} catch (OrderNotFoundException ex) {
			ra.addFlashAttribute("message", ex.getMessage());
		}

		return defaultRedirectURL;
	}

	@GetMapping("/orders/edit/{id}")
	public String editOrder(@PathVariable("id") Integer id, Model model, RedirectAttributes ra,
			HttpServletRequest request) {
		try {
			Order order = orderService.get(id);
			;

			List<City> listCities = orderService.listAllCities();

			model.addAttribute("pageTitle", "Chỉnh sửa đơn hàng (ID: " + id + ")");
			model.addAttribute("order", order);
			model.addAttribute("listCities", listCities);

			return "orders/order_form";

		} catch (OrderNotFoundException ex) {
			ra.addFlashAttribute("message", ex.getMessage());
			return defaultRedirectURL;
		}

	}

	@PostMapping("/order/save")
	public String saveOrder(Order order, HttpServletRequest request, RedirectAttributes ra) {
		String cityName = request.getParameter("cityName");
		order.setCity(cityName);

		updateProductDetails(order, request);
		updateOrderTracks(order, request);

		// Restore quantities if any track status is RETURNED
		if (order.getOrderTracks().stream().anyMatch(track -> track.getStatus() == OrderStatus.RETURNED)) {
			restoreProductQuantities(order);
		}

		orderService.save(order);

		ra.addFlashAttribute("message", "Đơn hàng " + order.getId() + " cập nhật thành công");

		return defaultRedirectURL;
	}

	public void restoreProductQuantities(Order order) {
		System.out.println("Khôi phục số lượng cho ID đơn hàng: " + order.getId());
		for (OrderDetail detail : order.getOrderDetails()) {
			if (order.getOrderTracks().stream().anyMatch(track -> track.getStatus() == OrderStatus.RETURNED)) {
				Product product = detail.getProduct();
				int quantityToRestore = detail.getQuantity();
	
				// Log product and quantity
				System.out.println("Khôi phục số lượng cho ID sản phẩm: " + product.getId() + " by " + quantityToRestore);
	
				// Ensure that the product is retrieved from the database to reflect the latest state
				product = productService.findById(product.getId());
				product.setQuantity(product.getQuantity() + quantityToRestore);
				productService.save(product); // Save the updated product quantity
	
				// Log successful update
				System.out.println("Sản Phẩm ID: " + product.getId() + " số lượng cập nhật đến " + product.getQuantity());
			}
		}
	}
	

	private void updateOrderTracks(Order order, HttpServletRequest request) {
		String[] trackIds = request.getParameterValues("trackId");
		String[] trackStatuses = request.getParameterValues("trackStatus");
		String[] trackDates = request.getParameterValues("trackDate");
		String[] trackNotes = request.getParameterValues("trackNotes");

		List<OrderTrack> orderTracks = order.getOrderTracks();
		DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");

		for (int i = 0; i < trackIds.length; i++) {
			OrderTrack trackRecord = new OrderTrack();

			Integer trackId = Integer.parseInt(trackIds[i]);
			if (trackId > 0) {
				trackRecord.setId(trackId);
			}

			trackRecord.setOrder(order);
			trackRecord.setStatus(OrderStatus.valueOf(trackStatuses[i]));
			trackRecord.setNotes(trackNotes[i]);

			try {
				trackRecord.setUpdatedTime(dateFormatter.parse(trackDates[i]));
			} catch (ParseException e) {
				e.printStackTrace();
			}

			orderTracks.add(trackRecord);
		}
	}

	private void updateProductDetails(Order order, HttpServletRequest request) {
		String[] detailIds = request.getParameterValues("detailId");
		String[] productIds = request.getParameterValues("productId");
		String[] productPrices = request.getParameterValues("productPrice");
		String[] productDetailCosts = request.getParameterValues("productDetailCost");
		String[] quantities = request.getParameterValues("quantity");
		String[] productSubtotals = request.getParameterValues("productSubtotal");

		Set<OrderDetail> orderDetails = order.getOrderDetails();

		for (int i = 0; i < detailIds.length; i++) {
			System.out.println("Detail ID: " + detailIds[i]);
			System.out.println("\t Prodouct ID: " + productIds[i]);
			System.out.println("\t Cost: " + productDetailCosts[i]);
			System.out.println("\t Quantity: " + quantities[i]);
			System.out.println("\t Subtotal: " + productSubtotals[i]);

			OrderDetail orderDetail = new OrderDetail();
			Integer detailId = Integer.parseInt(detailIds[i]);
			if (detailId > 0) {
				orderDetail.setId(detailId);
			}

			orderDetail.setOrder(order);
			orderDetail.setProduct(new Product(Integer.parseInt(productIds[i])));
			orderDetail.setProductCost(Float.parseFloat(productDetailCosts[i]));
			orderDetail.setSubtotal(Float.parseFloat(productSubtotals[i]));
			orderDetail.setQuantity(Integer.parseInt(quantities[i]));
			orderDetail.setUnitPrice(Float.parseFloat(productPrices[i]));

			orderDetails.add(orderDetail);

		}

	}

}
