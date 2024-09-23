package ar.edu.itba.pod.client;

import ar.edu.itba.pod.grpc.administrationService.AdministrationServiceGrpc;
import ar.edu.itba.pod.grpc.common.*;
import ar.edu.itba.pod.grpc.emergencyCareService.EmergencyCareServiceGrpc;
import com.google.protobuf.Empty;
import com.google.protobuf.Int32Value;
import com.google.protobuf.StringValue;
import io.grpc.ManagedChannel;

import java.util.Iterator;
import java.util.concurrent.TimeUnit;

public class EmergencyCareClient {
    public static void main(String[] args) throws InterruptedException {
        ManagedChannel channel = ChannelCreator.createChannel();

        try {
            EmergencyCareServiceGrpc.EmergencyCareServiceBlockingStub blockingStub = EmergencyCareServiceGrpc.newBlockingStub(channel);

            switch (System.getProperty("action")) {
                case "carePatient":
                    try {
                        AttentionResponse attentionResponse = blockingStub.startAttention(Int32Value.of(Integer.parseInt(System.getProperty("room"))));
                        System.out.println("Patient " + attentionResponse.getPatient() + " (" + attentionResponse.getPatientLevel() + ") and Doctor " + attentionResponse.getDoctor() + " (" + attentionResponse.getDoctorLevel() + ") are now in room #" + attentionResponse.getRoom());
                    }catch (Exception e){
                        System.out.println(e.getMessage());
                    }
                    break;
                case "careAllPatients":
                    // TODO: VER QUE ONDA LA EXCEPTION CON EL ITERADOR O PREGUNTAR SI ESTA BIEN EL STATUS
                    Iterator<AttentionResponse> attentionResponses = blockingStub.startAllAttention(Empty.getDefaultInstance());
                    for (Iterator<AttentionResponse> it = attentionResponses; it.hasNext(); ) {
                        AttentionResponse a = it.next();
                        if (a.getStatus() == -2) {
                            System.out.println("Room #" + a.getRoom() + " remains Occupied");
                        } else if (a.getStatus() == -1) {
                            System.out.println("Room #" + a.getRoom() + " remains Free");
                        } else {
                            System.out.println("Patient " + a.getPatient() + " (" + a.getPatientLevel() + ") and Doctor " + a.getDoctor() + " (" + a.getDoctorLevel() + ") are now in room #" + a.getRoom());
                        }
                    }
                    break;
                case "dischargePatient":
                    try {
                        AttentionResponse response = blockingStub.finishAttention(Attention.newBuilder()
                                .setPatient(System.getProperty("patient"))
                                .setDoctor(System.getProperty("doctor"))
                                .setRoom(Integer.parseInt(System.getProperty("room")))
                                .build());
                        System.out.println("Patient " + response.getPatient() + " (" + response.getPatientLevel() + ") has been discharged from Doctor " + response.getDoctor() + " (" + response.getDoctorLevel() + ") and the room room #" + response.getRoom() + " is now Free");
                    }catch (Exception e){
                        System.out.println(e.getMessage());
                    }
                    break;
                default:
                    System.out.println("Invalid action");
                    break;
            }
        } finally {
            channel.shutdown().awaitTermination(10, TimeUnit.SECONDS);
        }
    }

}
