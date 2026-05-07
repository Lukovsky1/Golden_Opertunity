package com.GoldenOpportunity.Shop;

public class PaymentDetails {
    private String billingName;
    private String billingEmail;
    private String cardNumber;
    private String expirationDate;
    private String cvv;

    public PaymentDetails(String billingName, String billingEmail, String cardNumber,
                          String expirationDate, String cvv) {
        this.billingName = billingName;
        this.billingEmail = billingEmail;
        this.cardNumber = cardNumber;
        this.expirationDate = expirationDate;
        this.cvv = cvv;
    }

    public String getBillingName() {
        return billingName;
    }

    public String getBillingEmail() {
        return billingEmail;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public String getCvv() {
        return cvv;
    }
}
