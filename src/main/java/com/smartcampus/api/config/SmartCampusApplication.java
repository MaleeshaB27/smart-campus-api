package com.smartcampus.api.config;

import com.smartcampus.api.exception.GenericExceptionMapper;
import com.smartcampus.api.exception.LinkedResourceNotFoundExceptionMapper;
import com.smartcampus.api.exception.NotFoundExceptionMapper;
import com.smartcampus.api.exception.RoomNotEmptyExceptionMapper;
import com.smartcampus.api.exception.SensorUnavailableExceptionMapper;
import com.smartcampus.api.filter.ApiLoggingFilter;
import com.smartcampus.api.resource.DiscoveryResource;
import com.smartcampus.api.resource.RoomResource;
import com.smartcampus.api.resource.SensorResource;
import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.logging.Logger;

@ApplicationPath("/api/v1")
public class SmartCampusApplication extends Application {

    private static final Logger LOGGER = Logger.getLogger(SmartCampusApplication.class.getName());
    private final Set<Class<?>> classes = new HashSet<>();

    public SmartCampusApplication() {
        classes.add(DiscoveryResource.class);
        classes.add(RoomResource.class);
        classes.add(SensorResource.class);

        classes.add(ApiLoggingFilter.class);

        classes.add(RoomNotEmptyExceptionMapper.class);
        classes.add(LinkedResourceNotFoundExceptionMapper.class);
        classes.add(SensorUnavailableExceptionMapper.class);
        classes.add(NotFoundExceptionMapper.class);
        classes.add(GenericExceptionMapper.class);

        LOGGER.info("SmartCampusApplication initialized.");
    }

    @Override
    public Set<Class<?>> getClasses() {
        return classes;
    }
}
