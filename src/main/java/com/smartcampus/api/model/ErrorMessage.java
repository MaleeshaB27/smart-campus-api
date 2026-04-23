package com.smartcampus.api.model;

import java.util.logging.Logger;

public class ErrorMessage {

    private static final Logger LOGGER = Logger.getLogger(ErrorMessage.class.getName());

    private int status;
    private String code;
    private String message;

    public ErrorMessage() {
        LOGGER.fine("ErrorMessage created using default constructor.");
    }

    public ErrorMessage(int status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
        LOGGER.fine("ErrorMessage created with status: " + status + ", code: " + code);
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
        LOGGER.fine("ErrorMessage status updated: " + status);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
        LOGGER.fine("ErrorMessage code updated.");
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
        LOGGER.fine("ErrorMessage message updated.");
    }
}
