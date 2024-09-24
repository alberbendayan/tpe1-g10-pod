package ar.edu.itba.pod.server.servants;

import ar.edu.itba.pod.grpc.common.AttentionResponse;
import ar.edu.itba.pod.grpc.common.Doctor;
import ar.edu.itba.pod.grpc.common.Notification;
import ar.edu.itba.pod.grpc.common.NotificationType;
import ar.edu.itba.pod.grpc.doctorPageService.DoctorPageServiceGrpc;
import ar.edu.itba.pod.server.repositories.AttentionRepository;
import ar.edu.itba.pod.server.repositories.DoctorRepository;
import ar.edu.itba.pod.server.repositories.PatientRepository;
import ar.edu.itba.pod.server.repositories.RoomRepository;
import com.google.protobuf.StringValue;
import io.grpc.stub.StreamObserver;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class DoctorPagerServant extends DoctorPageServiceGrpc.DoctorPageServiceImplBase {

    private final RoomRepository roomRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final AttentionRepository attentionRepository;

    private final Map<String, Set<StreamObserver<Notification>>> subscribers = new ConcurrentHashMap<>();

    public DoctorPagerServant(RoomRepository roomRepository, DoctorRepository doctorRepository, PatientRepository patientRepository, AttentionRepository attentionRepository) {
        this.roomRepository = roomRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.attentionRepository = attentionRepository;
    }

    @Override
    public void registerDoctor(StringValue request, StreamObserver<Notification> responseObserver) {
        String doctorId = request.getValue();
        //TODO: tiene que fallar si ya estaba
        subscribers.computeIfAbsent(doctorId, k -> ConcurrentHashMap.newKeySet()).add(responseObserver);
        Doctor doctor = doctorRepository.getDoctorByName(doctorId);
        responseObserver.onNext(Notification.newBuilder().setDoctor(doctor).setType(NotificationType.SUBSCRIBE).build());
    }

    @Override
    public void unsuscribeDoctor(StringValue request, StreamObserver<Notification> responseObserver) {
        String doctorId = request.getValue();
        Set<StreamObserver<Notification>> doctorSubscribers = subscribers.get(doctorId);
        if (doctorSubscribers != null) {
            doctorSubscribers.remove(responseObserver);
        }
        Doctor doctor = doctorRepository.getDoctorByName(doctorId);
        responseObserver.onNext(Notification.newBuilder().setDoctor(doctor).setType(NotificationType.UNSUBSCRIBE).build());
    }


  public void notify(String doctorId, Doctor doctor, AttentionResponse attentionResponse, NotificationType notificationType) {
        Set<StreamObserver<Notification>> doctorSubscribers = subscribers.get(doctorId);
        if (doctorSubscribers != null) {
            doctorSubscribers.forEach(observer -> observer.onNext(Notification.newBuilder().setDoctor(doctor).setAttention(attentionResponse).setType(notificationType).build()));
        }
    }
}
