package ar.edu.itba.pod.server.servants;

import ar.edu.itba.pod.grpc.common.Patient;
import ar.edu.itba.pod.grpc.common.PatientTime;
import ar.edu.itba.pod.grpc.common.RequestPatient;
import ar.edu.itba.pod.grpc.waitingRoomService.WaitingRoomServiceGrpc;
import ar.edu.itba.pod.server.repositories.AttentionRepository;
import ar.edu.itba.pod.server.repositories.DoctorRepository;
import ar.edu.itba.pod.server.repositories.PatientRepository;
import ar.edu.itba.pod.server.repositories.RoomRepository;
import com.google.protobuf.StringValue;
import io.grpc.stub.StreamObserver;


public class WaitingRoomServant extends WaitingRoomServiceGrpc.WaitingRoomServiceImplBase {

    private RoomRepository roomRepository;
    private DoctorRepository doctorRepository;
    private PatientRepository patientRepository;
    private AttentionRepository attentionRepository;

    public WaitingRoomServant(RoomRepository roomRepository, DoctorRepository doctorRepository, PatientRepository patientRepository, AttentionRepository attentionRepository) {
        this.roomRepository = roomRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.attentionRepository = attentionRepository;
    }

    @Override
    public void addPatient(RequestPatient request, StreamObserver<Patient> responseObserver) {
        Patient patient = patientRepository.addPatient(request);
        responseObserver.onNext(patient);
        responseObserver.onCompleted();
    }

    @Override
    public void updateLevel(RequestPatient request, StreamObserver<Patient> responseObserver) {
        Patient patient = patientRepository.updateLevel(request.getName(), request.getLevel());
        responseObserver.onNext(patient);
        responseObserver.onCompleted();
    }

    @Override
    public void checkPatient(StringValue request, StreamObserver<PatientTime> responseObserver) {
        PatientTime patientTime = patientRepository.checkPatient(request.getValue());
        responseObserver.onNext(patientTime);
        responseObserver.onCompleted();
    }
}
