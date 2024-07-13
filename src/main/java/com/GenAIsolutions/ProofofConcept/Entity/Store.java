package com.GenAIsolutions.ProofofConcept.Entity;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Store {
    @Id
    private int id;
    private int companyId;
    private String location;

    // Getters
    public int getId() {
        return id;
    }

    public int getCompanyId() {
        return companyId;
    }

    public String getLocation() {
        return location;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
