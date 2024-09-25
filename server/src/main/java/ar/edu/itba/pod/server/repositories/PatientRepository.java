package ar.edu.itba.pod.server.repositories;

import ar.edu.itba.pod.grpc.common.Patient;
import ar.edu.itba.pod.grpc.common.PatientTime;
import ar.edu.itba.pod.grpc.common.RequestPatient;
import ar.edu.itba.pod.grpc.common.State;
import com.google.protobuf.Timestamp;

import java.security.spec.RSAOtherPrimeInfo;
import java.time.Instant;
import java.util.*;

import ar.edu.itba.pod.grpc.common.Patient;
import ar.edu.itba.pod.grpc.common.PatientTime;
import ar.edu.itba.pod.grpc.common.RequestPatient;
import ar.edu.itba.pod.grpc.common.State;
import com.google.protobuf.Timestamp;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class PatientRepository {

    private final SortedSet<Patient>[] waitingPatients;
    private final Map<String, Patient> patients;
    private final int QTY_LEVELS = 5;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

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
        lock.writeLock().lock();
        try {
            int level = requestPatient.getLevel();
            String name = requestPatient.getName();

            if (level < 1 || level > 5) {
                return Patient.newBuilder().setLevel(-1).build();
            }
            if (patients.containsKey(name)) {
                return Patient.newBuilder().setLevel(-2).build();
            }

            Instant now = Instant.now();
            Timestamp timestamp = Timestamp.newBuilder()
                    .setSeconds(now.getEpochSecond())
                    .setNanos(now.getNano())
                    .build();

            Patient patient = Patient.newBuilder()
                    .setName(name)
                    .setLevel(level)
                    .setTime(timestamp)
                    .build();

            patients.put(name, patient);
            waitingPatients[level - 1].add(patient);
            return patient;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public Patient updateLevel(String name, int newLevel) {
        lock.writeLock().lock();
        Patient oldPatient = patients.get(name);

        try {
            if(!waitingPatients[oldPatient.getLevel()-1].remove(oldPatient) || patients.remove(name) == null){
                return Patient.newBuilder().setLevel(-2).build();
            }
            Patient patient = Patient.newBuilder()
                    .setLevel(newLevel)
                    .setName(name)
                    .setState(State.STATE_WAITING)
                    .build();

            waitingPatients[newLevel - 1].add(patient);
            patients.put(name, Patient.newBuilder()
                    .setLevel(newLevel)
                    .setName(name)
                    .setState(State.STATE_WAITING)
                    .build());
            return patient;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public PatientTime checkPatient(String name) {
        lock.readLock().lock();
        try {
            Patient patient = patients.get(name);
            int counter = 0;
            int i;
            for (i = QTY_LEVELS - 1; i >= patient.getLevel(); i--) {
                counter += waitingPatients[i].size();
            }
            for (Patient p : waitingPatients[patient.getLevel() - 1]) {
                if (p.getName().equals(name)) {
                    return PatientTime.newBuilder()
                            .setPatient(patients.get(name))
                            .setPatientsAhead(counter)
                            .build();
                }
                counter++;
            }
            return null;
        } finally {
            lock.readLock().unlock();
        }
    }

    public Patient getMostUrgentPatient() {
        lock.readLock().lock();
        try {
            for (int i = QTY_LEVELS - 1; i >= 0; i--) {
                if (!waitingPatients[i].isEmpty()) {
                    return waitingPatients[i].iterator().next();
                }
            }
            return null;
        } finally {
            lock.readLock().unlock();
        }
    }

    public Patient changeStatus(String name, int level, State state) {
        lock.writeLock().lock();
        try {
            if (state != State.STATE_WAITING) {
                waitingPatients[level - 1].remove(patients.get(name));
            }
            Patient patient = Patient.newBuilder()
                    .setState(state)
                    .setName(name)
                    .setLevel(level)
                    .build();
            patients.remove(name);
            patients.put(name, patient);
            return patient;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public Patient getPatient(String name) {
        lock.readLock().lock();
        try {
            return patients.getOrDefault(name, null);
        } finally {
            lock.readLock().unlock();
        }
    }

    public List<Patient> getPatientsInWaitingRoom() {
        lock.readLock().lock();
        try {
            List<Patient> list = new ArrayList<>();
            for (int i = QTY_LEVELS - 1; i >= 0; i--) {
                list.addAll(waitingPatients[i]);
            }
            return list;
        } finally {
            lock.readLock().unlock();
        }
    }
}
