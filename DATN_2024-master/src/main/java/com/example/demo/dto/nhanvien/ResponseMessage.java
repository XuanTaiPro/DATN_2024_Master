package com.example.demo.dto.nhanvien;

public class ResponseMessage {
    private String message;

    // Constructor
    public ResponseMessage(String message) {
        this.message = message;
    }

    // Getter và Setter
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
