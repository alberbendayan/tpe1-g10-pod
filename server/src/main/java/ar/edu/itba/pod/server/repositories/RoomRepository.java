package ar.edu.itba.pod.server.repositories;

import ar.edu.itba.pod.grpc.common.Room;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class RoomRepository {

    private final List<Room> rooms;
    private int roomIdCounter = 1;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    public RoomRepository() {
        this.rooms = Collections.synchronizedList(new ArrayList<>());
    }

    public Room addRoom() {
        lock.writeLock().lock();
        try {
            Room room = Room.newBuilder()
                    .setId(roomIdCounter++)
                    .setIsEmpty(true)
                    .build();
            rooms.add(room);
            return room;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public boolean isFree(int number) {
        lock.readLock().lock();
        try {
            if (number < 1 || number > rooms.size()) {
                return false;
            }
            return rooms.get(number - 1).getIsEmpty();
        } finally {
            lock.readLock().unlock();
        }
    }

    public Room setOccupied(int number) {
        lock.writeLock().lock();
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
            lock.writeLock().unlock();
        }
    }

    public Room setFree(int number) {
        lock.writeLock().lock();
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
            lock.writeLock().unlock();
        }
    }

    public List<Room> getRooms() {
        return List.copyOf(rooms);
    }
}
