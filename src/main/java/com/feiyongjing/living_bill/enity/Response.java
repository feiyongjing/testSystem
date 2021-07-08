package com.feiyongjing.living_bill.enity;

public class Response<T> {
    int statusCode;
    String message;
    T date;

    public static <T> Response<T> of(int statusCode, String message, T date){
        return new Response<T>(statusCode,message,date);
    }

    public Response() {
    }

    private Response(int statusCode, String message, T date) {
        this.statusCode = statusCode;
        this.message = message;
        this.date = date;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getDate() {
        return date;
    }

    public void setDate(T date) {
        this.date = date;
    }
}
