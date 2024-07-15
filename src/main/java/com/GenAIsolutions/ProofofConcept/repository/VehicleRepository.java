package com.GenAIsolutions.ProofofConcept.repository;

import org.springframework.data.jpa.repository.JpaRepository;


import com.GenAIsolutions.ProofofConcept.Entity.Vehicle;

public interface VehicleRepository extends JpaRepository<Vehicle, Integer> {
}