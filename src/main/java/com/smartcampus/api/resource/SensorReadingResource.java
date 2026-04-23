package com.smartcampus.api.resource;

import com.smartcampus.api.exception.SensorUnavailableException;
import com.smartcampus.api.model.Sensor;
import com.smartcampus.api.model.SensorReading;
import com.smartcampus.api.repository.DataStore;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

@Produces(MediaType.APPLICATION_JSON)
public class SensorReadingResource {

    private static final Logger LOGGER = Logger.getLogger(SensorReadingResource.class.getName());

    private final String sensorId;

    public SensorReadingResource(String sensorId) {
        this.sensorId = sensorId;
        LOGGER.fine("SensorReadingResource created for sensor: " + sensorId);
    }

    @GET
    public List<SensorReading> getReadings() {
        LOGGER.info("Fetching readings for sensor: " + sensorId);
        return DataStore.readingsBySensor(sensorId);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addReading(SensorReading reading) {
        if (reading == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Sensor reading payload is required.")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }

        Sensor sensor = DataStore.sensors().get(sensorId);
        if (sensor == null) {
            throw new NotFoundException("Sensor not found: " + sensorId);
        }
        if ("MAINTENANCE".equalsIgnoreCase(sensor.getStatus())) {
            throw new SensorUnavailableException("Sensor " + sensorId + " is in MAINTENANCE and cannot accept readings.");
        }

        if (reading.getId() == null || reading.getId().isBlank()) {
            reading.setId(UUID.randomUUID().toString());
        }
        if (reading.getTimestamp() == 0L) {
            reading.setTimestamp(System.currentTimeMillis());
        }

        DataStore.readingsBySensor(sensorId).add(reading);
        sensor.setCurrentValue(reading.getValue());
        LOGGER.info("Added reading " + reading.getId() + " for sensor " + sensorId);
        return Response.status(Response.Status.CREATED).entity(reading).build();
    }
}
