package com.GoldenOpportunity;

public class PaymentMethod {

    // minimal for now, used for valid store purchase use case
    public boolean submitPayment(int guestID, String paymentDetails) {
        return paymentDetails != null && !paymentDetails.isBlank();
    }

}
