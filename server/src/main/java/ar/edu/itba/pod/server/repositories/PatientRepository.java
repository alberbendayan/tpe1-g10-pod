package ar.edu.itba.pod.server.repositories;

import ar.edu.itba.pod.grpc.common.Patient;
import ar.edu.itba.pod.grpc.common.PatientTime;
import ar.edu.itba.pod.grpc.common.RequestPatient;
import ar.edu.itba.pod.grpc.common.State;

import java.util.*;

public class PatientRepository {

    //En la queue solo los q esperan
    private Queue<String>[] waitingPatients;

    //Aca los que estan siendo atendidos y los que terminaron
    private Map<String,Patient> patients;
    private final int QTY_LEVELS = 5;

    public PatientRepository() {
        waitingPatients = new LinkedList[QTY_LEVELS];
        patients = new LinkedHashMap<>();
        for (int i = 0; i < QTY_LEVELS; i++) {
            waitingPatients[i] = new LinkedList<>();

        }
    }

    public Patient addPatient(RequestPatient requestPatient) {
        int level = requestPatient.getLevel();
        String name = requestPatient.getName();

        if (level < 1 || level > 5) {
            //TODO: error
        }
        if (patients.containsKey(name)) {
            // TODO: error xq ya existe el patient
        }
        waitingPatients[level - 1].add(name);
        Patient patient = Patient.newBuilder()
                .setName(name)
                .setLevel(level)
                .build();
        patients.put(name,patient);
        return patient;
    }

    public Patient updateLevel(String name, int newLevel) {
        for (int i = 0; i < QTY_LEVELS; i++) {
            for (String p : waitingPatients[i]) {
                if (p.equals(name) && newLevel != patients.get(name).getLevel()) {
                    waitingPatients[i].remove(name);
                    Patient patient = Patient.newBuilder()
                            .setLevel(newLevel)
                            .setName(name)
                            .setState(State.STATE_WAITING)
                            .build();

                    waitingPatients[newLevel - 1].add(name);
                    patients.remove(name);
                    patients.put(name,patient);
                    return patient;
                }
            }
        }
        return null; //TODO ver si aca tirar error code
    }

    public PatientTime checkPatient(String name) {
        int counter = 0;
        for (int i = QTY_LEVELS - 1; i >= 0; i--) {
            for (String p : waitingPatients[i]) {
                if (p.equals(name)) {
                    return PatientTime.newBuilder()
                            .setPatient(patients.get(p))
                            .setPatientsAhead(counter)
                            .build();
                }
                counter++;
            }
        }
        return null; //TODO ver si aca tirar error code
    }

    public Patient getMostUrgentPatientFromLevel(int level){
        for(int i = level -1;i>=0;i++){
            if(!waitingPatients[i].isEmpty()){
                String name = waitingPatients[i].poll();
                return patients.get(name);
            }
        }
        return null;
    }

    public Patient changeStatus(String name,int level,State state){
        if(state != State.STATE_WAITING){
            waitingPatients[level-1].remove(name);
        }
        Patient patient = Patient.newBuilder()
                .setState(state)
                .setName(name)
                .setLevel(level)
                .build();
        patients.remove(name);
        patients.put(name,patient);
        return patient;
    }

}
