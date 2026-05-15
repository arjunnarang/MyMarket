package com.Arjun.MyMarket.product.exception;

public record ApiResponse(
        String message,
        int code,
        String name
) {

    public static ApiResponse create(String message, int code, String name){
        return new ApiResponse(message, code, name);
    }
}
