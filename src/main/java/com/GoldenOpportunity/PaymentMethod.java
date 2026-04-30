package com.GoldenOpportunity;

import com.GoldenOpportunity.Shop.PaymentDetails;

public class PaymentMethod {

    public boolean submitPayment(int guestID, PaymentDetails paymentDetails) {
        // this is a simulation, not checking if the credit card is actually valid
        // all im doing is making sure nothing is missing
        if (guestID <= 0) {
            return false;
        }

        if (paymentDetails == null) {
            return false;
        }

        if (isBlank(paymentDetails.getBillingName())) {
            return false;
        }

        if (isBlank(paymentDetails.getBillingEmail())) {
            return false;
        }

        if (isBlank(paymentDetails.getCardNumber())) {
            return false;
        }

        if (isBlank(paymentDetails.getExpirationDate())) {
            return false;
        }

        if (isBlank(paymentDetails.getCvv())) {
            return false;
        }

        if (!paymentDetails.getBillingEmail().contains("@")) {
            return false;
        }

        String cardNumber = paymentDetails.getCardNumber().replace(" ", "");

        if (cardNumber.length() < 13 || cardNumber.length() > 19) {
            return false;
        }

        if (!cardNumber.matches("\\d+")) {
            return false;
        }

        if (!paymentDetails.getCvv().matches("\\d{3,4}")) {
            return false;
        }

        return true;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
