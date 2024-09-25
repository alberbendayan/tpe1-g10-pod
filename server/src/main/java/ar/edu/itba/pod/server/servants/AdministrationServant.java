package ar.edu.itba.pod.server.servants;


import ar.edu.itba.pod.grpc.administrationService.AdministrationServiceGrpc;
import ar.edu.itba.pod.grpc.common.*;
import ar.edu.itba.pod.server.repositories.*;
import com.google.protobuf.Empty;
import com.google.protobuf.StringValue;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

public class AdministrationServant extends AdministrationServiceGrpc.AdministrationServiceImplBase {

    private final RoomRepository roomRepository;
    private final DoctorRepository doctorRepository;
    private final NotificationRepository notificationRepository;

    public AdministrationServant(RoomRepository roomRepository, DoctorRepository doctorRepository, NotificationRepository notificationRepository) {
        this.roomRepository = roomRepository;
        this.doctorRepository = doctorRepository;
        this.notificationRepository = notificationRepository;
    }

    @Override
    public void addRoom(Empty request, StreamObserver<Room> responseObserver) {
        Room room = roomRepository.addRoom();
        responseObserver.onNext(room);
        responseObserver.onCompleted();

    }

    @Override
    public void addDoctor(RequestDoctorLevel request, StreamObserver<Doctor> responseObserver) {
        if (request.getLevel() > 5 || request.getLevel() < 1) {
            responseObserver.onError(Status.INVALID_ARGUMENT.withDescription("Invalid level").asRuntimeException());
        }
        Doctor doctor = doctorRepository.addDoctor(request);
        if (doctor.getLevel() == -1) {
            responseObserver.onError(Status.INVALID_ARGUMENT.withDescription("Doctor " + request.getName() + " already exists").asRuntimeException());
        }
        responseObserver.onNext(doctor);
        responseObserver.onCompleted();
    }

    @Override
    public void setDoctor(RequestDoctor request, StreamObserver<Doctor> responseObserver) {
        Doctor doctor = doctorRepository.changeAvailability(request);
        if (doctor.getLevel() == -1) {
            responseObserver.onError(Status.INVALID_ARGUMENT.withDescription("Doctor " + request.getName() + " is attending").asRuntimeException());
        } else if (doctor.getLevel() == -2) {
            responseObserver.onError(Status.INVALID_ARGUMENT.withDescription("Doctor does not exists").asRuntimeException());
        }
        notificationRepository.notify(doctor);
        responseObserver.onNext(doctor);
        responseObserver.onCompleted();
    }

    @Override
    public void checkDoctor(StringValue request, StreamObserver<Doctor> responseObserver) {
        Doctor doctor = doctorRepository.getAvailability(request.getValue());
        if (doctor.getLevel() == -2) {
            responseObserver.onError(Status.INVALID_ARGUMENT.withDescription("Doctor does not exists").asRuntimeException());
        }
        responseObserver.onNext(doctor);
        responseObserver.onCompleted();
    }
}
