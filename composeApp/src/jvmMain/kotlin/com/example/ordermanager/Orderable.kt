package com.example.ordermanager

import com.example.ordermanager.Order.InvalidOrderStatusChange
import java.time.LocalDateTime

abstract class Orderable(val items: MutableList<Item>, initialStatus: OrderStatus = OrderStatus.INCOMING) {
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

    override fun toString(): String {
        return "$orderType Order"
    }
}

class PickupOrder(items: MutableList<Item>, var pickupStatus: PickupStatus = PickupStatus.PREPARING) : Orderable(items) {
    enum class PickupStatus { PREPARING, READY }

    override val orderType: String
        get() = "Pickup"

    override fun completeOrder() {
        require(super.status == OrderStatus.STARTED) {
            "Order must be started before it can become completed."
        }

        super.status = OrderStatus.COMPLETED
        super.completedTime = LocalDateTime.now()
        pickupStatus = PickupStatus.READY
    }
}

class TogoOrder(items: MutableList<Item>, var togoStatus: TogoStatus = TogoStatus.PREPARING) : Orderable(items) {
    enum class TogoStatus { PREPARING, READY }
    override val orderType: String
        get() = "To-Go"

    override fun completeOrder() {
        require(super.status == OrderStatus.STARTED) {
            "Order must be started before it can become completed."
        }

        super.status = OrderStatus.COMPLETED
        super.completedTime = LocalDateTime.now()
        togoStatus = TogoStatus.READY
    }
}

class DeliveryOrder(items: MutableList<Item>, var deliveryStatus: DeliveryStatus = DeliveryStatus.PREPARING) : Orderable(items) {
    enum class DeliveryStatus { PREPARING, DELIVERING, ARRIVED }
    override val orderType: String
        get() = "Delivery"

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
}

fun main() {
    val items: MutableList<Item> = mutableListOf(Item())

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