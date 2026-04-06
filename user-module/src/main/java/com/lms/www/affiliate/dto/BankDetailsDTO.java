package com.lms.www.affiliate.dto;

import lombok.Data;

@Data
public class BankDetailsDTO {
    private String bankName;
    private String accountNumber;
    private String ifscCode;
    private String accountHolderName;
    private String upiId;
}
