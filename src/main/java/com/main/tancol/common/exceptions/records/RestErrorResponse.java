package com.main.tancol.common.exceptions.records;

import java.time.LocalDateTime;

public record RestErrorResponse(int status, String message, String requestURI, LocalDateTime timestamp) {
    @Override
    public String toString() {
        return "{\"status\":" + status
                + ",\"message\":\"" + escape(message)
                + "\",\"path\":\"" + escape(requestURI)
                + "\",\"timestamp\":\"" + timestamp + "\"}";
    }

    private static String escape(String value) {
        if (value == null) {
            return "null";
        }
        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\r", "\\r")
                .replace("\n", "\\n")
                .replace("\t", "\\t");
    }
}
