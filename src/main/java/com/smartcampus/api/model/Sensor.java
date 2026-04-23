package com.smartcampus.api.model;

import java.util.logging.Logger;

public class Sensor {

    private static final Logger LOGGER = Logger.getLogger(Sensor.class.getName());

    private String id;
    private String type;
    private String status;
    private double currentValue;
    private String roomId;

    public Sensor() {
        LOGGER.fine("Sensor created using default constructor.");
    }

    public Sensor(String id, String type, String status, double currentValue, String roomId) {
        this.id = id;
        this.type = type;
        this.status = status;
        this.currentValue = currentValue;
        this.roomId = roomId;
        LOGGER.fine("Sensor created with id: " + id);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
        LOGGER.fine("Sensor id updated: " + id);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
        LOGGER.fine("Sensor type updated for id: " + id);
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
        LOGGER.fine("Sensor status updated for id: " + id);
    }

    public double getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(double currentValue) {
        this.currentValue = currentValue;
        LOGGER.fine("Sensor currentValue updated for id: " + id);
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
        LOGGER.fine("Sensor roomId updated for id: " + id);
    }
}
