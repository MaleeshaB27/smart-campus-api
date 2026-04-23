package com.smartcampus.api.model;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class Room {

    private static final Logger LOGGER = Logger.getLogger(Room.class.getName());

    private String id;
    private String name;
    private int capacity;
    private List<String> sensorIds = new ArrayList<>();

    public Room() {
        LOGGER.fine("Room created using default constructor.");
    }

    public Room(String id, String name, int capacity) {
        this.id = id;
        this.name = name;
        this.capacity = capacity;
        LOGGER.fine("Room created with id: " + id);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
        LOGGER.fine("Room id updated: " + id);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        LOGGER.fine("Room name updated for id: " + id);
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
        LOGGER.fine("Room capacity updated for id: " + id);
    }

    public List<String> getSensorIds() {
        return sensorIds;
    }

    public void setSensorIds(List<String> sensorIds) {
        this.sensorIds = sensorIds == null ? new ArrayList<>() : sensorIds;
        LOGGER.fine("Room sensorIds list updated for id: " + id);
    }
}
