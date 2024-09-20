package ar.edu.itba.pod.client;

import ar.edu.itba.pod.grpc.administrationService.AdministrationServiceGrpc;
import ar.edu.itba.pod.grpc.common.*;
import ar.edu.itba.pod.grpc.emergencyCareService.EmergencyCareServiceGrpc;
import com.google.protobuf.Empty;
import com.google.protobuf.Int32Value;
import com.google.protobuf.StringValue;
import io.grpc.ManagedChannel;

import java.util.concurrent.TimeUnit;

public class EmergencyCareClient {
    public static void main(String[] args) throws InterruptedException {
        ManagedChannel channel = ChannelCreator.createChannel();

        try {
            EmergencyCareServiceGrpc.EmergencyCareServiceBlockingStub blockingStub = EmergencyCareServiceGrpc.newBlockingStub(channel);

            switch (System.getProperty("action")) {
                case "carePatient":
                    AttentionResponse attentionResponse = blockingStub.startAttention(Int32Value.of(Integer.parseInt(System.getProperty("room"))));
                    if(attentionResponse.getPatientLevel() == -2){
                        System.out.println("Room #"+attentionResponse.getRoom()+" remains Occupied");
                    }else if(attentionResponse.getPatientLevel() == -1){
                        System.out.println("Room #"+attentionResponse.getRoom()+" remains Free");
                    }else {
                        System.out.println("Patient " + attentionResponse.getPatient() + "(" + attentionResponse.getPatientLevel() + ") and Doctor " + attentionResponse.getDoctor() + " (" + attentionResponse.getDoctorLevel() + ") are now in room #" + attentionResponse.getRoom());
                    }
                    break;
                case "careAllPatients":

                    break;
                case "dischargePatient":

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
