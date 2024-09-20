package ar.edu.itba.pod.server.repositories;

import ar.edu.itba.pod.grpc.common.Doctor;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DoctorRepository {
    private Map<String, Doctor>[] doctors;
    private final int CANT_LEVELS_DOCTORS = 5;

    public DoctorRepository() {
        doctors = new Map[CANT_LEVELS_DOCTORS];
        for (int i = 0; i < doctors.length; i++) {
            doctors[i] = new HashMap<>();
        }
    }

    public Doctor addDoctor(Doctor doctor){

        int level = doctor.getLevel();
        String name = doctor.getName();

        if(level<1 || level>5){
            // error nivel invalido
        }
        for(int i = 0; i< CANT_LEVELS_DOCTORS; i++){
            if(doctors[i].containsKey(name)){
                // error xq ya existe el dr
            }
        }
        doctors[level-1].put(name,doctor);

        return doctor;
    }

}
