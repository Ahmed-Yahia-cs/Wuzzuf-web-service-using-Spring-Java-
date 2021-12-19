package com.bittu.helperclasses;

import com.bittu.model.UpdatedJob;
import org.apache.spark.sql.*;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RefactorDateSet {



    public static void dataWithAppendedColumn(Dataset<Row> data)
    {
        // add the additional column and transform it to a dataset of jobs
        data = data.withColumn("minYearsExp", functions.lit(-1));
        //convert the data to a list of jobs
        List<UpdatedJob> js =dfToJobs(data);

        // change the data
        for( UpdatedJob j:js){
            j.setMinYearsExp(extractInt(j.getYearsExp()));
        }
        Dataset<Row> updatedData =JobsToDf(js);
        updatedData.show(10);
    }



    private static Dataset<Row> addMinExCol(Dataset<Row> data)
    {
        //convert the data to a list of jobs
        List<UpdatedJob> js = dfToJobs(data);

        // change the data
        for( UpdatedJob j:js){
            j.setMinYearsExp(extractInt(j.getYearsExp()));
        }


        return JobsToDf (js);

    }

    private  static int extractInt(String str)
    {
        // Replacing every non-digit number , and spaces with one space(" ")
        str = str.replaceAll("[^\\d]", " ").replaceAll(" +", " ").trim();

        if (str.equals(""))
            return -1;

        String[] temp;
        temp = str.split(" ");
        return Integer.parseInt(temp[0]);
    }
    private static List<UpdatedJob> dfToJobs (Dataset<Row> data)
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

        List<UpdatedJob> js = SparkSession.builder().appName ("Wazaf dataset model").getOrCreate()
                .createDataFrame(data.toJavaRDD(), schema).as(Encoders.bean(UpdatedJob.class)).collectAsList();
        return js;
    }
    private static Dataset<Row> JobsToDf ( List<UpdatedJob> js)
    {
        Dataset df = SparkSession.builder().appName ("Wazaf dataset model").getOrCreate().createDataFrame(js,UpdatedJob.class);
        String[] columns = df.columns();
        String[] newColumns = Arrays.stream(columns)
                .map(column -> "_" +(column))
                .toArray(String[]::new);
        df.toDF(newColumns);
        return df;
    }


}
