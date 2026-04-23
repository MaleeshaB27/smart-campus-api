package com.smartcampus.api.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class DiscoveryResource {

    private static final Logger LOGGER = Logger.getLogger(DiscoveryResource.class.getName());

    @GET
    public Map<String, Object> getDiscovery() {
        LOGGER.info("Serving API discovery document.");
        Map<String, Object> discovery = new LinkedHashMap<>();
        discovery.put("apiName", "Smart Campus Sensor & Room Management API");
        discovery.put("version", "v1");
        discovery.put("contact", "campus-facilities@westminster.example");

        Map<String, String> resources = new LinkedHashMap<>();
        resources.put("rooms", "/api/v1/rooms");
        resources.put("sensors", "/api/v1/sensors");
        resources.put("sensorReadingsTemplate", "/api/v1/sensors/{sensorId}/readings");

        discovery.put("resources", resources);
        return discovery;
    }
}
