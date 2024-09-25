package ar.edu.itba.pod.client;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChannelCreator {

    private static final Logger logger = LoggerFactory.getLogger(ChannelCreator.class);

    public static ManagedChannel createChannel() {

        String serverAddress = System.getProperty("serverAddress");

        if (serverAddress != null && serverAddress.contains(":")) {
            // Separar la dirección y el puerto
            String[] parts = serverAddress.split(":");
            String address = parts[0];
            String port = parts[1];


            logger.info("tpe1-g10 Client Starting ...");
            logger.info("grpc-com-patterns Client Starting ...");
            return ManagedChannelBuilder.forAddress(address, Integer.parseInt(port))
                    .usePlaintext()
                    .build();
        } else {
            System.out.println("Invalid server address");
            throw new IllegalArgumentException("Invalid server address");
        }
    }
}

