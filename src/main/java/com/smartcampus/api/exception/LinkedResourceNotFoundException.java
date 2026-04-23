package com.smartcampus.api.exception;

import java.util.logging.Logger;

public class LinkedResourceNotFoundException extends RuntimeException {

    private static final Logger LOGGER = Logger.getLogger(LinkedResourceNotFoundException.class.getName());

    public LinkedResourceNotFoundException(String message) {
        super(message);
        LOGGER.warning("LinkedResourceNotFoundException: " + message);
    }
}
