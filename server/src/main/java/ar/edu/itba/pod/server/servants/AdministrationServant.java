package ar.edu.itba.pod.server.servants;


import ar.edu.itba.pod.grpc.administrationService.AdministrationServiceGrpc;
import ar.edu.itba.pod.grpc.common.RequestDoctor;
import ar.edu.itba.pod.grpc.common.RequestDoctorLevel;
import ar.edu.itba.pod.grpc.common.Room;
import ar.edu.itba.pod.server.repositories.DoctorRepository;
import ar.edu.itba.pod.server.repositories.PatientRepository;
import ar.edu.itba.pod.server.repositories.RoomRepository;
import com.google.protobuf.Empty;
import com.google.protobuf.StringValue;
import ar.edu.itba.pod.grpc.common.Doctor;
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
    public void addDoctor(RequestDoctorLevel request, StreamObserver<Doctor> responseObserver) {
        Doctor doctor = doctorRepository.addDoctor(request.getDefaultInstanceForType());
        responseObserver.onNext(doctor);
        responseObserver.onCompleted();
    }

    @Override
    public void changeDoctorAvailability(RequestDoctor request, StreamObserver<Doctor> responseObserver) {
        Doctor doctor = doctorRepository.changeAvailability(request.getDefaultInstanceForType());
        responseObserver.onNext(doctor);
        responseObserver.onCompleted();
    }

    @Override
    public void getDoctorAvailability(StringValue request, StreamObserver<Doctor> responseObserver) {
        Doctor doctor = doctorRepository.getAvailability(String.valueOf(request.getDefaultInstanceForType()));
        responseObserver.onNext(doctor);
        responseObserver.onCompleted();
    }
}
