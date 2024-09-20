package ar.edu.itba.pod.server.repositories;

import ar.edu.itba.pod.grpc.common.Availability;
import ar.edu.itba.pod.grpc.common.Doctor;
import ar.edu.itba.pod.grpc.common.RequestDoctor;
import ar.edu.itba.pod.grpc.common.RequestDoctorLevel;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class DoctorRepository {
    private Map<String, Doctor>[] doctors;
    private final int QTY_LEVELS_DOCTORS = 5;

    public DoctorRepository() {
        doctors = new Map[QTY_LEVELS_DOCTORS];
        for (int i = 0; i < doctors.length; i++) {
            doctors[i] = new TreeMap<>();
        }
    }

    public Doctor addDoctor(RequestDoctorLevel doctor){

        int level = doctor.getLevel();
        String name = doctor.getName();

        if(level<1 || level>5){
            // error nivel invalido
        }
        for(int i = 0; i< QTY_LEVELS_DOCTORS; i++){
            if(doctors[i].containsKey(name)){
                // error xq ya existe el dr
            }
        }
        Doctor doc = Doctor.newBuilder()
                .setName(name)
                .setLevel(level)
                .setAvailability(Availability.AVAILABILITY_AVAILABLE)
                .setIsRegistered(false)
                .build();
        doctors[level-1].put(name,doc);

        return doc;
    }

    public Doctor changeAvailability(RequestDoctor doctor){

        String name = doctor.getName();
        for(int i = 0; i< QTY_LEVELS_DOCTORS; i++){
            if(doctors[i].containsKey(name)){
                Doctor old = doctors[i].get(name);
                if(old.getAvailability() == Availability.AVAILABILITY_ATTENDING){
                    // error
                }else{
                    Doctor doc = Doctor.newBuilder()
                            .setName(name)
                            .setLevel(old.getLevel())
                            .setAvailability(doctor.getAvailability())
                            .setIsRegistered(old.getIsRegistered())
                            .build();
                    doctors[i].remove(name);
                    doctors[i].put(name,doc);
                    return doc;
                }
            }
        }
        return null;
        // no encontre nada falla
    }

    public Doctor changeAvailability(RequestDoctor doctor,int level){

        String name = doctor.getName();
            if(doctors[level-1].containsKey(name)){
                Doctor old = doctors[level-1].get(name);
                if(old.getAvailability() == Availability.AVAILABILITY_ATTENDING){
                    // error
                }else{
                    Doctor doc = Doctor.newBuilder()
                            .setName(name)
                            .setLevel(old.getLevel())
                            .setAvailability(doctor.getAvailability())
                            .setIsRegistered(old.getIsRegistered())
                            .build();
                    doctors[level-1].remove(name);
                    doctors[level-1].put(name,doc);
                    return doc;
                }
            }

        return null;
        // no encontre nada falla
    }

    public Doctor getAvailability(String name){

        for(int i = 0; i< QTY_LEVELS_DOCTORS; i++){
            if(doctors[i].containsKey(name)){
                return doctors[i].get(name);
            }
        }
        return null;
        // no encontre nada falla
    }

    public Doctor getHighLevelFreeDoctor (){
        for(int i=QTY_LEVELS_DOCTORS-1;i>=0;i--){
            for (Map.Entry<String, Doctor> entry : doctors[i].entrySet()) {
                Doctor doc = entry.getValue();
                if (doc.getAvailability() == Availability.AVAILABILITY_AVAILABLE) {
                    return doc;
                }
            }
        }
        return null;
    }

}
