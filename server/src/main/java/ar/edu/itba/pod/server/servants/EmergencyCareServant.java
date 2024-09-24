package ar.edu.itba.pod.server.servants;

import ar.edu.itba.pod.grpc.common.*;
import ar.edu.itba.pod.grpc.emergencyCareService.EmergencyCareServiceGrpc;
import ar.edu.itba.pod.server.repositories.*;
import com.google.protobuf.Empty;
import com.google.protobuf.Int32Value;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.checkerframework.checker.units.qual.A;

import javax.print.Doc;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EmergencyCareServant extends EmergencyCareServiceGrpc.EmergencyCareServiceImplBase {

    private final RoomRepository roomRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final AttentionRepository attentionRepository;
    private final NotificationRepository notificationRepository;

    public EmergencyCareServant(RoomRepository roomRepository, DoctorRepository doctorRepository, PatientRepository patientRepository, AttentionRepository attentionRepository, NotificationRepository notificationRepository) {
        this.roomRepository = roomRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.attentionRepository = attentionRepository;
        this.notificationRepository =  notificationRepository;
    }

    private AttentionResponse exitWithoutError(int number,StreamObserver<AttentionResponse> responseObserver){
        return  AttentionResponse.newBuilder()
                .setStatus(-1)
                .setRoom(number)
                .build();

    }

    private AttentionResponse exitRoomOccupied(int number,StreamObserver<AttentionResponse> responseObserver){
        return AttentionResponse.newBuilder()
                .setStatus(-2)
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
                .setStatus(0)
                .build();
        attentionRepository.startAttention(response);
        return response;
    }
    @Override
    public void startAttention(Int32Value request, StreamObserver<AttentionResponse> responseObserver) {
        AttentionResponse response = attention(request,responseObserver);
        if(response.getStatus() < 0){
            responseObserver.onError(Status.INVALID_ARGUMENT.withDescription("The attention could not be executed").asRuntimeException());
        }
        Doctor doctor = doctorRepository.getDoctorByName(response.getDoctor());
        notificationRepository.notify(doctor);
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
            responseObserver.onError(Status.INVALID_ARGUMENT.withDescription("El att response es null").asRuntimeException());
        }
        if(attentionResponse == null || doctorRepository.getDoctorByName(request.getDoctor()) == null){
            responseObserver.onError(Status.INVALID_ARGUMENT.withDescription("The attention could not be finished").asRuntimeException());
        }
        roomRepository.setFree(attentionResponse.getRoom());
        doctorRepository.changeAvailability(RequestDoctor.newBuilder()
                .setName(request.getDoctor())
                .setAvailability(Availability.AVAILABILITY_AVAILABLE)
                .build());
        Patient patient = patientRepository.getPatient(request.getPatient());
        patientRepository.changeStatus(patient.getName(),patient.getLevel(),State.STATE_FINISHED);
        notificationRepository.notify(request.getDoctor(),attentionResponse,NotificationType.NOTIFICATION_FINISH_ATTENTION);
        responseObserver.onNext(attentionRepository.finishAttention(attentionResponse));
        responseObserver.onCompleted();
    }
}
