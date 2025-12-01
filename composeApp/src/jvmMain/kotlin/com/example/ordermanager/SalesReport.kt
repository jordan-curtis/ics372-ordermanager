package com.example.ordermanager

class SalesReport {
    var totalProfit: Double = 0.0;
    var totalExpense: Double = 0.0;
    var totalPrice: Double = 0.0;

    constructor(i: Item) {
        totalExpense = i.quantity * ItemMap.map.getValue(i.name);
        totalPrice = i.getTotal();
        totalProfit = totalPrice - totalExpense;
    }

    constructor(o: Order) {
        for (item: Item in o.items) {
            var report: SalesReport = SalesReport(item);
            addReport(report);
        }
    }

    constructor(l: List<Order>) {
        for (o: Order in l) {
            var report: SalesReport = SalesReport(o);
            addReport(report);
        }
    }

    fun addReport(report: SalesReport) {
        this.totalExpense += report.totalExpense;
        this.totalPrice += report.totalPrice;
        this.totalProfit += report.totalProfit;
    }
}