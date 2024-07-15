package com.GenAIsolutions.ProofofConcept.service;

import com.GenAIsolutions.ProofofConcept.Entity.Vehicle;
import com.GenAIsolutions.ProofofConcept.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VehicleService {

    private final VehicleRepository vehicleRepository;

    @Autowired
    public VehicleService(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    public List<Vehicle> findAll() {
        return vehicleRepository.findAll();
    }

    public void save(Vehicle vehicle) {
        vehicleRepository.save(vehicle);
    }

    // Additional methods if needed
}
