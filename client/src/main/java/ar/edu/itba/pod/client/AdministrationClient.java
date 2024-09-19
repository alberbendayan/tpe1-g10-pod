package ar.edu.itba.pod.client;

public class AdministrationClient {
    public static void main(String[] args) {
        switch(System.getProperty("action")) {
            case "addRoom":
                //llamar a addRoom
                break;
            case "addDoctor":
                //llamar a addDoctor(System.getProperty("doctor"), System.getProperty("level"))
                break;
            case "setDoctor":
                //llamar a setDoctor(System.getProperty("doctor"), System.getProperty("availability")
                break;
            case "checkDoctor":
                //llamar a checkDoctor(System.getProperty("doctor"))
                break;
            default:
                System.out.println("Invalid action");
                break;
        }
        //ChannelCreator.createChannel(System.getProperty("serverAddress"));
    }

}
