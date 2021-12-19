package com.bittu.api;

import com.bittu.model.Job;
import com.bittu.model.ValueCount;
import com.bittu.service.JobService;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
public class JobApi {
    @Autowired
    private JobService jobService;

    @GetMapping("/jobs")
    public List<Job> allGobs()  {
       try {
           return this.jobService.getJobs() ;
       }catch (Exception e ){
           e.printStackTrace();
           return  null;
       }

    }


    @GetMapping("/all-companies")

    public List<ValueCount> countryWithCount() {

        try {
            return  this.jobService.getCompanyCount();
        }catch (Exception e ){
            e.printStackTrace();
            return  null;

        }

    }
    @GetMapping("/most-locations")
    public List<ValueCount> locationsWithCount(){

        try {
            return  this.jobService.getlocationsCount();
        }catch (Exception e ){
            e.printStackTrace();
            return null;
        }
    }

    @GetMapping("/demands-job")
    public List<ValueCount>  demandesJobs() throws Exception {

        try {
            return  this.jobService.getDemandesJobs();
        }catch (Exception e ){
            e.printStackTrace();
            return null;
        }
    }

    @GetMapping("/req-skills")
    public Map<String, Integer> requriedSkills(){

        try {
            return  this.jobService.getRequiredSkills();
        }catch (Exception e ){
            e.printStackTrace();
            return null;
        }
    }

    @GetMapping("/summary")
    public ResponseEntity<String> cleanAndSummary(){

        try {
            return  this.jobService.cleanAndSummary();
        }catch (Exception e ){
            e.printStackTrace();
            return null;
        }
    }

    /*All Charts Images Api */
    @GetMapping(value = "/companies-count-piechart", produces = MediaType.IMAGE_PNG_VALUE)
    public  ResponseEntity<ByteArrayResource> getCompanyCountChart(){

        try {
            return  this.jobService.companiesWithCountChart();
        }catch (Exception e ){
            e.printStackTrace();
            return null;
        }
    }

    @GetMapping(value = "/job-demands-catChart", produces = MediaType.IMAGE_PNG_VALUE)
    public  ResponseEntity<ByteArrayResource> getJobDemandsChart(){

        try {
            return  this.jobService.demandesJobsChart();

        }catch (Exception e ){
            e.printStackTrace();
            return null;
        }
    }
    @GetMapping(value = "/locations-count-catchart", produces = MediaType.IMAGE_PNG_VALUE)
    public  ResponseEntity<ByteArrayResource> getLocationCountChart(){

        try {
            return  this.jobService.locationWithCountChart();
        }catch (Exception e ){
            e.printStackTrace();
            return null;
        }
    }

    @GetMapping(value = "/aplly-kmean")
    public String K_Mean(){
        try {
            return  this.jobService.apllyKmeans(10);
        }catch (Exception e){
            e.printStackTrace();
            return  "K-Mean Failuer";
        }
    }

    @GetMapping(value = "add-col")
    public String  addColTodDset(){
        try {
            return  this.jobService.addColumnToDset();
        }catch (Exception e){
            e.printStackTrace();
            return  "Adding Column Faileur";
        }
    }

    @GetMapping(value = "img")
    public Map<String,String>  img(){
        return this.jobService.getImage();
    }


}
