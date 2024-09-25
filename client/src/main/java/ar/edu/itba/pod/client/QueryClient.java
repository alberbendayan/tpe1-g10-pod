package ar.edu.itba.pod.client;

import ar.edu.itba.pod.grpc.administrationService.AdministrationServiceGrpc;
import ar.edu.itba.pod.grpc.common.*;
import ar.edu.itba.pod.grpc.queryClientService.QueryClientServiceGrpc;
import com.google.protobuf.Empty;
import com.google.protobuf.Int32Value;
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
            String path = System.getProperty("outPath");
            StringBuilder content = new StringBuilder();
            switch (System.getProperty("action")) {
                case "queryRooms":
                    try {
                        content.append("Room,Status,Patient,Doctor\n");
                        Iterator<AttentionResponse> rooms = blockingStub.getRooms(Empty.getDefaultInstance());
                        while (rooms.hasNext()) {
                            AttentionResponse room = rooms.next();
                            if (room.getIsEmpty()) {
                                content.append(room.getRoom() + ",Free,,\n");
                            } else {
                                content.append(room.getRoom() + ",Occupied," + room.getPatient() + " (" + room.getPatientLevel() + ")," + room.getDoctor() + " (" + room.getDoctorLevel() + ")\n");
                            }
                        }
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                    break;
                case "queryWaitingRoom":
                    try {
                        content.append("Patient,Level\n");
                        Iterator<Patient> patients = blockingStub.getPatients(Empty.getDefaultInstance());
                        while (patients.hasNext()) {
                            Patient patient = patients.next();
                            content.append(patient.getName() + "," + patient.getLevel() + "\n");
                        }
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                    break;
                case "queryCares":
                    try {
                        String roomProperty = System.getProperty("room");
                        Integer room = null;
                        if (roomProperty != null) {
                            try {
                                room = Integer.parseInt(roomProperty);
                            } catch (NumberFormatException e) {
                                System.out.println("El parámetro 'room' no es un número válido.");
                            }
                        }

                        content.append("Room,Patient,Doctor\n");
                        Iterator<AttentionResponse> attentionIterator;
                        if (room != null) {
                            attentionIterator = blockingStub.getAttentionsRoom(Int32Value.of(room));
                        } else {
                            attentionIterator = blockingStub.getAttentions(Empty.getDefaultInstance());
                        }
                        while (attentionIterator.hasNext()) {
                            AttentionResponse a = attentionIterator.next();
                            content.append(a.getRoom() + "," + a.getPatient() + " (" + a.getPatientLevel() + ")," + a.getDoctor() + " (" + a.getDoctorLevel() + ")\n");
                        }
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                    break;
                default:
                    System.out.println("Invalid action");
                    break;
            }
            if (!content.isEmpty()) {
                try (FileWriter escritor = new FileWriter(path)) {
                    escritor.write(content.toString());
                } catch (IOException e) {
                    System.out.println("Error: creating CSV.");
                }
            }
        } finally {
            channel.shutdown().awaitTermination(10, TimeUnit.SECONDS);
        }
    }


}
