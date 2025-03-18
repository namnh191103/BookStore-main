package com.bookstore.checkout.usecase;

import com.bookstore.checkout.model.response.InitiateTransactionResponse;
import com.bookstore.checkout.model.response.PaymentStatusResponse;

public interface PaymentGateway {
    InitiateTransactionResponse initateTransaction(int totalAmount, String orderInfo);

    PaymentStatusResponse paymentStatus();

}
