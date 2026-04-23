package com.smartcampus.api.repository;

import com.smartcampus.api.model.Room;
import com.smartcampus.api.model.Sensor;
import com.smartcampus.api.model.SensorReading;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public final class DataStore {

    private static final Logger LOGGER = Logger.getLogger(DataStore.class.getName());

    private static final Map<String, Room> ROOMS = new ConcurrentHashMap<>();
    private static final Map<String, Sensor> SENSORS = new ConcurrentHashMap<>();
    private static final Map<String, List<SensorReading>> SENSOR_READINGS = new ConcurrentHashMap<>();

    private DataStore() {
        LOGGER.fine("DataStore utility initialized.");
    }

    public static Map<String, Room> rooms() {
        return ROOMS;
    }

    public static Map<String, Sensor> sensors() {
        return SENSORS;
    }

    public static List<SensorReading> readingsBySensor(String sensorId) {
        return SENSOR_READINGS.computeIfAbsent(sensorId, id -> {
            LOGGER.fine("Initializing readings list for sensor: " + id);
            return new ArrayList<>();
        });
    }
}
