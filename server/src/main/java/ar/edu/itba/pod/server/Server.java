package ar.edu.itba.pod.server;

import ar.edu.itba.pod.server.repositories.*;
import ar.edu.itba.pod.server.servants.*;
import io.grpc.ServerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Server {
    private static Logger logger = LoggerFactory.getLogger(Server.class);

    public static void main(String[] args) throws InterruptedException, IOException {
        logger.info(" Server Starting ...");

        RoomRepository roomRepository = new RoomRepository();
        DoctorRepository doctorRepository = new DoctorRepository();
        PatientRepository patientRepository = new PatientRepository();
        AttentionRepository attentionRepository = new AttentionRepository();
        NotificationRepository notificationRepository = new NotificationRepository();

        int port = 50052;
        io.grpc.Server server = ServerBuilder.forPort(port)
                .addService(new DoctorPagerServant(roomRepository, doctorRepository, patientRepository,attentionRepository,notificationRepository))
                .addService(new AdministrationServant(roomRepository, doctorRepository, notificationRepository))
                .addService(new QueryClientServant(roomRepository, patientRepository, attentionRepository))
                .addService(new EmergencyCareServant(roomRepository, doctorRepository, patientRepository,attentionRepository,notificationRepository))
                .addService(new WaitingRoomServant(patientRepository))
                .build();
        server.start();
        logger.info("Server started, listening on " + port);
        server.awaitTermination();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Shutting down gRPC server since JVM is shutting down");
            server.shutdown();
            logger.info("Server shut down");
        }));
    }
}
