package ar.edu.itba.pod.server.repositories;

import ar.edu.itba.pod.grpc.administrationService.Room;

import java.util.ArrayList;
import java.util.List;

public class RoomRepository {

    private List<Room> rooms;

    public RoomRepository() {
        this.rooms = new ArrayList<>();
    }

    public Room addRoom() {
        Room room = Room.newBuilder().build();
        rooms.add(room);
        return room;
    }
}


