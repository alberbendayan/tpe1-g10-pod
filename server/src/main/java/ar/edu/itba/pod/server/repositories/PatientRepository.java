package ar.edu.itba.pod.server.repositories;

import ar.edu.itba.pod.grpc.common.Patient;
import ar.edu.itba.pod.grpc.common.PatientTime;
import ar.edu.itba.pod.grpc.common.RequestPatient;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class PatientRepository {

    private Queue<Patient>[] patients;
    private final int QTY_LEVELS = 5;

    public PatientRepository() {
        patients = new LinkedList[QTY_LEVELS];
        for (int i = 0; i < patients.length; i++) {
            patients[i] = new LinkedList<>();
        }
    }

    public Patient addPatient(RequestPatient requestPatient) {
        int level = requestPatient.getLevel();
        String name = requestPatient.getName();
        Patient patient = Patient.newBuilder().setName(name).setLevel(level).build();

        if (level < 1 || level > 5) {
            //TODO: error
        }
        for (int i = 0; i < QTY_LEVELS; i++) {
            if (patients[i].contains(name)) {
                // TODO: error xq ya existe el dr
            }
        }
        patients[level - 1].add(patient);

        return patient;
    }

    public Patient updateLevel(String name, int newLevel) {
        for (int i = 0; i < QTY_LEVELS; i++) {
            for (Patient p : patients[i]) {
                if (p.getName().equals(name)) {
                    patients[i].remove(p);
                    patients[newLevel - 1].add(p);
                    return p;
                }
            }
        }
        return null; //TODO ver si aca tirar error code
    }

    public PatientTime checkPatient(String name) {
        int counter = 0;
        for (int i = QTY_LEVELS - 1; i >= 0; i--) {
            for (Patient p : patients[i]) {
                if (p.getName().equals(name)) {
                    return PatientTime.newBuilder().setPatient(p).setPatientsAhead(counter).build();
                }
                counter++;
            }
        }
        return null; //TODO ver si aca tirar error code
    }


}
