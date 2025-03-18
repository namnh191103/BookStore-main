package com.bookstore.checkout.model.response;

import com.bookstore.checkout.model.Status;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentStatusResponse {
    private Status status;
}
