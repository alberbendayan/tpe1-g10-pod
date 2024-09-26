package test;

import ar.edu.itba.pod.grpc.common.*;
import ar.edu.itba.pod.server.repositories.AttentionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AttentionRepositoryConcurrencyTest {

    private AttentionRepository attentionRepository;
    private AttentionResponse attentionResponse1, attentionResponse2;
    private final int THREADS = 1000;

    @BeforeEach
    public void setUp() {
        attentionRepository = new AttentionRepository();

        Doctor doctor1 = Doctor.newBuilder().setName("Smith").build();
        Patient patient1 = Patient.newBuilder().setName("Patient1").build();
        attentionResponse1 = AttentionResponse.newBuilder()
                .setDoctor(doctor1.getName())
                .setPatient(patient1.getName())
                .setRoom(1)
                .build();

        Doctor doctor2 = Doctor.newBuilder().setName("John").build();
        Patient patient2 = Patient.newBuilder().setName("Patient2").build();
        attentionResponse2 = AttentionResponse.newBuilder()
                .setDoctor(doctor2.getName())
                .setPatient(patient2.getName())
                .setRoom(2)
                .build();
    }

    @Test
    public void testStartAndFinishAttentionConcurrency() throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(THREADS);

        for (int i = 0; i < THREADS; i++) {
            int finalI = i;
            executor.submit(() -> {
                if (finalI % 2 == 0) {
                    attentionRepository.startAttention(attentionResponse1);
                } else {
                    attentionRepository.startAttention(attentionResponse2);
                }
            });

            executor.submit(() -> {
                if (finalI % 2 == 0) {
                    attentionRepository.finishAttention(attentionResponse1);
                } else {
                    attentionRepository.finishAttention(attentionResponse2);
                }
            });
        }

        executor.shutdown();
        assertTrue(executor.awaitTermination(1, TimeUnit.MINUTES));

        List<AttentionResponse> finishedAttentions = attentionRepository.getFinishedAttentions();
        assertTrue(finishedAttentions.contains(attentionResponse1));
        assertTrue(finishedAttentions.contains(attentionResponse2));
    }

    @Test
    public void testConcurrentAccessToExistAttention() throws InterruptedException {
        attentionRepository.startAttention(attentionResponse1);
        attentionRepository.startAttention(attentionResponse2);

        ExecutorService executor = Executors.newFixedThreadPool(THREADS);

        for (int i = 0; i < THREADS; i++) {
            executor.submit(() -> {
                Attention attention = Attention.newBuilder()
                        .setDoctor(attentionResponse1.getDoctor())
                        .setPatient(attentionResponse1.getPatient())
                        .setRoom(attentionResponse1.getRoom())
                        .build();
                assertNotNull(attentionRepository.existAttention(attention));
            });

            executor.submit(() -> {
                Attention attention = Attention.newBuilder()
                        .setDoctor(attentionResponse2.getDoctor())
                        .setPatient(attentionResponse2.getPatient())
                        .setRoom(attentionResponse2.getRoom())
                        .build();
                assertNotNull(attentionRepository.existAttention(attention));
            });
        }

        executor.shutdown();
        assertTrue(executor.awaitTermination(1, TimeUnit.MINUTES));
    }
}
