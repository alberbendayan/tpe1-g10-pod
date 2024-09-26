package ar.edu.itba.pod.server.repositories;

import ar.edu.itba.pod.grpc.common.*;

import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class AttentionRepository {
    private HashMap<Integer, AttentionResponse> startedAttentions;
    private List<AttentionResponse> finishedAttentions;

    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    public AttentionRepository() {
        startedAttentions = new HashMap<>();
        finishedAttentions = new ArrayList<>();
    }

    public AttentionResponse startAttention(AttentionResponse request) {
        lock.writeLock().lock();
        try {
            if (startedAttentions.containsKey(request.getRoom())) {
                return null;
            }
            startedAttentions.put(request.getRoom(), request);
            return request;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public AttentionResponse existAttention(Attention attention) {
        lock.readLock().lock();
        try {
            AttentionResponse a = startedAttentions.get(attention.getRoom());
            if (a != null
                    && a.getDoctor().equals(attention.getDoctor())
                    && a.getPatient().equals(attention.getPatient())
                    && a.getRoom() == attention.getRoom())
                return a;
            return null;
        } finally {
            lock.readLock().unlock();
        }
    }

    public AttentionResponse finishAttention(AttentionResponse attentionResponse) {
        lock.writeLock().lock();
        try {
            if (!startedAttentions.containsKey(attentionResponse.getRoom()) ||
                    !startedAttentions.get(attentionResponse.getRoom()).getDoctor().equals(attentionResponse.getDoctor())
            ) {
                return null;
            }
            startedAttentions.remove(attentionResponse.getRoom());
            finishedAttentions.add(attentionResponse);
            return attentionResponse;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public AttentionResponse getStartedAttention(Integer key) {
        lock.readLock().lock();
        try {
            return startedAttentions.get(key);
        } finally {
            lock.readLock().unlock();
        }
    }

    public List<AttentionResponse> getFinishedAttentions() {
        lock.readLock().lock();
        try {
            return List.copyOf(finishedAttentions);
        } finally {
            lock.readLock().unlock();
        }
    }

    public List<AttentionResponse> getFinishedAttentionsByRoom(int room) {
        lock.readLock().lock();
        try {
            List<AttentionResponse> list = new ArrayList<>();
            for (AttentionResponse a : finishedAttentions) {
                if (a.getRoom() == room) {
                    list.add(a);
                }
            }
            return list;
        } finally {
            lock.readLock().unlock();
        }
    }
}
