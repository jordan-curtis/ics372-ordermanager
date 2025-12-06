package com.example.ordermanager

import com.example.ordermanager.Order.InvalidOrderStatusChange
import java.time.LocalDateTime

abstract class Orderable(val items: List<Item>, initialStatus: OrderStatus = OrderStatus.INCOMING) {
    enum class OrderStatus { INCOMING, STARTED, COMPLETED, CANCELLED }

    companion object { private var staticID: Int = 0 }

    abstract val orderType: String

    val orderID: Int = ++staticID
    var status: OrderStatus = initialStatus
    val total: Double get() = items.sumOf { it.getTotal() }
    val incomingTime: LocalDateTime = LocalDateTime.now()
    var completedTime: LocalDateTime? = null

    fun startOrder() {
        if(status == OrderStatus.STARTED || status == OrderStatus.CANCELLED){
            throw InvalidOrderStatusChange("Order has already been started or canceled")
        }

        status = OrderStatus.STARTED
    }

    abstract fun completeOrder()

    fun cancelOrder() {
        check(status != OrderStatus.COMPLETED || status != OrderStatus.CANCELLED) {
            "Order has already been completed or cancelled."
        }

        completedTime = LocalDateTime.now()
        status = OrderStatus.CANCELLED
    }

}

class PickupOrder(items: List<Item>, var pickupStatus: PickupStatus = PickupStatus.PREPARING) : Orderable(items) {
    enum class PickupStatus { PREPARING, READY }

    override val orderType: String
        get() = "PICKUP"

    override fun completeOrder() {
        require(super.status == OrderStatus.STARTED) {
            "Order must be started before it can become completed."
        }

        super.status = OrderStatus.COMPLETED
        super.completedTime = LocalDateTime.now()
        pickupStatus = PickupStatus.READY
    }

    override fun toString(): String {
        return "Order Type: $orderType\nOrder Status: $status\nPickup Status: $pickupStatus"
    }
}

class TogoOrder(items: List<Item>, var togoStatus: TogoStatus = TogoStatus.PREPARING) : Orderable(items) {
    enum class TogoStatus { PREPARING, READY }
    override val orderType: String
        get() = "TO-GO"

    override fun completeOrder() {
        require(super.status == OrderStatus.STARTED) {
            "Order must be started before it can become completed."
        }

        super.status = OrderStatus.COMPLETED
        super.completedTime = LocalDateTime.now()
        togoStatus = TogoStatus.READY
    }

    override fun toString(): String {
        return "Order Type: $orderType\nOrder Status: $status\nTogo Status: $togoStatus"
    }
}

class DeliveryOrder(items: List<Item>, var deliveryStatus: DeliveryStatus = DeliveryStatus.PREPARING) : Orderable(items) {
    enum class DeliveryStatus { PREPARING, DELIVERING, ARRIVED }
    override val orderType: String
        get() = "DELIVERY"

    // Allows for the ability to have a departure time for the delivery.
    var driverDepartureTime: LocalDateTime? = null

    override fun completeOrder() {
        require(super.status == OrderStatus.STARTED) {
            "Order must be started before it can become completed."
        }

        super.status = OrderStatus.COMPLETED
        super.completedTime = LocalDateTime.now()
        deliveryStatus = DeliveryStatus.ARRIVED
    }

    // Allows for the ability to list a delivery order as currently being delivered.
    fun deliveringOrder() {
        require(deliveryStatus == DeliveryStatus.PREPARING) {
            "Order cannot enter delivery step if the order is not already in the preparing step."
        }

        driverDepartureTime = LocalDateTime.now()
        deliveryStatus = DeliveryStatus.DELIVERING
    }

    override fun toString(): String {
        return "Order Type: $orderType\nOrder Status: $status\nDelivery Status: $deliveryStatus"
    }
}

fun main() {
    val items: List<Item> = listOf(Item("Burger", 5.00))

    val orders: List<Orderable> = listOf(
        PickupOrder(items),
        TogoOrder(items),
        DeliveryOrder(items)
    )

    // Loops through each Order, printing out each Item in the Order.
    for(order in orders) {
        println(order.toString())
        for(item in order.items) {
            println(item.toString())
        }
        println()
    }
}