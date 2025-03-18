package com.bookstore.entity;

import java.io.Serializable;

public class PaymentResDTO implements Serializable {
    private String status;
    private String message;
    private String URL;

    // Default constructor
    public PaymentResDTO() {

    }

    // Constructor with fields
    public PaymentResDTO(String status, String message, String URL) {
        this.status = status;
        this.message = message;
        this.URL = URL;
    }

    // Getter and Setter for status
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // Getter and Setter for message
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    // Getter and Setter for URL
    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }
}
