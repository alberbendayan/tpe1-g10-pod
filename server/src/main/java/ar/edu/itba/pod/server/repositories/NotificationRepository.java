package ar.edu.itba.pod.server.repositories;

import ar.edu.itba.pod.grpc.common.AttentionResponse;
import ar.edu.itba.pod.grpc.common.Doctor;
import ar.edu.itba.pod.grpc.common.Notification;
import ar.edu.itba.pod.grpc.common.NotificationType;
import io.grpc.stub.StreamObserver;

import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class NotificationRepository {
    private final Map<String, Queue<Notification>> subscribers = new ConcurrentHashMap<>();

    public void registerSubscriber(String name, Doctor doctor) {
        subscribers.computeIfAbsent(name, k -> new ConcurrentLinkedQueue<>()).add(Notification.newBuilder().setDoctor(doctor).setType(NotificationType.NOTIFICATION_SUBSCRIBE).build());
    }

    public Notification unregisterSubscriber(String name, Doctor doctor) {
        Notification notification=null;
        Queue<Notification> doctorSubscribers = subscribers.get(name);
        if (doctorSubscribers != null) {
             notification=Notification.newBuilder().setDoctor(doctor).setType(NotificationType.NOTIFICATION_UNSUBSCRIBE).build()
            doctorSubscribers.add(notification);
        }
        return notification;
    }

    public void notify( Doctor doctor) {
        Queue<Notification> doctorSubscribers = subscribers.get(doctor.getName());
        if (doctorSubscribers != null) {
            doctorSubscribers.add(Notification.newBuilder().setDoctor(doctor).setType(NotificationType.NOTIFICATION_DOCTOR_SET_AVAILABILITY).build());
        }
    }

    public void notify(String name, AttentionResponse attentionResponse, NotificationType notificationType) {
        Queue<Notification> doctorSubscribers = subscribers.get(name);
        if (doctorSubscribers != null) {
            doctorSubscribers.add(Notification.newBuilder().setAttention(attentionResponse).setType(notificationType).build());
        }
    }

    public  Queue<Notification> getSubscriber(String name) {
        return subscribers.get(name);
    }
}
