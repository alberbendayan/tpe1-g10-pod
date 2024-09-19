package ar.edu.itba.pod.server.servants;

import ar.edu.itba.pod.grpc.doctorPageService.DoctorPageServiceGrpc;
import ar.edu.itba.pod.server.repositories.DoctorRepository;
import ar.edu.itba.pod.server.repositories.PatientRepository;
import ar.edu.itba.pod.server.repositories.RoomRepository;
import com.google.protobuf.StringValue;
import common.Common;
import io.grpc.stub.StreamObserver;

public class DoctorPagerServant extends DoctorPageServiceGrpc.DoctorPageServiceImplBase{

    private RoomRepository roomRepository;
    private DoctorRepository doctorRepository;
    private PatientRepository patientRepository;

    public DoctorPagerServant(RoomRepository roomRepository, DoctorRepository doctorRepository, PatientRepository patientRepository) {
        this.roomRepository = new RoomRepository();
        this.doctorRepository = new DoctorRepository();
        this.patientRepository = new PatientRepository();
    }

    @Override
    public void registerDoctor(StringValue request, StreamObserver<Common.Doctor> responseObserver) {
        super.registerDoctor(request, responseObserver);
    }

    @Override
    public void unsuscribeDoctor(StringValue request, StreamObserver<Common.Doctor> responseObserver) {
        super.unsuscribeDoctor(request, responseObserver);
    }
}
