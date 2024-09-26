package test;

import ar.edu.itba.pod.grpc.common.*;
import ar.edu.itba.pod.server.repositories.DoctorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class DoctorRepositoryConcurrencyTest {

    private DoctorRepository doctorRepository;
    private final int THREADS = 1000;

    @BeforeEach
    public void setUp() {
        doctorRepository = new DoctorRepository();
    }

    @Test
    public void testConcurrentAddDoctors() throws InterruptedException {
        int threadCount = THREADS;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        for (int i = 0; i < threadCount; i++) {
            final int doctorNumber = i;
            executor.submit(() -> {
                RequestDoctorLevel requestDoctor = RequestDoctorLevel.newBuilder()
                        .setName("Doctor_" + doctorNumber)
                        .setLevel(1 + (doctorNumber % 5))
                        .build();
                doctorRepository.addDoctor(requestDoctor);
            });
        }

        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);

        for (int i = 0; i < threadCount; i++) {
            Doctor doctor = doctorRepository.getDoctorByName("Doctor_" + i);
            assertNotNull(doctor);
        }
    }

    @Test
    public void testConcurrentChangeAvailability() throws InterruptedException {
        int threadCount = THREADS;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        for (int i = 0; i < threadCount; i++) {
            RequestDoctorLevel requestDoctor = RequestDoctorLevel.newBuilder()
                    .setName("Doctor_" + i)
                    .setLevel(1 + (i % 5))
                    .build();
            doctorRepository.addDoctor(requestDoctor);
        }

        for (int i = 0; i < threadCount; i++) {
            final int doctorNumber = i;
            executor.submit(() -> {
                RequestDoctor doctor = RequestDoctor.newBuilder()
                        .setName("Doctor_" + doctorNumber)
                        .setAvailability(Availability.AVAILABILITY_AVAILABLE)
                        .build();
                doctorRepository.changeAvailability(doctor);
            });
        }

        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);

        for (int i = 0; i < threadCount; i++) {
            Doctor doctor = doctorRepository.getDoctorByName("Doctor_" + i);
            assertNotNull(doctor);
            assertEquals(Availability.AVAILABILITY_AVAILABLE, doctor.getAvailability());
        }
    }

    @Test
    public void testConcurrentGetDoctorToPatient() throws InterruptedException {
        int threadCount = THREADS;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        for (int i = 0; i < threadCount; i++) {
            RequestDoctorLevel requestDoctor = RequestDoctorLevel.newBuilder()
                    .setName("Doctor_" + i)
                    .setLevel(1 + (i % 5))
                    .build();
            doctorRepository.addDoctor(requestDoctor);
        }

        for (int i = 0; i < threadCount; i++) {
            RequestDoctor doctor = RequestDoctor.newBuilder()
                    .setName("Doctor_" + i)
                    .setAvailability(Availability.AVAILABILITY_AVAILABLE)
                    .build();
            doctorRepository.changeAvailability(doctor);
        }

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                Doctor availableDoctor = doctorRepository.getDoctorToPatient(1);
                assertNotNull(availableDoctor);
                assertEquals(Availability.AVAILABILITY_AVAILABLE, availableDoctor.getAvailability());
            });
        }

        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);
    }
}

