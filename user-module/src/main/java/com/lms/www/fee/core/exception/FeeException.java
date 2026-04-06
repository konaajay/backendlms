package com.lms.www.fee.core.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class FeeException extends RuntimeException {
    private final HttpStatus status;

    public FeeException(String message) {
        super(message);
        this.status = HttpStatus.BAD_REQUEST;
    }

    public FeeException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
}
