package com.example.ordermanager

import javax.swing.event.ChangeListener

interface IOrderManager {

    /**
     * Inrterface that has been adapted from previous orderManager. This now allows for different
     * implementations of managing orders and gives testing capability with mock order ability
     *
     */

    /**
     * Returns the list of incoming orders
     */
    fun getIncomingOrders(): List<Order>

    /**
     * Returns the list of started orders
     */
    fun getStartedOrders(): List<Order>

    /**
     * Returns the list of started orders
     */
    fun getCompletedOrders(): List<Order>

    /**
     * Returns the list of started orders
     */
    fun getCancelledOrders(): List<Order>

    /**
     * Returns the list of started orders
     * @param order is a single order to add
     */
    fun addOrder(order: Order)

    /**
     * Adds MANY orders at a time not just one
     * @param orders are the many added orders
     */
    fun addOrders(orders: List<Order>)

    /**
     * Returns the list of started orders
     * @param order is a single order to add
     */
    fun startOrder(order: Order)

    /**
     * Returns the list of started orders
     * @param order is a single order to add
     */
    fun completeOrder(order: Order)

    /**
     * Returns the list of started orders
     * @param order is a single order to add
     */
    fun cancelOrder(order: Order)

    fun getOrder(orderId: Int): Order? //Not null

    fun getAllOrders(): List<Order>

    fun clearAllOrders()

    fun addOrderChangeListener (listener: OrderChangeListener)

    fun removeOrderChangeListener (listener: OrderChangeListener)


    /**
     * Listener interface, this is used for the notification of the actual order change,
     * this is helpful for the UI implementation as it can be focused on this specific interface
     */

    fun interface OrderChangeListener{

        /**
         * Call this when the order lists have been modified in any way
         */

        fun synchOrders()
    }

}