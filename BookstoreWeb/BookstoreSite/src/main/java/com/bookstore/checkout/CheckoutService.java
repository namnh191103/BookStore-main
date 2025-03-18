package com.bookstore.checkout;

import java.util.List;

import org.springframework.stereotype.Service;

import com.bookstore.entity.CartItem;
import com.bookstore.entity.product.Product;
import com.bookstore.entity.ShippingRate;

@Service
public class CheckoutService {
	private static final int DIM_DIVISOR = 139;

	public CheckoutInfo prepareCheckout(List<CartItem> cartItems, ShippingRate shippingRate) {
		CheckoutInfo checkoutInfo = new CheckoutInfo();
		
		float productCost = calculateProductCost(cartItems);
		float productTotal = calculateProductTotal(cartItems);
		float shippingCostTotal = calculateShippingCost(cartItems, shippingRate);
		float paymentTotal = productTotal + shippingCostTotal;
		
		checkoutInfo.setProductCost(productCost);
		checkoutInfo.setProductTotal(productTotal);
		checkoutInfo.setShippingCostTotal(shippingCostTotal);
		checkoutInfo.setPaymentTotal(paymentTotal);
		
		checkoutInfo.setDeliverDays(shippingRate.getDays());
		checkoutInfo.setCodSupported(shippingRate.isCodSupported());
		
		return checkoutInfo;
	}

	private float calculateShippingCost(List<CartItem> cartItems, ShippingRate shippingRate) {
		float totalWeight = 0.0f;
		
		// Tính tổng trọng lượng của tất cả sản phẩm trong giỏ hàng
		for (CartItem item : cartItems) {
			Product product = item.getProduct();
			float finalWeight = product.getWeight(); // Sử dụng trọng lượng sản phẩm trực tiếp
			totalWeight += finalWeight * item.getQuantity();
		}
		
		totalWeight = totalWeight / 1000; // Chuyển đổi trọng lượng từ gram sang kilogram
		
		// Tính chi phí vận chuyển dựa trên tổng trọng lượng
		float shippingCost;
		float baseRate = shippingRate.getRate(); // Giá trị 20.000 đồng cho 2 kg đầu tiên
		if (totalWeight <= 2) {
			shippingCost = baseRate; // Chi phí cố định cho 2 kg đầu tiên
		} else {
			shippingCost = baseRate + 2000 * (totalWeight - 2); // Phụ thu 2.000 đồng cho mỗi kg tiếp theo
		}
		
		// Gán chi phí vận chuyển này cho từng mục hàng
		for (CartItem item : cartItems) {
			item.setShippingCost(shippingCost);
		}
		
		return shippingCost;
	}
	

	private float calculateProductTotal(List<CartItem> cartItems) {
		float total = 0.0f;
		
		for (CartItem item : cartItems) {
			total += item.getSubtotal();
		}
		
		return total;
	}

	private float calculateProductCost(List<CartItem> cartItems) {
		float cost = 0.0f;
		
		for (CartItem item : cartItems) {
			cost += item.getQuantity() * item.getProduct().getCost();
		}
		
		return cost;
	}
}