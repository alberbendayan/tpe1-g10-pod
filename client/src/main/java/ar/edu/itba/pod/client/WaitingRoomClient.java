package ar.edu.itba.pod.client;

import ar.edu.itba.pod.grpc.administrationService.AdministrationServiceGrpc;
import ar.edu.itba.pod.grpc.common.Patient;
import ar.edu.itba.pod.grpc.common.RequestDoctorLevel;
import ar.edu.itba.pod.grpc.common.RequestPatient;
import ar.edu.itba.pod.grpc.waitingRoomService.WaitingRoomServiceGrpc;
import com.google.protobuf.Empty;
import io.grpc.ManagedChannel;

import java.util.concurrent.TimeUnit;

public class WaitingRoomClient {
    public static void main(String[] args) throws InterruptedException {
        ManagedChannel channel = ChannelCreator.createChannel();

        try {
            WaitingRoomServiceGrpc.WaitingRoomServiceBlockingStub blockingStub = WaitingRoomServiceGrpc.newBlockingStub(channel);
            Patient patient=null;
            switch (System.getProperty("action")) {
                case "addPatient":
                   patient= blockingStub.addPatient(RequestPatient.newBuilder()
                            .setName(System.getProperty("patient"))
                            .setLevel(Integer.parseInt(System.getProperty("level")))
                            .build());
                    System.out.println("Patient "+ patient.getName()+" ("+patient.getLevel()+") is in the waiting room");
                    break;
                case "updateLevel":
                    patient = blockingStub.updateLevel(RequestPatient.newBuilder()
                            .setName(System.getProperty("patient"))
                            .setLevel(Integer.parseInt(System.getProperty("level")))
                            .build()
                    );
                    System.out.println("Patient "+ patient.getName()+" ("+patient.getLevel()+") is in the waiting room");

                    break;
                case "checkPatient":
                    blockingStub.checkPatient(System.getProperty("patient"));
                    break;
                default:
                    System.out.println("Invalid action");
                    break;
            }
        }
        finally {
            channel.shutdown().awaitTermination(10, TimeUnit.SECONDS);
        }
    }

}
