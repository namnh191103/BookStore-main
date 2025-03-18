package com.bookstore.entity.order;

public enum OrderStatus {
	
	NEW {
		@Override
		public String defaultDescription() {
			return "Đơn hàng đã được khách hàng đặt";
		}
		
	}, 
	
	CANCELLED {
		@Override
		public String defaultDescription() {
			return "Đơn hàng đã bị từ chối";
		}
	}, 
	
	PROCESSING {
		@Override
		public String defaultDescription() {
			return "Đơn hàng đang được xử lý";
		}
	},
	
	PACKAGED {
		@Override
		public String defaultDescription() {
			return "Sản phẩm đã được đóng gói";
		}		
	}, 
	
	PICKED {
		@Override
		public String defaultDescription() {
			return "Shipper đã nhận gói hàng";
		}		
	}, 
	
	SHIPPING {
		@Override
		public String defaultDescription() {
			return "Shipper đang giao hàng";
		}		
	},
	
	DELIVERED {
		@Override
		public String defaultDescription() {
			return "Khách hàng đã nhận được sản phẩm";
		}		
	}, 
	
	RETURNED {
		@Override
		public String defaultDescription() {
			return "Sản phẩm đã được hoàn trả";
		}		
	}, 
	
	RETURN_REQUESTED {
		@Override
		public String defaultDescription() {
			return "Khách hàng đã gửi yêu cầu trả lại hàng đã mua";
		}		
	},
	
	PAID {
		@Override
		public String defaultDescription() {
			return "Khách hàng đã thanh toán đơn hàng này";
		}		
	}, 
	
	REFUNDED {
		@Override
		public String defaultDescription() {
			return "Khách hàng đã được hoàn tiền";
		}		
	};
	
	public abstract String defaultDescription();
}
