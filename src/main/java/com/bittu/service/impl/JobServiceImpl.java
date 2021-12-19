package com.bittu.service.impl;

import com.bittu.helperclasses.Charting;
import com.bittu.helperclasses.RefactorDateSet;
import com.bittu.model.Job;
import com.bittu.model.ValueCount;
import com.bittu.service.JobService;
import org.apache.spark.ml.Pipeline;
import org.apache.spark.ml.PipelineModel;
import org.apache.spark.ml.PipelineStage;
import org.apache.spark.ml.clustering.KMeans;
import org.apache.spark.ml.feature.OneHotEncoder;
import org.apache.spark.ml.feature.StringIndexer;
import org.apache.spark.ml.feature.VectorAssembler;
import org.apache.spark.sql.*;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static org.apache.spark.sql.functions.col;
import static org.apache.spark.sql.functions.desc;

@Service
public class JobServiceImpl implements JobService {

    private Dataset<Row> csvDataFrame;

    private List<ValueCount> companiesCountList;
    private List<ValueCount> jobDemandesList;
    private List<ValueCount> locationsCount;

    private long fullDataCount;
    private long nullDoubleDataCount;
    private long cleanDataCount;


    @Autowired
    private SparkSession sparkSession;

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public List<Job> getJobs() throws Exception {

        if(this.csvDataFrame == null)
            this.initalizeDataSet();

        Dataset<Job> jobsDataset = this.csvDataFrame.as(Encoders.bean(Job.class));

        List<Job> allJobs = jobsDataset.limit(150).collectAsList();


        return allJobs;
    }

    @Override
    /*Get Most demanding companies */
    public  List<ValueCount>  getCompanyCount(){

        if(this.csvDataFrame == null)
            this.initalizeDataSet();

        Dataset<Job> jobsDataset = this.csvDataFrame.as(Encoders.bean(Job.class));
        Dataset<Row> companyCount = this.csvDataFrame.groupBy(jobsDataset.col("Company").as("name")).count().sort(desc("count"));
//        companyCount.show();

        companiesCountList = companyCount.limit(150).as(Encoders.bean(ValueCount.class)).collectAsList();

        drawPiechart(companiesCountList , "CompaniesPiechart");

        return companiesCountList;

    }

    // Get Demandes Job
    @Override
    public List<ValueCount> getDemandesJobs() throws Exception {
        if(csvDataFrame == null)
            this.initalizeDataSet();


        Dataset<Row> jobCount = this.csvDataFrame.groupBy(this.csvDataFrame.col("Title").as("name")).count().sort(desc("count")).limit(10);
        jobDemandesList= jobCount.as(Encoders.bean(ValueCount.class)).collectAsList();

        drawCategoryChart(jobDemandesList, "demandsJobparchart" , "job demands");

        return jobDemandesList;
    }

    @Override
    public List<ValueCount> getlocationsCount() throws Exception {
        if(this.csvDataFrame == null)
            this.initalizeDataSet();

        Dataset<Row> locationsWithCount = this.csvDataFrame.groupBy(this.csvDataFrame.col("Location").as("name")).count().sort(desc("count")).limit(10);

        locationsCount = locationsWithCount.as(Encoders.bean(ValueCount.class)).collectAsList();


        drawCategoryChart(locationsCount , "locationsParChart", "locations of jobs");


        return locationsCount;
    }

    @Override
    public Map<String, Integer> getRequiredSkills() throws Exception {
        if(this.csvDataFrame == null)
            this.initalizeDataSet();

        Dataset<Job> jobsDataset = this.csvDataFrame.as(Encoders.bean(Job.class));
        Dataset<Row> skillsData = jobsDataset.select(col("Skills"));
        List<String> skillsDataStr = skillsData.as(Encoders.STRING()).collectAsList();

        Map<String, Integer> skillCount = new HashMap<String, Integer>();
        for (String sk : skillsDataStr)
        {
            for(String s: sk.split(","))
            {
                String key = s.trim().toLowerCase();
                skillCount.put(key, skillCount.getOrDefault(key,1) + 1);
            }
        }
        // create sorted map
        LinkedHashMap<String, Integer> sortedMap = new LinkedHashMap<>();

        skillCount.entrySet().stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .forEachOrdered(x -> sortedMap.put(x.getKey() , x.getValue()) );

        return sortedMap;
    }

    @Override
     public ResponseEntity<String> cleanAndSummary(){

        if(this.csvDataFrame == null)
            initalizeDataSet();


        String html = String.format("<h1>%s</h1>", "Running Apache Spark on/with support of Spring boot") +
                String.format("<h2>%s</h2>", "Spark version = "+ sparkSession.sparkContext().version()) +
                String.format("<h3>%s</h3>", "Full Data Count = " + this.fullDataCount)+
                String.format("<h3>%s</h3>", "Clean Data Count = "+this.cleanDataCount)+
                String.format("<h5>Schema <br/> %s</h5> <br/> Sample data - <br/>", this.csvDataFrame.schema().treeString()) +
                this.csvDataFrame.showString(2,1);

        //    Call K-means Algo
//        this.titleCompKmeans(this.csvDataFrame , 10);

        return ResponseEntity.ok(html);


    }

    @Override
    public  ResponseEntity<ByteArrayResource> companiesWithCountChart() throws IOException {

        if(companiesCountList == null)
            return null;

        final ByteArrayResource img = new ByteArrayResource(Files.readAllBytes(Paths
                .get("src/main/resources/CompaniesPiechart.png")));

        return  ResponseEntity
                .status(HttpStatus.OK)
                .contentLength(img.contentLength())
                .body(img);

    }

    @Override
    public ResponseEntity<ByteArrayResource> demandesJobsChart() throws IOException {
        if (jobDemandesList == null)
            return null;

        final ByteArrayResource img = new ByteArrayResource(Files.readAllBytes(Paths
                .get("src/main/resources/demandsJobParChart.png")));

        
        return  ResponseEntity
                .status(HttpStatus.OK)
                .contentLength(img.contentLength())
                .body(img);

    }

    @Override
    public ResponseEntity<ByteArrayResource> locationWithCountChart() throws IOException {
        if (locationsCount == null)
            return null;

        final ByteArrayResource img = new ByteArrayResource(Files.readAllBytes(Paths
                .get("src/main/resources/locationsParChart.png")));

        return  ResponseEntity
                .status(HttpStatus.OK)
                .contentLength(img.contentLength())
                .body(img);
    }

//---------------------- End Of aLl Andpoint Functions //------------------------------------------

    /*___________________________  Helper Method __________________________*/

    private void initalizeDataSet() {

        sparkSession = SparkSession.builder().appName("Wazaf dataset model").master("local[2]").getOrCreate();
        sparkSession.sparkContext().setLogLevel("ERROR");

        // reading the data
        final DataFrameReader dataFrameReader = sparkSession.read();


        csvDataFrame = dataFrameReader.option("header", "true").csv("Wuzzuf_Jobs.csv");
        csvDataFrame = csvDataFrame.select("Title", "Company", "Location", "Type", "Level", "YearsExp", "Country", "Skills");

        //remove duplicate and null values
        this.csvDataFrame = this.cleanData(this.csvDataFrame);
    }

    /* All This Function Help to Add the new Column in DataSet<Row> */
    public static List<Job> dfToJobs (Dataset<Row> data)
    {
        List<StructField> fields = new ArrayList<>();
        fields.add(DataTypes.createStructField("Title", DataTypes.StringType, true));
        fields.add(DataTypes.createStructField("Company", DataTypes.StringType, true));
        fields.add(DataTypes.createStructField("Location", DataTypes.StringType, true));
        fields.add(DataTypes.createStructField("Type", DataTypes.StringType, true));
        fields.add(DataTypes.createStructField("Level", DataTypes.StringType, true));
        fields.add(DataTypes.createStructField("YearsExp", DataTypes.StringType, true));
        fields.add(DataTypes.createStructField("Country", DataTypes.StringType, true));
        fields.add(DataTypes.createStructField("Skills", DataTypes.StringType, true));
        fields.add(DataTypes.createStructField("minYearsExp", DataTypes.IntegerType, true));
        StructType schema = DataTypes.createStructType(fields);

        List<Job> js = SparkSession.builder().appName ("Wazaf dataset model").getOrCreate()
                .createDataFrame(data.toJavaRDD(), schema).as(Encoders.bean(Job.class)).collectAsList();
        return js;
    }

    public Dataset<Row> cleanData(Dataset<Row> data)
    {
        this.fullDataCount = data.count ();
        data = data.na().drop().distinct();

        nullDoubleDataCount = fullDataCount - data.count ();
        cleanDataCount = fullDataCount - nullDoubleDataCount;

        return data;
    }

    // Take 0-3YearsOfExp  -> return 0
    public static int extractInt(String str)
    {
        // Replacing every non-digit number , and spaces with one space(" ")
        str = str.replaceAll("[^\\d]", " ").replaceAll(" +", " ").trim();

        if (str.equals(""))
            return -1;

        String[] temp;
        temp = str.split(" ");
        return Integer.parseInt(temp[0]);
    }

    // Take list of job  , retrun  DateSet of Job
    public static Dataset<Row> JobsToDf ( List<Job> js)
    {
        Dataset df = SparkSession.builder().appName ("Wazaf dataset model").getOrCreate().createDataFrame(js,Job.class);
        String[] columns = df.columns();
        String[] newColumns = Arrays.stream(columns)
                .map(column -> "_" +(column))
                .toArray(String[]::new);
        df.toDF(newColumns);
        return df;
    }

    private void drawPiechart(List<ValueCount> l , String imgName)
    {
        try {
            Charting.myPieChart(l , imgName);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void drawCategoryChart(List<ValueCount> l , String imgName , String seriesName){

        try {
            Charting.myCategoryChart(l,imgName,seriesName);
        }catch (Exception e ){
            e.printStackTrace();
        }
    }


    /* Applly K-means Algorithm to dataSet */
    @Override
    public String apllyKmeans( int n)
    {

        try {


            Dataset<Job> data = this.csvDataFrame.as(Encoders.bean(Job.class));

            StringIndexer titleIndx = new StringIndexer().setInputCol("Title").setOutputCol("TitleIndex");
            OneHotEncoder titleVec = new OneHotEncoder().setInputCol("TitleIndex").setOutputCol("TitleVector");
            StringIndexer compIndx = new StringIndexer().setInputCol("Company").setOutputCol("CompanyIndex");
            OneHotEncoder ComVec = new OneHotEncoder().setInputCol("CompanyIndex").setOutputCol("CompanyVector");
            VectorAssembler vectorAssembler = new VectorAssembler()
                    .setInputCols(new String[] { "TitleVector", "CompanyVector"})
                    .setOutputCol("features");

            Pipeline prePipeline = new Pipeline()
                    .setStages(new PipelineStage[] { titleIndx, titleVec,compIndx, ComVec,vectorAssembler});
            PipelineModel preModel = prePipeline.fit(data);
            Dataset<Row> newdata = preModel.transform(data);

            Dataset<Row>[] splits = newdata.randomSplit(new double[] { 0.9, 0.1 },42);
            Dataset<Row> trainingFeaturesData = splits[0];
            Dataset<Row> testFeaturesData = splits[1];

            KMeans kmeans = new KMeans().setK(n).setFeaturesCol("features").setPredictionCol("prediction");

            Pipeline pipeline = new Pipeline()
                    .setStages(new PipelineStage[] {kmeans});
            PipelineModel model = pipeline.fit(trainingFeaturesData);

            Dataset<Row> predictions = model.transform(testFeaturesData);

            predictions.show();

            return "K-means Apllying Successfully";


        }catch (Exception e){
            e.printStackTrace();
            return  "UnError Occure , K-means Failed ";
        }

    }

    @Override
    public String addColumnToDset() throws Exception {

        try {
            RefactorDateSet.dataWithAppendedColumn(this.csvDataFrame);
            return "Adding Column Successfully";
        }catch (Exception e ){
            e.printStackTrace();
            return  " UnError Occure , Adding Column Faileur";
        }

    }

    @Override
    public Map<String,String> getImage() {
        String img = "src/main/resources/locationsParChart.png";  
        Map<String , String> map = new HashMap<>();
        map.put("path" , img);

          return map;
    }
}
