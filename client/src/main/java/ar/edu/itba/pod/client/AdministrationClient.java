package ar.edu.itba.pod.client;

import ar.edu.itba.pod.grpc.administrationService.AdministrationServiceGrpc;
import ar.edu.itba.pod.grpc.common.*;
import com.google.protobuf.Empty;
import com.google.protobuf.StringValue;
import io.grpc.ManagedChannel;

import java.util.concurrent.TimeUnit;

public class AdministrationClient {
    public static void main(String[] args) throws InterruptedException {
        ManagedChannel channel = ChannelCreator.createChannel();

        try {
            AdministrationServiceGrpc.AdministrationServiceBlockingStub blockingStub = AdministrationServiceGrpc.newBlockingStub(channel);
            Doctor doctor = null;
            String availabilityMessage;
            switch (System.getProperty("action")) {
                case "addRoom":
                    Room room = blockingStub.addRoom(Empty.newBuilder().build());
                    System.out.println("Room #" + room.getId() + " added successfully");
                    break;
                case "addDoctor":
                    RequestDoctorLevel requestDoctorLevel= RequestDoctorLevel.newBuilder()
                            .setName(System.getProperty("doctor"))
                            .setLevel(Integer.parseInt(System.getProperty("level")))
                            .build();
                    doctor = blockingStub.addDoctor(requestDoctorLevel);
                    System.out.println("Doctor " + doctor.getName() + " (" + doctor.getLevel() + ") added successfully");
                    break;
                case "setDoctor":
                    Availability availability;
                    switch (System.getProperty("availability")) {
                        case "available":
                            availability = Availability.AVAILABILITY_AVAILABLE;
                            availabilityMessage = "Available";
                            break;
                        case "unavailable":
                            availability = Availability.AVAILABILITY_UNAVAILABLE;
                            availabilityMessage = "Unavailable";

                            break;
                        case "attending":
                            availability = Availability.AVAILABILITY_ATTENDING;
                            availabilityMessage = "Attending";
                            break;
                        default:
                            throw new RuntimeException(); //TODO: check exception

                    }
                    doctor = blockingStub.setDoctor(RequestDoctor.newBuilder()
                            .setName(System.getProperty("doctor"))
                            .setAvailability(availability)
                            .build());
                    System.out.println("Doctor " + doctor.getName() + " (" + doctor.getLevel() + ") is " + availabilityMessage);
                    break;
                case "checkDoctor":
                    doctor = blockingStub.checkDoctor(StringValue.of(System.getProperty("doctor")));
                    availabilityMessage = switch (doctor.getAvailability()) {
                        case AVAILABILITY_AVAILABLE -> "Available";
                        case AVAILABILITY_UNAVAILABLE -> "Unavailable";
                        case AVAILABILITY_ATTENDING -> "Attending";
                        default -> throw new RuntimeException(); //TODO: check exception
                    };
                    System.out.println("Doctor " + doctor.getName() + " (" + doctor.getLevel() + ") is " + availabilityMessage);
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
