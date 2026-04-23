package com.smartcampus.api.resource;

import com.smartcampus.api.exception.RoomNotEmptyException;
import com.smartcampus.api.model.Room;
import com.smartcampus.api.repository.DataStore;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Path("/rooms")
@Produces(MediaType.APPLICATION_JSON)
public class RoomResource {

    private static final Logger LOGGER = Logger.getLogger(RoomResource.class.getName());

    @GET
    public List<Room> getRooms() {
        LOGGER.info("Fetching all rooms.");
        return new ArrayList<>(DataStore.rooms().values());
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createRoom(Room room, @Context UriInfo uriInfo) {
        if (room == null || room.getId() == null || room.getId().isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Room id is required.")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }

        DataStore.rooms().put(room.getId(), room);
        LOGGER.info("Room created: " + room.getId());
        URI location = uriInfo.getAbsolutePathBuilder().path(room.getId()).build();
        return Response.created(location).entity(room).build();
    }

    @GET
    @Path("/{roomId}")
    public Room getRoomById(@PathParam("roomId") String roomId) {
        LOGGER.info("Fetching room: " + roomId);
        Room room = DataStore.rooms().get(roomId);
        if (room == null) {
            throw new NotFoundException("Room not found: " + roomId);
        }
        return room;
    }

    @DELETE
    @Path("/{roomId}")
    public Response deleteRoom(@PathParam("roomId") String roomId) {
        Room room = DataStore.rooms().get(roomId);
        if (room == null) {
            LOGGER.info("Room already absent (idempotent delete): " + roomId);
            return Response.noContent().build();
        }

        if (room.getSensorIds() != null && !room.getSensorIds().isEmpty()) {
            throw new RoomNotEmptyException("Room " + roomId + " cannot be deleted because sensors are still assigned.");
        }

        DataStore.rooms().remove(roomId);
        LOGGER.info("Room deleted: " + roomId);
        return Response.noContent().build();
    }
}
