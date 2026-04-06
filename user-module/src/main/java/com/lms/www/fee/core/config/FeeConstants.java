package com.lms.www.fee.core.config;

public class FeeConstants {
    public static final String CURRENCY_INR = "INR";
    public static final String GATEWAY_CASHFREE = "CASHFREE";
    
    public static final String ERROR_ALLOCATION_NOT_FOUND = "Fee allocation not found";
    public static final String ERROR_INSTALLMENT_NOT_FOUND = "Installment plan not found";
    public static final String ERROR_PAYMENT_NOT_FOUND = "Payment record not found";
    public static final String ERROR_UNAUTHORIZED_ACCESS = "You are not authorized to access this resource";
    public static final String ERROR_INSUFFICIENT_BALANCE = "Insufficient balance for this payment";
    public static final String ERROR_INVALID_AMOUNT = "Payment amount must be greater than zero";
    
    public static final String WEBHOOK_STATUS_SUCCESS = "SUCCESS";
    public static final String WEBHOOK_STATUS_PAID = "PAID";
    
    public static final String ORDER_PREFIX_NORMAL = "ORD-";
    public static final String ORDER_PREFIX_EARLY = "EP-";
    public static final String ORDER_PREFIX_MANUAL = "MAN-";
}
