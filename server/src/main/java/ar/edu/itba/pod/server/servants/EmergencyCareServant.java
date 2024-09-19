package ar.edu.itba.pod.server.servants;

import ar.edu.itba.pod.grpc.emergencyCareService.EmergencyCareServiceGrpc;
import ar.edu.itba.pod.server.repositories.DoctorRepository;
import ar.edu.itba.pod.server.repositories.PatientRepository;
import ar.edu.itba.pod.server.repositories.RoomRepository;
import com.google.protobuf.Empty;
import common.Common;
import io.grpc.stub.StreamObserver;

public class EmergencyCareServant extends EmergencyCareServiceGrpc.EmergencyCareServiceImplBase {

    private RoomRepository roomRepository;
    private DoctorRepository doctorRepository;
    private PatientRepository patientRepository;

    public EmergencyCareServant(RoomRepository roomRepository, DoctorRepository doctorRepository, PatientRepository patientRepository) {
        this.roomRepository = new RoomRepository();
        this.doctorRepository = new DoctorRepository();
        this.patientRepository = new PatientRepository();
    }

    @Override
    public void startAttention(Common.Attention request, StreamObserver<Common.AttentionResponse> responseObserver) {
        super.startAttention(request, responseObserver);
    }

    @Override
    public void startAllAttention(Empty request, StreamObserver<Common.AttentionResponse> responseObserver) {
        super.startAllAttention(request, responseObserver);
    }

    @Override
    public void finishAttention(Common.Attention request, StreamObserver<Common.AttentionResponse> responseObserver) {
        super.finishAttention(request, responseObserver);
    }
}
