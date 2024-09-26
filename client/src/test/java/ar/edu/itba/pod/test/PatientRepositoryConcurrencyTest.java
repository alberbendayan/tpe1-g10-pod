package ar.edu.itba.pod.test;
import ar.edu.itba.pod.grpc.common.*;
import ar.edu.itba.pod.server.repositories.PatientRepository;
import com.google.protobuf.Timestamp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

public class PatientRepositoryConcurrencyTest {

    private PatientRepository patientRepository;

    @BeforeEach
    public void setUp() {
        patientRepository = new PatientRepository();
    }

    @Test
    public void testConcurrentAddPatients() throws InterruptedException {
        int threadCount = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        for (int i = 0; i < threadCount; i++) {
            final int patientNumber = i;
            executor.submit(() -> {
                RequestPatient requestPatient = RequestPatient.newBuilder()
                        .setName("Patient_" + patientNumber)
                        .setLevel(1 + (patientNumber % 5))
                        .build();
                patientRepository.addPatient(requestPatient);
            });
        }

        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);

        assertEquals(threadCount, patientRepository.getPatientsInWaitingRoom().size());
    }

    @Test
    public void testConcurrentUpdatePatients() throws InterruptedException {
        int threadCount = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        for (int i = 0; i < threadCount; i++) {
            RequestPatient requestPatient = RequestPatient.newBuilder()
                    .setName("Patient_" + i)
                    .setLevel(1 + (i % 5))
                    .build();
            patientRepository.addPatient(requestPatient);
        }

        for (int i = 0; i < threadCount; i++) {
            final int patientNumber = i;
            executor.submit(() -> {
                String patientName = "Patient_" + patientNumber;
                int newLevel = (patientNumber % 5) + 1;
                patientRepository.updateLevel(patientName, newLevel);
            });
        }

        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);

        for (int i = 0; i < threadCount; i++) {
            String patientName = "Patient_" + i;
            Patient patient = patientRepository.getPatient(patientName);
            assertNotNull(patient);
            assertEquals((i % 5) + 1, patient.getLevel());
        }
    }

    @Test
    public void testConcurrentCheckAndGetMostUrgentPatient() throws InterruptedException {
        int threadCount = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        for (int i = 0; i < threadCount; i++) {
            RequestPatient requestPatient = RequestPatient.newBuilder()
                    .setName("Patient_" + i)
                    .setLevel(1 + (i % 5))
                    .build();
            patientRepository.addPatient(requestPatient);
        }

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                Patient mostUrgent = patientRepository.getMostUrgentPatient();
                assertNotNull(mostUrgent);
            });
        }

        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);
    }
}

