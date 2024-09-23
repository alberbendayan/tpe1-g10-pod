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
import io.grpc.Status;
import io.grpc.stub.StreamObserver;


public class WaitingRoomServant extends WaitingRoomServiceGrpc.WaitingRoomServiceImplBase {

    private PatientRepository patientRepository;
    public WaitingRoomServant(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    @Override
    public void addPatient(RequestPatient request, StreamObserver<Patient> responseObserver) {
        Patient patient = patientRepository.addPatient(request);
        if(patient.getLevel() == -1){
            responseObserver.onError(Status.INVALID_ARGUMENT.withDescription("Invalid level").asRuntimeException());
        }else if(patient.getLevel() == -2){
            responseObserver.onError(Status.INVALID_ARGUMENT.withDescription("Patient "+request.getName()+" already exists").asRuntimeException());
        }
        responseObserver.onNext(patient);
        responseObserver.onCompleted();
    }

    @Override
    public void updateLevel(RequestPatient request, StreamObserver<Patient> responseObserver) {
        Patient patient = patientRepository.updateLevel(request.getName(), request.getLevel());
        if(request.getLevel() < 1 || request.getLevel() > 5){
            responseObserver.onError(Status.INVALID_ARGUMENT.withDescription("Invalid level").asRuntimeException());
        }else if(patient.getLevel() == -2){
            responseObserver.onError(Status.INVALID_ARGUMENT.withDescription("Patient "+request.getName()+" does not exists").asRuntimeException());
        }
        responseObserver.onNext(patient);
        responseObserver.onCompleted();
    }

    @Override
    public void checkPatient(StringValue request, StreamObserver<PatientTime> responseObserver) {
        PatientTime patientTime = patientRepository.checkPatient(request.getValue());
        if(patientTime.getPatientsAhead() == -1){
            responseObserver.onError(Status.INVALID_ARGUMENT.withDescription("Patient "+request.getValue()+" does not exists").asRuntimeException());
        }
        responseObserver.onNext(patientTime);
        responseObserver.onCompleted();
    }
}
