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

    private void exitWithoutError(int number,StreamObserver<AttentionResponse> responseObserver){
        AttentionResponse response = AttentionResponse.newBuilder()
                .setPatientLevel(-1)
                .setRoom(number)
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void startAttention(Int32Value request, StreamObserver<AttentionResponse> responseObserver) {
        int roomNumber = Integer.valueOf(String.valueOf(request));
        if(!roomRepository.isFree(roomNumber)){
            // falla xq el room esta ocupad
        }
        Doctor doctor = doctorRepository.getHighLevelFreeDoctor();
        if(doctor == null){
            exitWithoutError(roomNumber,responseObserver);
            return;
        }
        Patient patient = patientRepository.getMostUrgentPatientFromLevel(doctor.getLevel());
        if(patient == null){
            exitWithoutError(roomNumber,responseObserver);
            return;
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

        attentionRepository.startAttention(response);
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void startAllAttention(Empty request, StreamObserver<AttentionResponse> responseObserver) {
        List<Room> freeRooms = roomRepository.getAllFreeRooms();
        for(Room room : freeRooms){
            startAttention(Int32Value.of(room.getId()),responseObserver);
        }
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
