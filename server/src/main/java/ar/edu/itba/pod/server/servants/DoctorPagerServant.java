package ar.edu.itba.pod.server.servants;

import ar.edu.itba.pod.grpc.common.AttentionResponse;
import ar.edu.itba.pod.grpc.common.Doctor;
import ar.edu.itba.pod.grpc.common.Notification;
import ar.edu.itba.pod.grpc.common.NotificationType;
import ar.edu.itba.pod.grpc.doctorPageService.DoctorPageServiceGrpc;
import ar.edu.itba.pod.server.repositories.*;
import com.google.protobuf.StringValue;
import io.grpc.stub.StreamObserver;

import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class DoctorPagerServant extends DoctorPageServiceGrpc.DoctorPageServiceImplBase {

    private final RoomRepository roomRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final AttentionRepository attentionRepository;
    private final NotificationRepository notificationRepository;


    public DoctorPagerServant(RoomRepository roomRepository, DoctorRepository doctorRepository, PatientRepository patientRepository, AttentionRepository attentionRepository, NotificationRepository notificationRepository){
        this.roomRepository = roomRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.attentionRepository = attentionRepository;
        this.notificationRepository= notificationRepository;
    }

    @Override
    public void registerDoctor(StringValue request, StreamObserver<Notification> responseObserver) {
        String name = request.getValue();
        Doctor doctor = doctorRepository.getDoctorByName(name);
        notificationRepository.registerSubscriber(name, doctor);
        Queue<Notification> notifications = notificationRepository.getSubscriber(name);
        for(Notification notification : notifications){
            responseObserver.onNext(notification);
        }

    }

    @Override
    public void unsuscribeDoctor(StringValue request, StreamObserver<Notification> responseObserver) {
        String name = request.getValue();
        Doctor doctor = doctorRepository.getDoctorByName(name);
        Notification notification=notificationRepository.unregisterSubscriber(name, doctor);
        responseObserver.onNext(notification);
        responseObserver.onCompleted();
    }


}
