package com.smartcampus.api.resource;

import com.smartcampus.api.exception.LinkedResourceNotFoundException;
import com.smartcampus.api.model.Room;
import com.smartcampus.api.model.Sensor;
import com.smartcampus.api.repository.DataStore;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

@Path("/sensors")
@Produces(MediaType.APPLICATION_JSON)
public class SensorResource {

    private static final Logger LOGGER = Logger.getLogger(SensorResource.class.getName());

    @GET
    public List<Sensor> getSensors(@QueryParam("type") String type) {
        LOGGER.info("Fetching sensors, filter type: " + type);
        List<Sensor> sensors = new ArrayList<>(DataStore.sensors().values());
        if (type == null || type.isBlank()) {
            return sensors;
        }
        return sensors.stream()
                .filter(sensor -> Objects.equals(type, sensor.getType()))
                .toList();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createSensor(Sensor sensor) {
        if (sensor == null || sensor.getId() == null || sensor.getId().isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Sensor id is required.")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }

        Room room = DataStore.rooms().get(sensor.getRoomId());
        if (room == null) {
            throw new LinkedResourceNotFoundException("Cannot create sensor. Room does not exist: " + sensor.getRoomId());
        }

        DataStore.sensors().put(sensor.getId(), sensor);
        room.getSensorIds().add(sensor.getId());
        LOGGER.info("Sensor created: " + sensor.getId() + " linked to room " + sensor.getRoomId());
        return Response.status(Response.Status.CREATED).entity(sensor).build();
    }

    @GET
    @Path("/{sensorId}")
    public Sensor getSensorById(@PathParam("sensorId") String sensorId) {
        Sensor sensor = DataStore.sensors().get(sensorId);
        if (sensor == null) {
            throw new NotFoundException("Sensor not found: " + sensorId);
        }
        LOGGER.info("Sensor fetched: " + sensorId);
        return sensor;
    }

    @Path("/{sensorId}/readings")
    public SensorReadingResource getSensorReadingResource(@PathParam("sensorId") String sensorId) {
        LOGGER.info("Resolving sub-resource for sensor readings: " + sensorId);
        if (!DataStore.sensors().containsKey(sensorId)) {
            throw new NotFoundException("Sensor not found: " + sensorId);
        }
        return new SensorReadingResource(sensorId);
    }
}
