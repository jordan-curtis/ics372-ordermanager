package com.example.ordermanager

/**
 * OrderGenerator is a singleton that returns the appropriate type of order based on the String read
 * from the JSON or XML file. This allows the Parsers to avoid needing to know what every type of order is.
 *
 * @author Jordan Curtis
 */
object OrderGenerator {

    fun orderGenerator(type: String, items: List<Item>): Order {
        return when(type.lowercase()) {
            "togo" -> TogoOrder(items)
            "pickup" -> PickupOrder(items)
            "delivery" -> DeliveryOrder(items)
            else -> TogoOrder(items)
        }
    }
}