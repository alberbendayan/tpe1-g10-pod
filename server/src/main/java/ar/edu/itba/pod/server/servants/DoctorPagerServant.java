package ar.edu.itba.pod.server.servants;

import ar.edu.itba.pod.grpc.common.Doctor;
import ar.edu.itba.pod.grpc.doctorPageService.DoctorPageServiceGrpc;
import ar.edu.itba.pod.server.repositories.AttentionRepository;
import ar.edu.itba.pod.server.repositories.DoctorRepository;
import ar.edu.itba.pod.server.repositories.PatientRepository;
import ar.edu.itba.pod.server.repositories.RoomRepository;
import com.google.protobuf.StringValue;
import io.grpc.stub.StreamObserver;

public class DoctorPagerServant extends DoctorPageServiceGrpc.DoctorPageServiceImplBase{

    private RoomRepository roomRepository;
    private DoctorRepository doctorRepository;
    private PatientRepository patientRepository;
    private AttentionRepository attentionRepository;

    public DoctorPagerServant(RoomRepository roomRepository, DoctorRepository doctorRepository, PatientRepository patientRepository, AttentionRepository attentionRepository) {
        this.roomRepository = roomRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.attentionRepository = attentionRepository;
    }

    @Override
    public void registerDoctor(StringValue request, StreamObserver<Doctor> responseObserver) {
        super.registerDoctor(request, responseObserver);
    }

    @Override
    public void unsuscribeDoctor(StringValue request, StreamObserver<Doctor> responseObserver) {
        super.unsuscribeDoctor(request, responseObserver);
    }
}
