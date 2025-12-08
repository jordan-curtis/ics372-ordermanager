package com.example.ordermanager

/**
 * OrderGenerator is a singleton that returns the appropriate type of order based on the String read
 * from the JSON or XML file. This allows the Parsers to avoid needing to know what every type of order is.
 *
 * @author Jordan Curtis
 */
object OrderGenerator {

    /**
     * overloading In Kotlin, this means having multiple functions with the
     * same name but different parameters. The compiler picks which one to
     * call based on the arguments passed. This lets us use OrderGenerator
     * for both new order imports AND loading saved state.
     */

    //Takes in only two params, will default orders to INCOMING, this is for
    // generating NEW orders
    fun orderGenerator(type: String, items: List<Item>): Order{
        return orderGenerator(type,items,Order.OrderStatus.INCOMING)
    }


    /**
     * This was added becuase for the load state, all orders were getting
     * loaded in as INCOMING not their respective places
     */

    //Takes in params of specific order status

    fun orderGenerator(type: String, items: List<Item>, status: Order
        .OrderStatus): Order {
        //return when(type.lowercase()

        //Now sets the status specific to the status of the Order
        val order = when(type.lowercase()) {
            "togo" -> TogoOrder(items)
            "pickup" -> PickupOrder(items)
            "delivery" -> DeliveryOrder(items)
            else -> TogoOrder(items)
        }

        //Setting status to the matched saved status

        order.status = status

        return order

    }
}