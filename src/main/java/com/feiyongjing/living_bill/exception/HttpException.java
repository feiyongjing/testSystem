package com.feiyongjing.living_bill.exception;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

public class HttpException extends RuntimeException {
    int statusCode;
    String message;

    private HttpException(String message, int statusCode) {
        this.message = message;
        this.statusCode = statusCode;
    }

    public static HttpException preconditionFailed(String message) {
        return new HttpException(message, BAD_REQUEST.value());
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
