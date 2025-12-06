package com.example.ordermanager

import java.time.LocalDateTime

/**
 * Abstract Order class guarantees the base functionality of each order type is shared between all current and types.
 * The goal is to separate functionality between subclasses to allow for specific tracking metrics for graphing purposes,
 * as well as the ability to separate based on subclass types with shared custom functionality.
 *
 * @author Jordan Curtis
 */
abstract class Order(val items: List<Item>, initialStatus: OrderStatus = OrderStatus.INCOMING) {
    enum class OrderStatus { INCOMING, STARTED, COMPLETED, CANCELLED }
    enum class OrderType { TOGO, PICKUP, DELIVERY }

    companion object { private var latestID: Int = 0 }

    abstract val orderType: String

    val orderID: Int = ++latestID
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

/**
 * To-Go Orders have added functionality for tracking the order's readiness.
 *
 * @author Jordan Curtis
 */
class TogoOrder(items: List<Item>, var togoStatus: TogoStatus = TogoStatus.PREPARING) : Order(items) {
    enum class TogoStatus { PREPARING, READY }
    override val orderType: String = OrderType.TOGO.name

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

/**
 * Pick-Up Orders have added functionality for tracking the order's readiness.
 *
 * @author
 */
class PickupOrder(items: List<Item>, var pickupStatus: PickupStatus = PickupStatus.PREPARING) : Order(items) {
    enum class PickupStatus { PREPARING, READY }

    override val orderType: String = OrderType.PICKUP.name

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

/**
 * Delivery Orders have adjusted functionality for tracking whether the order is being delivered.
 *
 * @author Jordan Curtis
 */
class DeliveryOrder(items: List<Item>, var deliveryStatus: DeliveryStatus = DeliveryStatus.PREPARING) : Order(items) {
    enum class DeliveryStatus { PREPARING, DELIVERING, ARRIVED }
    override val orderType: String = OrderType.DELIVERY.name

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

class InvalidOrderStatusChange(message: String) : Exception(message)