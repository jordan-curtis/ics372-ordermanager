package com.example.ordermanager

//our project does not have the JSON library installed so i have added in the bulid.gradle.kts file

import java.io.File
import java.io.FileReader
import javax.json.Json
import javax.json.JsonObject
import javax.xml.parsers.DocumentBuilderFactory
import org.w3c.dom.Element
import org.w3c.dom.Node

// simple interface so both json and xml parsers work the same way
interface OrderParser {
    fun parse(dir: File): List<Order>
}

// json parser
class JsonOrderParser : OrderParser {

    override fun parse(dir: File): List<Order> {
        val results = mutableListOf<Order>()

        // only files ending with .json
        val jsonFiles = dir.listFiles { _, name ->
            name.lowercase().endsWith(".json")
        } ?: return results

        // go through json files
        for (file in jsonFiles) {
            try {
                // read json file
                val reader = Json.createReader(FileReader(file))
                val root: JsonObject = reader.readObject()
                reader.close()

                // same idea as xml: get main order part
                val orderObj = root.getJsonObject("order")
                val orderType = orderObj.getString("type")


                val items = mutableListOf<Item>()
                val itemsArray = orderObj.getJsonArray("items")

                for (i in 0 until itemsArray.size) {
                    val itemObj = itemsArray.getJsonObject(i)

                    val name = itemObj.getString("name")
                    val price = itemObj.getJsonNumber("price").doubleValue()
                    val qty = itemObj.getInt("quantity")

                    items.add(Item(name, price, qty))
                }

                // add final order
                results.add(
                    Order(
                        orderType,
                        items,
                        Order.OrderStatus.INCOMING
                    )
                )

            } catch (e: Exception) {
                println("error reading json file: ${file.name}")
            }
        }

        return results
    }
}

// xml parser
class XmlOrderParser : OrderParser {

    override fun parse(dir: File): List<Order> {
        val results = mutableListOf<Order>()


        val xmlFiles = dir.listFiles { _, name ->
            name.lowercase().endsWith(".xml")
        } ?: return results

        // loop
        for (file in xmlFiles) {
            try {

                val factory = DocumentBuilderFactory.newInstance()
                val builder = factory.newDocumentBuilder()
                val doc = builder.parse(file)
                doc.documentElement.normalize()


                val orderNode = doc.getElementsByTagName("Order").item(0) as Element
                val orderType = orderNode
                    .getElementsByTagName("OrderType")
                    .item(0).textContent


                val items = mutableListOf<Item>()
                val itemNodes = doc.getElementsByTagName("Item")

                for (i in 0 until itemNodes.length) {
                    val node = itemNodes.item(i)
                    if (node.nodeType == Node.ELEMENT_NODE) {
                        val elem = node as Element

                        val name = elem.getAttribute("type")
                        val price = elem.getElementsByTagName("Price")
                            .item(0).textContent.toDouble()
                        val qty = elem.getElementsByTagName("Quantity")
                            .item(0).textContent.toInt()

                        items.add(Item(name, price, qty))
                    }
                }

                // add final order
                results.add(
                    Order(
                        orderType,
                        items,
                        Order.OrderStatus.INCOMING
                    )
                )

            } catch (e: Exception) {
                println("error reading xml file: ${file.name}")
            }
        }

        return results
    }
}
