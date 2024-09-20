package ar.edu.itba.pod.server.servants;

import ar.edu.itba.pod.grpc.common.Patient;
import ar.edu.itba.pod.grpc.common.PatientTime;
import ar.edu.itba.pod.grpc.common.RequestPatient;
import ar.edu.itba.pod.grpc.waitingRoomService.WaitingRoomServiceGrpc;
import ar.edu.itba.pod.server.repositories.DoctorRepository;
import ar.edu.itba.pod.server.repositories.PatientRepository;
import ar.edu.itba.pod.server.repositories.RoomRepository;
import com.google.protobuf.StringValue;
import io.grpc.stub.StreamObserver;


public class WaitingRoomServant extends WaitingRoomServiceGrpc.WaitingRoomServiceImplBase{

    private RoomRepository roomRepository;
    private DoctorRepository doctorRepository;
    private PatientRepository patientRepository;

    public WaitingRoomServant(RoomRepository roomRepository, DoctorRepository doctorRepository, PatientRepository patientRepository) {
        this.roomRepository = new RoomRepository();
        this.doctorRepository = new DoctorRepository();
        this.patientRepository = new PatientRepository();
    }

    @Override
    public void addPatient(RequestPatient request, StreamObserver<Patient> responseObserver) {
        super.addPatient(request, responseObserver);
    }

    @Override
    public void changePatientLevel(RequestPatient request, StreamObserver<Patient> responseObserver) {
        super.changePatientLevel(request, responseObserver);
    }

    @Override
    public void waitingTime(StringValue request, StreamObserver<PatientTime> responseObserver) {
        super.waitingTime(request, responseObserver);
    }
}
