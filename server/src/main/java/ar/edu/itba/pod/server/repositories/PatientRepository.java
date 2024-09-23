package ar.edu.itba.pod.server.repositories;

import ar.edu.itba.pod.grpc.common.Patient;
import ar.edu.itba.pod.grpc.common.PatientTime;
import ar.edu.itba.pod.grpc.common.RequestPatient;
import ar.edu.itba.pod.grpc.common.State;
import com.google.protobuf.Timestamp;

import java.security.spec.RSAOtherPrimeInfo;
import java.time.Instant;
import java.util.*;

public class PatientRepository {

    private SortedSet<Patient>[] waitingPatients;

    private Map<String,Patient> patients;
    private final int QTY_LEVELS = 5;

    public PatientRepository() {
        waitingPatients = new SortedSet[QTY_LEVELS];
        patients = new LinkedHashMap<>();

        for (int i = 0; i < QTY_LEVELS; i++) {
            waitingPatients[i] = new TreeSet<>((o1, o2) -> {
                Timestamp t1 = o1.getTime();
                Timestamp t2 = o2.getTime();
                if (t1.getSeconds() == t2.getSeconds()) {
                    if (t1.getNanos() == t2.getNanos()) {
                        return o1.getName().compareTo(o2.getName());
                    }
                    return t1.getNanos() - t2.getNanos();
                }
                return Long.compare(t1.getSeconds(), t2.getSeconds());
            });
        }
    }

    public Patient addPatient(RequestPatient requestPatient) {
        int level = requestPatient.getLevel();
        String name = requestPatient.getName();

        if (level < 1 || level > 5) {
            return Patient.newBuilder().setLevel(-1).build();
        }
        if (patients.containsKey(name)) {
            return Patient.newBuilder().setLevel(-2).build();
        }

        Instant now = Instant.now();
        Timestamp timestamp= Timestamp.newBuilder()
                .setSeconds(now.getEpochSecond())
                .setNanos(now.getNano())
                .build();

        Patient patient = Patient.newBuilder()
                .setName(name)
                .setLevel(level)
                .setTime(timestamp)
                .build();

        patients.put(name,patient);
        waitingPatients[level - 1].add(patient);
        return patient;
    }

    public Patient updateLevel(String name, int newLevel) {
        for (int i = 0; i < QTY_LEVELS; i++) {
            for (Patient p : waitingPatients[i]) {
                if (p.getName().equals(name) && newLevel != patients.get(name).getLevel()) {
                    waitingPatients[i].remove(patients.get(name));
                    Patient patient = Patient.newBuilder()
                            .setLevel(newLevel)
                            .setName(name)
                            .setState(State.STATE_WAITING)
                            .build();

                    waitingPatients[newLevel - 1].add(patients.get(name));
                    patients.remove(name);
                    patients.put(name,patient);
                    return patient;
                }
            }
        }
        return Patient.newBuilder().setLevel(-2).build();
    }

    public PatientTime checkPatient(String name) {
        int counter = 0;
        for (int i = QTY_LEVELS - 1; i >= 0; i--) {
            for (Patient p : waitingPatients[i]) {
                if (p.getName().equals(name)) {
                    return PatientTime.newBuilder()
                            .setPatient(patients.get(name))
                            .setPatientsAhead(counter)
                            .build();
                }
                counter++;
            }
        }
        return null; //TODO ver si aca tirar error code
    }

    public Patient getMostUrgentPatient(){
        for(int i = QTY_LEVELS-1;i>=0;i--){
            if(!waitingPatients[i].isEmpty()){
                return waitingPatients[i].iterator().next();
            }
        }
        return null;
    }

    public Patient changeStatus(String name,int level,State state){
        if(state != State.STATE_WAITING){
            waitingPatients[level-1].remove(patients.get(name));
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

    public Patient getPatient(String name){
        if(patients.containsKey(name))
            return patients.get(name);
        return null;
    }

    public List<Patient> getPatientsInWaitingRoom(){
        List<Patient> list = new ArrayList<>();
        for (int i = QTY_LEVELS-1;i>=0;i--){
            list.addAll(waitingPatients[i]);
        }
        return list;
    }
}
