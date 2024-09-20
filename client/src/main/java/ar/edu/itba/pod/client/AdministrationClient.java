package ar.edu.itba.pod.client;

import ar.edu.itba.pod.grpc.administrationService.AdministrationServiceGrpc;
import com.google.protobuf.Empty;
import io.grpc.ManagedChannel;

import java.util.concurrent.TimeUnit;

public class AdministrationClient {
    public static void main(String[] args) throws InterruptedException {
        ManagedChannel channel = ChannelCreator.createChannel();

        try {
            AdministrationServiceGrpc.AdministrationServiceBlockingStub blockingStub = AdministrationServiceGrpc.newBlockingStub(channel);
            switch (System.getProperty("action")) {
                case "addRoom":
                    blockingStub.addRoom(Empty.newBuilder().build());
                    break;
                case "addDoctor":
                    //blockingStub.addDoctor(System.getProperty("doctor"), System.getProperty("level"))
                    break;
                case "setDoctor":
                    //blockingStub.setDoctor(System.getProperty("doctor"), System.getProperty("availability")
                    break;
                case "checkDoctor":
                    //blockingStub.checkDoctor(System.getProperty("doctor"))
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
