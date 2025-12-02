package com.example.ordermanager


import org.jfree.chart.ChartFactory
import org.jfree.chart.ChartUtils
import org.jfree.chart.JFreeChart
import org.jfree.chart.labels.PieSectionLabelGenerator
import org.jfree.chart.labels.StandardPieSectionLabelGenerator
import org.jfree.chart.plot.PiePlot
import org.jfree.data.general.DefaultPieDataset
import java.io.File

class Calculator {
    /**
     * TESTING JFREECHART
     */


    fun generateChartFromOrders(orders: List<Order>) :File {

        val dataset = DefaultPieDataset<String>()


        //Count orders by type

        val typeCounter = orders.groupingBy { it.orderType }.eachCount()

        typeCounter.forEach { (type, count) -> dataset.setValue(type, count.toDouble())}

        val outputPieChart = ChartFactory.createPieChart("Orders By Type", dataset,true,false,false)

        val labelGenerator: PieSectionLabelGenerator = StandardPieSectionLabelGenerator("{0} = {2}");
        val plot: PiePlot<String> = outputPieChart.plot as PiePlot<String>;
        plot.labelGenerator = labelGenerator;

        val width = 640; // Width of the image
        val height = 480; // Height of the image
        val chartFile = File("outputPieChart.png");
        ChartUtils.saveChartAsPNG(chartFile, outputPieChart, width, height);

        return  chartFile

    }




    fun chartTest(): File {
        // Create Dataset object
        val dataset: DefaultPieDataset<String> = DefaultPieDataset();
        dataset.setValue("Burger", 100.00);
        dataset.setValue("Fries", 50.0);
        dataset.setValue("Milkshake", 40.0);

        // Create Chart object from Dataset
        var chart: JFreeChart = ChartFactory.createPieChart(
            "Profit for Each Item", // Title of chart
            dataset,    // Dataset for chart
            true,   // Include legend
            false,   // Include tool tips
            false   // Generate URLs
        );

        // Change label generator to display percentages
        val labelGenerator: PieSectionLabelGenerator = StandardPieSectionLabelGenerator("{0} = {2}");
        val plot: PiePlot<String> = chart.plot as PiePlot<String>;
        plot.labelGenerator = labelGenerator;

        // Save the chart as a PNG
        val width = 640; // Width of the image
        val height = 480; // Height of the image
        val chartFile = File("chart.png");
        ChartUtils.saveChartAsPNG(chartFile, chart, width, height);

        return chartFile;
    }
}