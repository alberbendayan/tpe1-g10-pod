package ar.edu.itba.pod.server.repositories;

import ar.edu.itba.pod.grpc.common.Doctor;
import ar.edu.itba.pod.grpc.common.Room;

import java.util.ArrayList;
import java.util.List;

public class RoomRepository {

    private List<Room> rooms;
    private int roomIdCounter = 1;

    public RoomRepository() {
        this.rooms = new ArrayList<>();
    }
    public Room addRoom() {
        Room room = Room.newBuilder()
                .setId(roomIdCounter++)
                .setIsEmpty(true)
                .build();
        rooms.add(room);
        return room;
    }

}


