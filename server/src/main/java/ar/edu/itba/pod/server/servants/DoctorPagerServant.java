package ar.edu.itba.pod.server.servants;

import ar.edu.itba.pod.grpc.common.Doctor;
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

    private final Map<String, Set<StreamObserver<Doctor>>> subscribers = new ConcurrentHashMap<>();

    public DoctorPagerServant(RoomRepository roomRepository, DoctorRepository doctorRepository, PatientRepository patientRepository, AttentionRepository attentionRepository) {
        this.roomRepository = roomRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.attentionRepository = attentionRepository;
    }

    @Override
    public void registerDoctor(StringValue request, StreamObserver<Doctor> responseObserver) {
        String doctorId = request.getValue();
        //TODO: tiene que fallar si ya estaba
        subscribers.computeIfAbsent(doctorId, k -> ConcurrentHashMap.newKeySet()).add(responseObserver);
        Doctor doctor = doctorRepository.getDoctorById(doctorId);
        responseObserver.onNext(doctor);
        responseObserver.onCompleted();
    }

    @Override
    public void unsuscribeDoctor(StringValue request, StreamObserver<Doctor> responseObserver) {
        String doctorId = request.getValue();

        Set<StreamObserver<Doctor>> doctorSubscribers = subscribers.get(doctorId);
        if (doctorSubscribers != null) {
            doctorSubscribers.remove(responseObserver);
        }

        Doctor doctor = doctorRepository.getDoctorById(doctorId);
        responseObserver.onNext(doctor);
        responseObserver.onCompleted();
    }

    public void notifyDoctorChange(String doctorId, Doctor updatedDoctor) {
        Set<StreamObserver<Doctor>> doctorSubscribers = subscribers.get(doctorId);

        if (doctorSubscribers != null) {
            for (StreamObserver<Doctor> observer : doctorSubscribers) {
                observer.onNext(updatedDoctor);
            }
        }
    }
}
