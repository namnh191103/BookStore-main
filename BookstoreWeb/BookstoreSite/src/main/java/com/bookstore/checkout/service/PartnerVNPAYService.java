package com.bookstore.checkout.service;

import jakarta.servlet.http.HttpServletRequest;

public interface PartnerVNPAYService {
    String doInitial(int totalAmount, String orderInfo, String callbackUrl);
    int paymentStatus();

}
