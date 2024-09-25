package ar.edu.itba.pod.server.repositories;

import ar.edu.itba.pod.grpc.common.Doctor;
import ar.edu.itba.pod.grpc.common.Room;

import java.util.ArrayList;
import java.util.List;

import java.util.Collections; // Añadir esta importación para sincronizar la lista.
import java.util.concurrent.locks.ReentrantLock; // Para usar locks si lo prefieres en lugar de 'synchronized'

public class RoomRepository {

    private final List<Room> rooms;
    private int roomIdCounter = 1;

    public RoomRepository() {
        this.rooms = Collections.synchronizedList(new ArrayList<>());
    }

    public Room addRoom() {
        Room room = Room.newBuilder()
                .setId(roomIdCounter++)
                .setIsEmpty(true)
                .build();
        rooms.add(room);
        return room;
    }

    public boolean isFree(int number) {
        return rooms.get(number - 1).getIsEmpty();
    }

    public Room setOccupied(int number) {
        if (!isFree(number)) {
            return null;
        }
        Room room = Room.newBuilder()
                .setId(number)
                .setIsEmpty(false)
                .build();
        rooms.set(number - 1, room);
        return room;
    }

    public Room setFree(int number) {
        if (isFree(number)) {
            return null;
        }
        Room room = Room.newBuilder()
                .setId(number)
                .setIsEmpty(true)
                .build();
        rooms.set(number - 1, room);
        return room;
    }

    public List<Room> getAllFreeRooms() {
        List<Room> ret = new ArrayList<>();
        for (Room room : rooms) {
            if (room.getIsEmpty()) {
                ret.add(room);
            }
        }
        return ret;
    }

    public List<Room> getRooms() {
        return new ArrayList<>(rooms);
    }
}