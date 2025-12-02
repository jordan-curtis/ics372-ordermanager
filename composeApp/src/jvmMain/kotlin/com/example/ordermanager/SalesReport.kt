package com.example.ordermanager

/**
 * Takes either an item, order, or list of orders and calculates 3 values: totalExpense, totalPrice, and totalProfit.
 * @property totalExpense expense restaurant pays for all items in report
 * @property totalPrice price the customer pays for all items in report
 * @property totalProfit profit the restaurant makes for all items in report
 * @author Tommy Fenske
 */
class SalesReport {
    var totalProfit: Double = 0.0;
    var totalExpense: Double = 0.0;
    var totalPrice: Double = 0.0;

    /**
     * Constructor for one item
     * @param i Item to generate SalesReport for
     */
    constructor(i: Item) {
        totalExpense = i.quantity * ItemMap.map.getValue(i.name);
        totalPrice = i.getTotal();
        totalProfit = totalPrice - totalExpense;
    }

    /**
     * Constructor for one order
     * Adds together the expense, price, and profit of all items in the order.
     * @param order Order to generate SalesReport for
     */
    constructor(order: Order) {
        for (item: Item in order.items) {
            var report: SalesReport = SalesReport(item);
            addReport(report);
        }
    }

    /**
     * Constructor for a list of orders
     * Adds together the expense, price, and profit of all orders in the list.
     * @param list List of orders to generate SalesReport for
     */
    constructor(list: List<Order>) {
        for (order: Order in list) {
            var report: SalesReport = SalesReport(order);
            addReport(report);
        }
    }

    /**
     * Adds values from a different SalesReport
     * @param report report to add values from
     */
    private fun addReport(report: SalesReport) {
        this.totalExpense += report.totalExpense;
        this.totalPrice += report.totalPrice;
        this.totalProfit += report.totalProfit;
    }
}