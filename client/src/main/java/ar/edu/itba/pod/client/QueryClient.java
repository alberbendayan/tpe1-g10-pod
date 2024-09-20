package ar.edu.itba.pod.client;

import ar.edu.itba.pod.grpc.administrationService.AdministrationServiceGrpc;
import ar.edu.itba.pod.grpc.common.*;
import ar.edu.itba.pod.grpc.queryClientService.QueryClientServiceGrpc;
import com.google.protobuf.Empty;
import com.google.protobuf.StringValue;
import io.grpc.ManagedChannel;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

public class QueryClient {
    public static void main(String[] args) throws InterruptedException {
        ManagedChannel channel = ChannelCreator.createChannel();

        try {
            QueryClientServiceGrpc.QueryClientServiceBlockingStub blockingStub = QueryClientServiceGrpc.newBlockingStub(channel);
            String path=System.getProperty("outPath");
            StringBuilder content= new StringBuilder();
            switch (System.getProperty("action")) {
                case "queryRooms":
                    content.append("Room,Status,Patient,Doctor\n");
                    Iterator<AttentionResponse> rooms=blockingStub.getRooms(StringValue.of(System.getProperty("outPath")));
                    while(rooms.hasNext()) {
                        AttentionResponse room = rooms.next();
                        if(room.getIsEmpty()){
                            content.append(room.getRoom()+",Free,,\n");
                        }
                        else{
                            content.append(room.getRoom()+",Occupied,"+room.getPatient()+" ("+room.getPatientLevel()+"),"+room.getDoctor()+" ("+room.getDoctorLevel()+")\n");
                        }
                    }

                    break;
                case "queryWaitingRoom":

                    break;
                case "queryCares":

                    break;
                default:
                    System.out.println("Invalid action");
                    break;
            }
            try (FileWriter escritor = new FileWriter(path)) {
                escritor.write(content.toString());
            } catch (IOException e) {
                System.out.println("Error: creating CSV.");}
        } finally {
            channel.shutdown().awaitTermination(10, TimeUnit.SECONDS);
        }
    }


}
