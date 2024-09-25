package ar.edu.itba.pod.server.servants;

import ar.edu.itba.pod.grpc.common.AttentionResponse;
import ar.edu.itba.pod.grpc.common.Doctor;
import ar.edu.itba.pod.grpc.common.Notification;
import ar.edu.itba.pod.grpc.common.NotificationType;
import ar.edu.itba.pod.grpc.doctorPageService.DoctorPageServiceGrpc;
import ar.edu.itba.pod.server.repositories.*;
import com.google.protobuf.StringValue;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static java.lang.Thread.sleep;

public class DoctorPagerServant extends DoctorPageServiceGrpc.DoctorPageServiceImplBase {

    private final RoomRepository roomRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final AttentionRepository attentionRepository;
    private final NotificationRepository notificationRepository;


    public DoctorPagerServant(RoomRepository roomRepository, DoctorRepository doctorRepository, PatientRepository patientRepository, AttentionRepository attentionRepository, NotificationRepository notificationRepository) {
        this.roomRepository = roomRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.attentionRepository = attentionRepository;
        this.notificationRepository = notificationRepository;
    }

    @Override
    public void registerDoctor(StringValue request, StreamObserver<Notification> responseObserver) {
        String name = request.getValue();
        Doctor doctor = doctorRepository.getDoctorByName(name);
        if(doctor == null){
            responseObserver.onError(Status.INVALID_ARGUMENT.withDescription("The doctor does not exist").asRuntimeException());
            return;
        }
        Doctor d =notificationRepository.registerSubscriber(doctor);
        if(d == null){
            responseObserver.onError(Status.INVALID_ARGUMENT.withDescription("The action could not be executed").asRuntimeException());
            return;
        }
        while (notificationRepository.isRegistered(name) || notificationRepository.hasNext(name)) {
            if (!notificationRepository.hasNext(name)) {
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                Notification notification = notificationRepository.getNotification(name);
                responseObserver.onNext(notification);
            }
        }

        responseObserver.onCompleted();

    }

    @Override
    public void unsuscribeDoctor(StringValue request, StreamObserver<Notification> responseObserver) {
        String name = request.getValue();
        if(!notificationRepository.isRegistered(name)){
            responseObserver.onError(Status.INVALID_ARGUMENT.withDescription("The doctor is not registered").asRuntimeException());
        }
        Doctor doctor = doctorRepository.getDoctorByName(name);
        Notification notification = notificationRepository.unregisterSubscriber(name, doctor);
        responseObserver.onNext(notification);
        responseObserver.onCompleted();
    }


}
