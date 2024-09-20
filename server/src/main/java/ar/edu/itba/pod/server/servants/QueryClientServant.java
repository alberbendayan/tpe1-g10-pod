package ar.edu.itba.pod.server.servants;

import ar.edu.itba.pod.grpc.common.Attention;
import ar.edu.itba.pod.grpc.common.Patient;
import ar.edu.itba.pod.grpc.common.Room;
import ar.edu.itba.pod.grpc.queryClientService.QueryClientServiceGrpc;
import ar.edu.itba.pod.server.repositories.DoctorRepository;
import ar.edu.itba.pod.server.repositories.PatientRepository;
import ar.edu.itba.pod.server.repositories.RoomRepository;
import com.google.protobuf.StringValue;
import io.grpc.stub.StreamObserver;


public class QueryClientServant extends QueryClientServiceGrpc.QueryClientServiceImplBase{

    private RoomRepository roomRepository;
    private DoctorRepository doctorRepository;
    private PatientRepository patientRepository;

    public QueryClientServant(RoomRepository roomRepository, DoctorRepository doctorRepository, PatientRepository patientRepository) {
        this.roomRepository = new RoomRepository();
        this.doctorRepository = new DoctorRepository();
        this.patientRepository = new PatientRepository();
    }

    @Override
    public void getRooms(StringValue request, StreamObserver<Room> responseObserver) {
        super.getRooms(request, responseObserver);
    }

    @Override
    public void getPatients(StringValue request, StreamObserver<Patient> responseObserver) {
        super.getPatients(request, responseObserver);
    }

    @Override
    public void getAttentions(StringValue request, StreamObserver<Attention> responseObserver) {
        super.getAttentions(request, responseObserver);
    }
}
