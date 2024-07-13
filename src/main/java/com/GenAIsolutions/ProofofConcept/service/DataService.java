package com.GenAIsolutions.ProofofConcept.service;

import com.GenAIsolutions.ProofofConcept.Entity.Company;
import com.GenAIsolutions.ProofofConcept.Entity.Store;
import com.GenAIsolutions.ProofofConcept.repository.CompanyRepository;
import com.GenAIsolutions.ProofofConcept.repository.StoreRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DataService {

    private final CompanyRepository companyRepository;
    private final StoreRepository storeRepository;
    private final ObjectMapper objectMapper;

    @Autowired
    public DataService(CompanyRepository companyRepository, StoreRepository storeRepository, ObjectMapper objectMapper) {
        this.companyRepository = companyRepository;
        this.storeRepository = storeRepository;
        this.objectMapper = objectMapper;
    }

    public void exportDataToJson() throws IOException {
        List<Company> companies = companyRepository.findAll();
        List<Store> stores = storeRepository.findAll();

        Map<String, List<?>> data = new HashMap<>();
        data.put("companies", companies);
        data.put("stores", stores);

        File outputFile = new File("data.json");
        objectMapper.writeValue(outputFile, data);
        
        // Log the location of the output file
        System.out.println("Data exported to: " + outputFile.getAbsolutePath());
    }
}
