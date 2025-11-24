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
    fun chartTest() {
        // Create Dataset object
        var dataset: DefaultPieDataset<String> = DefaultPieDataset();
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
    }
}