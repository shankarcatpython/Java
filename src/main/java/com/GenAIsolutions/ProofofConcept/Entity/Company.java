package com.GenAIsolutions.ProofofConcept.Entity;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Company {
    @Id
    private int id;
    private String name;
    private String industry;

    // Getters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getIndustry() {
        return industry;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }
}
