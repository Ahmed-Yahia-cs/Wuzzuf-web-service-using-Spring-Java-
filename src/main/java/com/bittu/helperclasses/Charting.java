package com.bittu.helperclasses;

import com.bittu.model.ValueCount;
import org.knowm.xchart.*;

import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.List;

public class Charting {


    public static void myPieChart(List<ValueCount> list , String imageName) throws Exception {

        PieChart chart = new PieChartBuilder().width(800).height(600).title("pie chart").build();
        for (ValueCount com : list) {
            chart.addSeries(com.getName() + " ", com.getCount());

        }
        BitmapEncoder.saveBitmap(chart, "src/main/resources/"+imageName, BitmapEncoder.BitmapFormat.PNG);

    }

    public  static  void myCategoryChart(List<ValueCount> list , String imageName , String seriesName) throws Exception{

        List <String> jobList = new ArrayList<String>();
        List<Long> countList = new ArrayList<Long>();

        for (ValueCount i :list)
        {
            jobList.add(i.getName());
            countList.add(i.getCount());
        }

        // Create Chart
        CategoryChart chart = new CategoryChartBuilder().width(800).height(600).build();


        chart.getStyler().setXAxisLabelRotation(90);

        chart.addSeries(seriesName, jobList, countList);

        BitmapEncoder.saveBitmap(chart, "src/main/resources/"+imageName, BitmapEncoder.BitmapFormat.PNG);

    }
}



