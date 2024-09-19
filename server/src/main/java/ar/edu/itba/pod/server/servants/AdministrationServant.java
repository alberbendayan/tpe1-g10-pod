package ar.edu.itba.pod.server.servants;

import ar.edu.itba.pod.grpc.administrationService.AdministrationServiceGrpc;
import ar.edu.itba.pod.grpc.administrationService.Room;
import ar.edu.itba.pod.server.repositories.DoctorRepository;
import ar.edu.itba.pod.server.repositories.PatientRepository;
import ar.edu.itba.pod.server.repositories.RoomRepository;
import com.google.protobuf.Empty;
import com.google.protobuf.StringValue;
import common.Common;
import io.grpc.stub.StreamObserver;

public class AdministrationServant extends AdministrationServiceGrpc.AdministrationServiceImplBase {

    private final RoomRepository roomRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    public AdministrationServant(RoomRepository roomRepository, DoctorRepository doctorRepository, PatientRepository patientRepository) {
        this.roomRepository = new RoomRepository();
        this.doctorRepository = new DoctorRepository();
        this.patientRepository = new PatientRepository();
    }

    @Override
    public void addRoom(Empty request, StreamObserver<Room> responseObserver) {
        Room room = roomRepository.addRoom();
        responseObserver.onNext(room);
        responseObserver.onCompleted();
    }

    @Override
    public void addDoctor(Common.Doctor request, StreamObserver<Common.Doctor> responseObserver) {
        super.addDoctor(request, responseObserver);
    }

    @Override
    public void changeDoctorAvailability(Common.RequestDoctor request, StreamObserver<Common.Doctor> responseObserver) {
        super.changeDoctorAvailability(request, responseObserver);
    }

    @Override
    public void getDoctorAvailability(StringValue request, StreamObserver<Common.Doctor> responseObserver) {
        super.getDoctorAvailability(request, responseObserver);
    }
}
