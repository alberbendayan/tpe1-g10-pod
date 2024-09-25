package ar.edu.itba.pod.server.repositories;

import ar.edu.itba.pod.grpc.common.Availability;
import ar.edu.itba.pod.grpc.common.Doctor;
import ar.edu.itba.pod.grpc.common.RequestDoctor;
import ar.edu.itba.pod.grpc.common.RequestDoctorLevel;

import javax.print.Doc;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentSkipListMap;

public class DoctorRepository {
    private ConcurrentSkipListMap<String, Doctor>[] doctors;
    private final int QTY_LEVELS_DOCTORS = 5;

    public DoctorRepository() {
        doctors = new ConcurrentSkipListMap[QTY_LEVELS_DOCTORS];
        for (int i = 0; i < doctors.length; i++) {
            doctors[i] = new ConcurrentSkipListMap<>();
        }
    }

    public Doctor addDoctor(RequestDoctorLevel doctor) {

        int level = doctor.getLevel();
        String name = doctor.getName();

        if (level < 1 || level > 5) {
            return null;
        }
        for (int i = 0; i < QTY_LEVELS_DOCTORS; i++) {
            if (doctors[i].containsKey(name)) {
                return Doctor.newBuilder()
                        .setName(doctor.getName())
                        .setLevel(-1)
                        .build();
            }
        }
        Doctor doc = Doctor.newBuilder()
                .setName(name)
                .setLevel(level)
                .setAvailability(Availability.AVAILABILITY_UNAVAILABLE)
                .setIsRegistered(false)
                .build();
        doctors[level - 1].put(name, doc);

        return doc;
    }

    public Doctor changeAvailability(RequestDoctor doctor) {
        String name = doctor.getName();
        for (int i = 0; i < QTY_LEVELS_DOCTORS; i++) {
            if (doctors[i].containsKey(name)) {
                Doctor old = doctors[i].get(name);
                if (old.getAvailability() == Availability.AVAILABILITY_ATTENDING) {
                    return Doctor.newBuilder()
                            .setName(doctor.getName())
                            .setLevel(-1)
                            .build();
                } else {
                    Doctor doc = Doctor.newBuilder()
                            .setName(name)
                            .setLevel(old.getLevel())
                            .setAvailability(doctor.getAvailability())
                            .setIsRegistered(old.getIsRegistered())
                            .build();
                    doctors[i].remove(name);
                    doctors[i].put(name, doc);
                    return doc;
                }
            }
        }
        return Doctor.newBuilder()
                .setLevel(-2)
                .build();
    }

    public Doctor changeAvailability(RequestDoctor doctor, int level) {

        String name = doctor.getName();
        if (doctors[level - 1].containsKey(name)) {
            Doctor old = doctors[level - 1].get(name);
            if (old.getAvailability() == Availability.AVAILABILITY_ATTENDING) {
                return null;
            } else {
                Doctor doc = Doctor.newBuilder()
                        .setName(name)
                        .setLevel(old.getLevel())
                        .setAvailability(doctor.getAvailability())
                        .setIsRegistered(old.getIsRegistered())
                        .build();
                doctors[level - 1].remove(name);
                doctors[level - 1].put(name, doc);
                return doc;
            }
        }
        return Doctor.newBuilder()
                .setLevel(-2)
                .build();
    }

    public Doctor freeDoctor(RequestDoctor doctor, int level) {
        String name = doctor.getName();
        if (doctors[level - 1].containsKey(name)) {
            Doctor old = doctors[level - 1].get(name);
            if (old.getAvailability() != Availability.AVAILABILITY_ATTENDING) {
                return Doctor.newBuilder()
                        .setLevel(-2)
                        .build();
            }
            Doctor doc = Doctor.newBuilder()
                    .setName(name)
                    .setLevel(old.getLevel())
                    .setAvailability(doctor.getAvailability())
                    .setIsRegistered(old.getIsRegistered())
                    .build();
            doctors[level - 1].remove(name);
            doctors[level - 1].put(name, doc);
            return doc;
        }
        return Doctor.newBuilder()
                .setLevel(-2)
                .build();
    }

    public Doctor getAvailability(String name) {
        for (int i = 0; i < QTY_LEVELS_DOCTORS; i++) {
            if (doctors[i].containsKey(name)) {
                return doctors[i].get(name);
            }
        }
        return Doctor.newBuilder().setLevel(-2).build();

    }

    public Doctor getDoctorToPatient(int level) {
        for (int i = level - 1; i < QTY_LEVELS_DOCTORS; i++) {
            for (Map.Entry<String, Doctor> entry : doctors[i].entrySet()) {
                Doctor doc = entry.getValue();
                if (doc.getAvailability() == Availability.AVAILABILITY_AVAILABLE) {
                    return doc;
                }
            }
        }
        return null;
    }

    public Doctor getDoctorByName(String name) {
        for (int i = 0; i < QTY_LEVELS_DOCTORS; i++) {
            if (doctors[i].containsKey(name)) {
                return doctors[i].get(name);
            }
        }
        return null;
    }

}
