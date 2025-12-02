package com.example.ordermanager

import java.io.File

//helper fun to help me print cool line :D
fun printLine() {
    println("=".repeat(30))
}

/*
    *-_-*this is a small test file to check if the parsers work.*-_-*
    *-_-*it uses the testFiles folder.*-_-*
*/

fun main() {

    val jsonParser = JsonOrderParser()
    val xmlParser = XmlOrderParser()

    val folder = File("testFiles")

    printLine()
    println("*-_-*testing json parser*-_-*")
    printLine()

    val jsonOrders = jsonParser.parse(folder)
    jsonOrders.forEachIndexed { index, order ->
        println("from file order${index + 1}.json -> ${order.items}")
    }

    printLine()
    println("*-_-*testing xml parser*-_-*")
    printLine()

    val xmlOrders = xmlParser.parse(folder)
    xmlOrders.forEachIndexed { index, order ->
        println("from file order${index + 4}.xml -> ${order.items}")
    }
}
