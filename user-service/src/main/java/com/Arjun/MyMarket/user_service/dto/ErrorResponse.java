package com.Arjun.MyMarket.user_service.dto;

import java.time.Instant;
import java.util.List;

public record ErrorResponse(
        Instant timeStamp,
        int status,
        String error,
        String message,
        String path,
        List<String> errorDetails
) {
}
