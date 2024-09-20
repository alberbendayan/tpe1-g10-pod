package ar.edu.itba.pod.server.repositories;

import ar.edu.itba.pod.grpc.common.Patient;

import java.util.HashMap;
import java.util.Map;

public class PatientRepository {

    private Map<String, Patient>[] patients;
    private final int CANT_LEVELS = 5;

    public PatientRepository() {
        patients = new Map[CANT_LEVELS];
        for (int i = 0; i < patients.length; i++) {
            patients[i] = new HashMap<>();
        }
    }

    public Patient addPatient(Patient patient){

        int level = patient.getLevel();
        String name = patient.getName();

        if(level<1 || level>5){
            // error nivel invalido
        }
        for(int i = 0; i< CANT_LEVELS; i++){
            if(patients[i].containsKey(name)){
                // error xq ya existe el dr
            }
        }
        patients[level-1].put(name,patient);

        return patient;
    }
}
