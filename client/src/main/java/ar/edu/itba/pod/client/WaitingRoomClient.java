package ar.edu.itba.pod.client;

import ar.edu.itba.pod.grpc.administrationService.AdministrationServiceGrpc;
import ar.edu.itba.pod.grpc.common.Patient;
import ar.edu.itba.pod.grpc.common.PatientTime;
import ar.edu.itba.pod.grpc.common.RequestDoctorLevel;
import ar.edu.itba.pod.grpc.common.RequestPatient;
import ar.edu.itba.pod.grpc.waitingRoomService.WaitingRoomServiceGrpc;
import com.google.protobuf.Empty;
import com.google.protobuf.StringValue;
import io.grpc.ManagedChannel;

import java.util.concurrent.TimeUnit;

public class WaitingRoomClient {
    public static void main(String[] args) throws InterruptedException {
        ManagedChannel channel = ChannelCreator.createChannel();

        try {
            WaitingRoomServiceGrpc.WaitingRoomServiceBlockingStub blockingStub = WaitingRoomServiceGrpc.newBlockingStub(channel);
            Patient patient = null;
            switch (System.getProperty("action")) {
                case "addPatient":
                    try {
                        patient = blockingStub.addPatient(RequestPatient.newBuilder()
                                .setName(System.getProperty("patient"))
                                .setLevel(Integer.parseInt(System.getProperty("level")))
                                .build());
                        System.out.println("Patient " + patient.getName() + " (" + patient.getLevel() + ") is in the waiting room");
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                    break;
                case "updateLevel":
                    try {
                        patient = blockingStub.updateLevel(RequestPatient.newBuilder()
                                .setName(System.getProperty("patient"))
                                .setLevel(Integer.parseInt(System.getProperty("level")))
                                .build()
                        );
                        System.out.println("Patient " + patient.getName() + " (" + patient.getLevel() + ") is in the waiting room");
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                    break;
                case "checkPatient":
                    try {
                        PatientTime patientTime = blockingStub.checkPatient(StringValue.of(System.getProperty("patient")));
                        System.out.println("Patient " + patientTime.getPatient().getName() + " (" + patientTime.getPatient().getLevel() + ") is in the waiting room with " + patientTime.getPatientsAhead() + " patients ahead");
                    } catch (Exception e) {
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
