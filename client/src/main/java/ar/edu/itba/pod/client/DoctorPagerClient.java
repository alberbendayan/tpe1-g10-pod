package ar.edu.itba.pod.client;

import ar.edu.itba.pod.grpc.common.Attention;
import ar.edu.itba.pod.grpc.common.AttentionResponse;
import ar.edu.itba.pod.grpc.common.Doctor;
import ar.edu.itba.pod.grpc.common.Notification;
import ar.edu.itba.pod.grpc.doctorPageService.DoctorPageServiceGrpc;
import ar.edu.itba.pod.grpc.emergencyCareService.EmergencyCareServiceGrpc;
import com.google.protobuf.Empty;
import com.google.protobuf.Int32Value;
import com.google.protobuf.StringValue;
import io.grpc.ManagedChannel;

import java.util.Iterator;
import java.util.concurrent.TimeUnit;

public class DoctorPagerClient {
    public static void main(String[] args) throws InterruptedException {
        ManagedChannel channel = ChannelCreator.createChannel();

        try {
            DoctorPageServiceGrpc.DoctorPageServiceBlockingStub blockingStub = DoctorPageServiceGrpc.newBlockingStub(channel);

            switch (System.getProperty("action")) {
                case "register":
                    try {
                        Iterator<Notification> notifications = blockingStub.registerDoctor(StringValue.of(System.getProperty("doctor")));
                        while (notifications.hasNext()) {
                            Notification notification = notifications.next();
                            Doctor doctor= notification.getDoctor();
                            AttentionResponse attentionResponse = notification.getAttention();
                            switch (notification.getType()) {
                                case NOTIFICATION_SUBSCRIBE:
                                    System.out.println("Doctor " + doctor.getName() + " ("+ doctor.getLevel()+ ") registered successfully for pager");
                                    break;
                                case NOTIFICATION_UNSUBSCRIBE:
                                    System.out.println("Doctor " + doctor.getName() + " ("+ doctor.getLevel()+ ") unregistered successfully for pager");
                                    break;
                                case NOTIFICATION_DOCTOR_SET_AVAILABILITY:
                                    String availabilityMessage;
                                    switch (doctor.getAvailability()){
                                        case AVAILABILITY_AVAILABLE:
                                            availabilityMessage="Available";
                                            break;
                                        case AVAILABILITY_UNAVAILABLE:
                                            availabilityMessage="Unavailable";
                                            break;
                                        case AVAILABILITY_ATTENDING:
                                            availabilityMessage="Attending";
                                            break;
                                        default:
                                            availabilityMessage="";
                                            break;
                                    }
                                    System.out.println("Doctor " + doctor.getName() + " (" + doctor.getLevel() + ") is " + availabilityMessage );
                                    break;
                                case NOTIFICATION_START_ATTENTION:
                                    System.out.println("Patient " + attentionResponse.getPatient() + " (" + attentionResponse.getPatientLevel() + ") and Doctor " + attentionResponse.getDoctor() + " (" + attentionResponse.getDoctorLevel() + ") are now in room #" + attentionResponse.getRoom());
                                    break;
                                case NOTIFICATION_FINISH_ATTENTION:
                                    System.out.println("Patient " + attentionResponse.getPatient() + " (" + attentionResponse.getPatientLevel() + ") has been discharged from Doctor " + attentionResponse.getDoctor() + " (" + attentionResponse.getDoctorLevel() + ") and the room room #" + attentionResponse.getRoom() + " is now Free");
                                    break;
                                default:
                                    System.out.println("Invalid notification type");
                                    break;
                            }
                        }
                    }catch (Exception e){
                        System.out.println(e.getMessage());
                    }
                    break;
                case "unregister":
                    try {
                        Notification notification  = blockingStub.unsuscribeDoctor(StringValue.of(System.getProperty("doctor")));
                        System.out.println("Doctor " + notification.getDoctor().getName() + " ("+ notification.getDoctor().getLevel()+ ") unregistered successfully for pager");
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
