package ar.edu.itba.pod.server.servants;

import ar.edu.itba.pod.grpc.common.*;
import ar.edu.itba.pod.grpc.emergencyCareService.EmergencyCareServiceGrpc;
import ar.edu.itba.pod.server.repositories.AttentionRepository;
import ar.edu.itba.pod.server.repositories.DoctorRepository;
import ar.edu.itba.pod.server.repositories.PatientRepository;
import ar.edu.itba.pod.server.repositories.RoomRepository;
import com.google.protobuf.Empty;
import com.google.protobuf.Int32Value;
import io.grpc.stub.StreamObserver;
import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EmergencyCareServant extends EmergencyCareServiceGrpc.EmergencyCareServiceImplBase {

    private RoomRepository roomRepository;
    private DoctorRepository doctorRepository;
    private PatientRepository patientRepository;
    private AttentionRepository attentionRepository;

    public EmergencyCareServant(RoomRepository roomRepository, DoctorRepository doctorRepository, PatientRepository patientRepository, AttentionRepository attentionRepository) {
        this.roomRepository = roomRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.attentionRepository = attentionRepository;
    }

    private AttentionResponse exitWithoutError(int number,StreamObserver<AttentionResponse> responseObserver){
        return  AttentionResponse.newBuilder()
                .setPatientLevel(-1)
                .setRoom(number)
                .build();

    }

    private AttentionResponse exitRoomOccupied(int number,StreamObserver<AttentionResponse> responseObserver){
        return AttentionResponse.newBuilder()
                .setPatientLevel(-2)
                .setRoom(number)
                .build();

    }

    private AttentionResponse attention(Int32Value request, StreamObserver<AttentionResponse> responseObserver){
        int roomNumber = request.getValue();
        if(!roomRepository.isFree(roomNumber)){
            return exitRoomOccupied(roomNumber,responseObserver);
        }
        Patient patient = patientRepository.getMostUrgentPatient();
        if(patient == null){
            return exitWithoutError(roomNumber,responseObserver);
        }
        Doctor doctor = doctorRepository.getDoctorToPatient(patient.getLevel());
        if(doctor == null){
            return exitWithoutError(roomNumber,responseObserver);
        }

        Patient newPatient = patientRepository.changeStatus(patient.getName(),patient.getLevel(),State.STATE_ATTENDING);

        RequestDoctor requestDoctor = RequestDoctor.newBuilder()
                .setAvailability(Availability.AVAILABILITY_ATTENDING)
                .setName(doctor.getName())
                .build();

        Doctor newDoctor = doctorRepository.changeAvailability(requestDoctor,doctor.getLevel());

        Room room = roomRepository.setOccupied(roomNumber);

        AttentionResponse response = AttentionResponse.newBuilder()
                .setDoctor(newDoctor.getName())
                .setDoctorLevel(newDoctor.getLevel())
                .setPatient(newPatient.getName())
                .setPatientLevel(newPatient.getLevel())
                .setRoom(roomNumber)
                .setIsEmpty(true)
                .build();
        return response;
    }
    @Override
    public void startAttention(Int32Value request, StreamObserver<AttentionResponse> responseObserver) {
        AttentionResponse response = attention(request,responseObserver);

        attentionRepository.startAttention(response);
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void startAllAttention(Empty request, StreamObserver<AttentionResponse> responseObserver) {
        List<Room> rooms = roomRepository.getRooms();
        for(Room room : rooms){
            responseObserver.onNext(attention(Int32Value.of(room.getId()),responseObserver));
        }
        responseObserver.onCompleted();
    }

    @Override
    public void finishAttention(Attention request, StreamObserver<AttentionResponse> responseObserver) {
        AttentionResponse attentionResponse = attentionRepository.existAttention(request);
        if(attentionResponse == null){
            // error
        }
        responseObserver.onNext(attentionRepository.finishAttention(attentionResponse));
        responseObserver.onCompleted();
    }
}
