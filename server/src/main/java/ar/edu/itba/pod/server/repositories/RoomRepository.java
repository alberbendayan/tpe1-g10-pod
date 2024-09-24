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
    private final ReentrantLock lock = new ReentrantLock();

    public RoomRepository() {
        this.rooms = Collections.synchronizedList(new ArrayList<>());
    }

    public synchronized Room addRoom() {
        Room room = Room.newBuilder()
                .setId(roomIdCounter++)
                .setIsEmpty(true)
                .build();
        rooms.add(room);
        return room;
    }


    public boolean isFree(int number) {
        lock.lock();
        try {
            return rooms.get(number - 1).getIsEmpty();
        } finally {
            lock.unlock();
        }
    }

    public Room setOccupied(int number) {
        lock.lock();
        try {
            if (!isFree(number)) {
                return null;
            }
            Room room = Room.newBuilder()
                    .setId(number)
                    .setIsEmpty(false)
                    .build();
            rooms.set(number - 1, room);
            return room;
        } finally {
            lock.unlock();
        }
    }

    public Room setFree(int number) {
        lock.lock();
        try {
            if (isFree(number)) {
                return null;
            }
            Room room = Room.newBuilder()
                    .setId(number)
                    .setIsEmpty(true)
                    .build();
            rooms.set(number - 1, room);
            return room;
        } finally {
            lock.unlock();
        }
    }

    public List<Room> getAllFreeRooms() {
        lock.lock();
        try {
            List<Room> ret = new ArrayList<>();
            for (Room room : rooms) {
                if (room.getIsEmpty()) {
                    ret.add(room);
                }
            }
            return ret;
        } finally {
            lock.unlock();
        }
    }

    public List<Room> getRooms() {
        lock.lock();
        try {
            return new ArrayList<>(rooms); // Devolvemos una copia para evitar problemas de concurrencia.
        } finally {
            lock.unlock();
        }
    }
}