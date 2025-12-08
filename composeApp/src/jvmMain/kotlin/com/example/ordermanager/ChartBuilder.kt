package com.example.ordermanager

import org.jfree.chart.ChartFactory
import org.jfree.chart.ChartUtils
import org.jfree.chart.JFreeChart
import org.jfree.chart.labels.PieSectionLabelGenerator
import org.jfree.chart.labels.StandardPieSectionLabelGenerator
import org.jfree.chart.plot.PiePlot
import org.jfree.data.category.DefaultCategoryDataset
import org.jfree.data.general.Dataset
import org.jfree.data.general.DefaultPieDataset
import java.io.File

/**
 * Defines a Template pattern for creating charts
 */
abstract class ChartBuilderTemplate() {
    abstract var fileName : String;
    val IMAGE_WIDTH : Int = 640; // Magic number
    val IMAGE_HEIGHT : Int = 480; // Magic number

    /**
     * The sequence of methods that are needed to make a chart
     * @param list List of orders to make a chart out of
     */
    final fun buildChart(list: List<Order>) {
        var chartMap = makeMap(list);
        var dataset = makeDataset(chartMap);
        var chart = makeChart(dataset);
        chartToFile(chart);
    }

    final fun chartToFile(chart: JFreeChart) {
        val f = File("$fileName.png");
        ChartUtils.saveChartAsPNG(f, chart, IMAGE_WIDTH, IMAGE_HEIGHT);
    }

    final fun getFile(): File {
        return File("$fileName.png");
    }

    protected abstract fun makeMap(list: List<Order>) : Map<String, Number>;
    protected abstract fun makeDataset(chartMap: Map<String, Number>) : Dataset;
    protected abstract fun makeChart (dataset: Dataset) : JFreeChart;
}

class ProfitChartBuilder : ChartBuilderTemplate() {
    override var fileName = "pie-chart";

    override fun makeMap(list: List<Order>): Map<String, Number> {
        // Create profit map
        var profitMap: HashMap<String, Double> = hashMapOf();
        for (key in ItemMap.map.keys) {
            profitMap[key] = 0.0;
        }

        // Fill map
        for (order: Order in list) { // For each order
            for (item: Item in order.items) { // For each item
                if (profitMap.containsKey(item.name)) { // If item is on the menu
                    val report: SalesReport = SalesReport(item); // Get sales report for item
                    // Add profit from report to total profit for that item
                    profitMap[item.name] = profitMap.getValue(item.name) + report.totalProfit;
                }
            }
        }

        return profitMap;
    }

    override fun makeDataset(chartMap: Map<String, Number>) : Dataset {
        // Create dataset for chart
        val dataset: DefaultPieDataset<String> = DefaultPieDataset();
        // Fill dataset with each item and it's total profit
        for (entry in chartMap.entries) {
            dataset.setValue(entry.key, entry.value);
        }
        return dataset;
    }

    override fun makeChart (dataset: Dataset) : JFreeChart {
        // Create chart object from dataset
        val chart: JFreeChart = ChartFactory.createPieChart(
            "Profit of Menu Items", // Title of chart
            dataset as DefaultPieDataset<String>,    // Dataset for chart
            true,   // Include legend
            true,   // Include tool tips
            false   // Generate URLs
        );

        // Change labels on chart to display percentages
        val labelGenerator: PieSectionLabelGenerator = StandardPieSectionLabelGenerator("{0} = {2}");
        val plot: PiePlot<String> = chart.plot as PiePlot<String>;
        plot.labelGenerator = labelGenerator;

        return chart;
    }
}

class QuantityChartBuilder : ChartBuilderTemplate() {
    override var fileName = "bar-chart";

    override fun makeMap(list: List<Order>): Map<String, Number> {
        // Create quantity map
        var quantityMap: HashMap<String, Int> = hashMapOf();
        for (key in ItemMap.map.keys) {
            quantityMap[key] = 0;
        }

        for (order: Order in list) { // For each order
            for (item: Item in order.items) { // For each item
                if (quantityMap.containsKey(item.name)) { // If item is on the menu
                    // Add quantity from item to total quantity
                    quantityMap[item.name] = quantityMap.getValue(item.name) + item.quantity;
                }
            }
        }
        return quantityMap;
    }

    override fun makeDataset(chartMap: Map<String, Number>) : Dataset {
        // Create dataset for chart
        var dataset: DefaultCategoryDataset = DefaultCategoryDataset();
        // Fill dataset with each item and it's total profit
        for (entry in chartMap.entries) {
            dataset.addValue(entry.value, "Quantity", entry.key);
        }
        return dataset;
    }

    override fun makeChart (dataset: Dataset) : JFreeChart {
        // Create chart object from dataset
        var chart: JFreeChart = ChartFactory.createBarChart(
            "Profit of Menu Items", // Title of chart
            "Item", // X axis title
            "Quantity Ordered", // Y axis title
            dataset as DefaultCategoryDataset,    // Dataset for chart
        );
        return chart;
    }
}

class Calculator {

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

    fun createProfitChart(orderList: List<Order>) {
        // Create profit map
        var profitMap: HashMap<String, Double> = hashMapOf();
        for (key in ItemMap.map.keys) {
            profitMap[key] = 0.0;
        }

        for (order: Order in orderList) { // For each order
            for (item: Item in order.items) { // For each item
                if (profitMap.containsKey(item.name)) { // If item is on the menu
                    val report: SalesReport = SalesReport(item); // Get sales report for item
                    // Add profit from report to total profit for that item
                    profitMap[item.name] = profitMap.getValue(item.name) + report.totalProfit;
                }
            }
        }

        // Create dataset for chart
        var dataset: DefaultPieDataset<String> = DefaultPieDataset();
        // Fill dataset with each item and it's total profit
        for (entry in profitMap.entries) {
            dataset.setValue(entry.key, entry.value);
        }

        // Create chart object from dataset
        var chart: JFreeChart = ChartFactory.createPieChart(
            "Profit of Menu Items", // Title of chart
            dataset,    // Dataset for chart
            true,   // Include legend
            true,   // Include tool tips
            false   // Generate URLs
        );

        // Change labels on chart to display percentages
        val labelGenerator: PieSectionLabelGenerator = StandardPieSectionLabelGenerator("{0} = {2}");
        val plot: PiePlot<String> = chart.plot as PiePlot<String>;
        plot.labelGenerator = labelGenerator;

        // Save the chart as a PNG
        val width = 640; // Width of the image
        val height = 480; // Height of the image
        val chartFile = File("pie-chart.png");
        ChartUtils.saveChartAsPNG(chartFile, chart, width, height);
    }

    fun createQuantityChart(orderList: List<Order>) {
        // Create quantity map
        var quantityMap: HashMap<String, Int> = hashMapOf();
        for (key in ItemMap.map.keys) {
            quantityMap[key] = 0;
        }

        for (order: Order in orderList) { // For each order
            for (item: Item in order.items) { // For each item
                if (quantityMap.containsKey(item.name)) { // If item is on the menu
                    // Add quantity from item to total quantity
                    quantityMap[item.name] = quantityMap.getValue(item.name) + item.quantity;
                }
            }
        }

        // Create dataset for chart
        var dataset: DefaultCategoryDataset = DefaultCategoryDataset();
        // Fill dataset with each item and it's total profit
        for (entry in quantityMap.entries) {
            dataset.addValue(entry.value, "Quantity", entry.key);
        }

        // Create chart object from dataset
        var chart: JFreeChart = ChartFactory.createBarChart(
            "Profit of Menu Items", // Title of chart
            "Item", // X axis title
            "Quantity Ordered", // Y axis title
            dataset,    // Dataset for chart
        );

        // Save the chart as a PNG
        val width = 640; // Width of the image
        val height = 480; // Height of the image
        val chartFile = File("bar-chart.png");
        ChartUtils.saveChartAsPNG(chartFile, chart, width, height);
    }
}