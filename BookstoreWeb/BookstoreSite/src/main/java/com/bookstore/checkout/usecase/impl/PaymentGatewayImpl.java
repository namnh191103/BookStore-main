package com.bookstore.checkout.usecase.impl;

import com.bookstore.checkout.model.Status;
import com.bookstore.checkout.model.response.InitiateTransactionResponse;
import com.bookstore.checkout.model.response.PaymentStatusResponse;
import com.bookstore.checkout.service.PartnerVNPAYService;
import com.bookstore.checkout.usecase.PaymentGateway;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentGatewayImpl implements PaymentGateway {
    @Autowired
    private PartnerVNPAYService partnerVNPAYService;

    @Autowired
    private HttpServletRequest request;

    @Override
    public InitiateTransactionResponse initateTransaction(int totalAmount, String orderInfo) {
        String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
        String paymentUrl = partnerVNPAYService.doInitial(totalAmount, orderInfo, baseUrl);

        return InitiateTransactionResponse.builder()
                .paymentUrl(paymentUrl)
                .build();
    }

    @Override
    public PaymentStatusResponse paymentStatus() {
        int paymentStatus = partnerVNPAYService.paymentStatus();

        String orderInfo = request.getParameter("vnp_OrderInfo");
        String paymentTime = request.getParameter("vnp_PayDate");
        String transactionId = request.getParameter("vnp_TransactionNo");
        String totalPrice = request.getParameter("vnp_Amount");

        return PaymentStatusResponse.builder()
                .status(paymentStatus == 1 ? Status.SUCCESS : Status.FAIL)
                .build();
    }
}
