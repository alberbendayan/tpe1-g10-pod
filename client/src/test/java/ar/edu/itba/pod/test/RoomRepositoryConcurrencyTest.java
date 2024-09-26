package ar.edu.itba.pod.test;

import ar.edu.itba.pod.server.repositories.RoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

public class RoomRepositoryConcurrencyTest {

    private RoomRepository roomRepository;
    private final int THREADS = 1000;

    @BeforeEach
    public void setUp() {
        roomRepository = new RoomRepository();
    }

    @Test
    public void testConcurrentRoomAdditionAndOccupation() throws InterruptedException {
        int threadCount = THREADS;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                roomRepository.addRoom();
            });
        }

        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);

        assertEquals(threadCount, roomRepository.getRooms().size());

        ExecutorService occupationExecutor = Executors.newFixedThreadPool(threadCount);
        for (int i = 1; i <= threadCount; i++) {
            final int roomNumber = i;
            occupationExecutor.submit(() -> {
                roomRepository.setOccupied(roomNumber);
            });
        }

        occupationExecutor.shutdown();
        occupationExecutor.awaitTermination(10, TimeUnit.SECONDS);

        for (int i = 1; i <= threadCount; i++) {
            assertFalse(roomRepository.isFree(i));
        }
    }

    @Test
    public void testConcurrentSetFree() throws InterruptedException {
        int threadCount = THREADS;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        for (int i = 0; i < threadCount; i++) {
            roomRepository.addRoom();
            roomRepository.setOccupied(i + 1);
        }

        for (int i = 1; i <= threadCount; i++) {
            final int roomNumber = i;
            executor.submit(() -> {
                roomRepository.setFree(roomNumber);
            });
        }

        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);

        for (int i = 1; i <= threadCount; i++) {
            assertTrue(roomRepository.isFree(i));
        }
    }
}
