package com.main.tancol.auth.exceptions.records;

import java.time.LocalDateTime;

public record RestErrorResponse(int status, String message, String requestURI, LocalDateTime timestamp) {
    public String toString() {
        return "{\n" +
                "  \"status\": " +status+",\n" +
                "  \"message\": \""+message+"\",\n" +
                "  \"path\": \""+requestURI+"\",\n" +
                "  \"timestamp\": \""+timestamp+"\"\n" +
                "}";
    }


}
