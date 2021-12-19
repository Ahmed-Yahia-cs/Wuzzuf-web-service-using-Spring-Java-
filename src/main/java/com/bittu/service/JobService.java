package com.bittu.service;

import com.bittu.model.Job;
import com.bittu.model.ValueCount;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;


import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface JobService {
    public List<Job> getJobs() throws Exception;
    public List<ValueCount> getCompanyCount() throws Exception;
    public List<ValueCount> getDemandesJobs() throws Exception;
    public List<ValueCount> getlocationsCount() throws Exception;
    public Map<String, Integer> getRequiredSkills() throws Exception;


    public ResponseEntity<String> cleanAndSummary() throws Exception;
    public ResponseEntity<ByteArrayResource> companiesWithCountChart()throws IOException;
    public ResponseEntity<ByteArrayResource> demandesJobsChart()throws IOException;
    public ResponseEntity<ByteArrayResource> locationWithCountChart()throws IOException;

    public String apllyKmeans( int n) throws Exception;

    public String addColumnToDset()throws Exception;

    public Map<String , String> getImage();

}
