package ar.edu.itba.pod.server.servants;

import ar.edu.itba.pod.grpc.common.Attention;
import ar.edu.itba.pod.grpc.common.AttentionResponse;
import ar.edu.itba.pod.grpc.common.Patient;
import ar.edu.itba.pod.grpc.common.Room;
import ar.edu.itba.pod.grpc.queryClientService.QueryClientServiceGrpc;
import ar.edu.itba.pod.server.repositories.AttentionRepository;
import ar.edu.itba.pod.server.repositories.DoctorRepository;
import ar.edu.itba.pod.server.repositories.PatientRepository;
import ar.edu.itba.pod.server.repositories.RoomRepository;
import com.google.protobuf.Empty;
import com.google.protobuf.Int32Value;
import com.google.protobuf.StringValue;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

import java.util.Collection;
import java.util.List;


public class QueryClientServant extends QueryClientServiceGrpc.QueryClientServiceImplBase{

    private RoomRepository roomRepository;
    private PatientRepository patientRepository;
    private AttentionRepository attentionRepository;

    public QueryClientServant(RoomRepository roomRepository, PatientRepository patientRepository, AttentionRepository attentionRepository) {
        this.roomRepository = roomRepository;
        this.patientRepository = patientRepository;
        this.attentionRepository = attentionRepository;
    }


    @Override
    public void getRooms(Empty request, StreamObserver<AttentionResponse> responseObserver) {
        List<Room> rooms= roomRepository.getRooms();
        if(rooms.isEmpty()){
            responseObserver.onError(Status.NOT_FOUND.withDescription("There are no rooms").asRuntimeException());
        }
        for(Room room: rooms){
            if(room.getIsEmpty()){
                responseObserver.onNext( AttentionResponse.newBuilder()
                        .setRoom(room.getId())
                        .setIsEmpty(room.getIsEmpty())
                        .build());
            }
            else{
                AttentionResponse response=attentionRepository.getStartedAttention(room.getId());
                responseObserver.onNext( AttentionResponse.newBuilder()
                        .setRoom(room.getId())
                        .setDoctor(response.getDoctor())
                        .setDoctorLevel(response.getDoctorLevel())
                        .setPatient(response.getPatient())
                        .setPatientLevel(response.getPatientLevel())
                        .setIsEmpty(room.getIsEmpty())
                        .build());
            }
        }
        responseObserver.onCompleted();
    }

    @Override
    public void getPatients(Empty request, StreamObserver<Patient> responseObserver) {
        List<Patient> list = patientRepository.getPatientsInWaitingRoom();
        if(list.isEmpty()){
            responseObserver.onError(Status.NOT_FOUND.withDescription("There are no patients").asRuntimeException());
        }
        for(Patient p : list){
            responseObserver.onNext(p);
        }
        responseObserver.onCompleted();
    }

    @Override
    public void getAttentions(Empty request, StreamObserver<AttentionResponse> responseObserver) {
        List<AttentionResponse> list = attentionRepository.getFinishedAttentions();
        if(list.isEmpty()){
            responseObserver.onError(Status.NOT_FOUND.withDescription("There are no attentions").asRuntimeException());
        }
        for(AttentionResponse a:list){
            responseObserver.onNext(a);
        }
        responseObserver.onCompleted();
    }

    @Override
    public void getAttentionsRoom(Int32Value room, StreamObserver<AttentionResponse> responseObserver) {
        List<AttentionResponse> list = attentionRepository.getFinishedAttentionsByRoom(room.getValue());
        if(list.isEmpty()){
            responseObserver.onError(Status.NOT_FOUND.withDescription("There are no attentions").asRuntimeException());
        }
        for(AttentionResponse a:list){
            responseObserver.onNext(a);
        }
        responseObserver.onCompleted();
    }
}
