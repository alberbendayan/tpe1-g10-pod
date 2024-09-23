package ar.edu.itba.pod.server.servants;


import ar.edu.itba.pod.grpc.administrationService.AdministrationServiceGrpc;
import ar.edu.itba.pod.grpc.common.*;
import ar.edu.itba.pod.server.repositories.AttentionRepository;
import ar.edu.itba.pod.server.repositories.DoctorRepository;
import ar.edu.itba.pod.server.repositories.PatientRepository;
import ar.edu.itba.pod.server.repositories.RoomRepository;
import com.google.protobuf.Empty;
import com.google.protobuf.StringValue;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

public class AdministrationServant extends AdministrationServiceGrpc.AdministrationServiceImplBase {

    private final RoomRepository roomRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private AttentionRepository attentionRepository;

    public AdministrationServant(RoomRepository roomRepository, DoctorRepository doctorRepository, PatientRepository patientRepository, AttentionRepository attentionRepository) {
        this.roomRepository = roomRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.attentionRepository = attentionRepository;
    }

    @Override
    public void addRoom(Empty request, StreamObserver<Room> responseObserver) {
        Room room = roomRepository.addRoom();
        responseObserver.onNext(room);
        responseObserver.onCompleted();

    }

    @Override
    public void addDoctor(RequestDoctorLevel request, StreamObserver<Doctor> responseObserver) {
        if(request.getLevel()>5 || request.getLevel()<1){
            responseObserver.onError(Status.INVALID_ARGUMENT.withDescription("Invalid level").asRuntimeException());
        }
        Doctor doctor = doctorRepository.addDoctor(request);
        if(doctor.getLevel() == -1){
            responseObserver.onError(Status.INVALID_ARGUMENT.withDescription("Doctor "+request.getName()+" already exists").asRuntimeException());
        }
        responseObserver.onNext(doctor);
        responseObserver.onCompleted();
    }

    @Override
    public void setDoctor(RequestDoctor request, StreamObserver<Doctor> responseObserver) {
        Doctor doctor = doctorRepository.changeAvailability(request);
        if(doctor.getLevel() == -1){
            responseObserver.onError(Status.INVALID_ARGUMENT.withDescription("Doctor "+request.getName()+" is attending").asRuntimeException());
        }else if(doctor.getLevel() == -2){
            responseObserver.onError(Status.INVALID_ARGUMENT.withDescription("Doctor does not exists").asRuntimeException());
        }
        responseObserver.onNext(doctor);
        responseObserver.onCompleted();
    }

    @Override
    public void checkDoctor(MyString request, StreamObserver<Doctor> responseObserver) {
        System.out.println(request.getName());
        Doctor doctor = doctorRepository.getAvailability(request.getName());
        if(doctor.getLevel() == -2){
            responseObserver.onError(Status.INVALID_ARGUMENT.withDescription("Doctor does not exists").asRuntimeException());
        }
        responseObserver.onNext(doctor);
        responseObserver.onCompleted();
    }
}
