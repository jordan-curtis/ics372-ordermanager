package com.example.ordermanager

import java.time.LocalDateTime

class Order(val orderType: String, val items: MutableList<Item>, initialStatus: OrderStatus = OrderStatus.INCOMING)

{

    enum class OrderStatus {
        INCOMING,
        STARTED,
        COMPLETED,
        CANCELED

    }
    companion object {
        private var latestId: Int = 0
    }

    val orderId: Int = ++latestId
    var status: OrderStatus = initialStatus
    private set //Making the setter private, keeping the getter public

    //Calculated total (it is evaluated each time it is run)

    val total : Double get() = items.sumOf { it.getTotal() }

    val openTime: LocalDateTime = LocalDateTime.now()
    var closeTime: LocalDateTime? = null
    private set

    val displayTime = closeTime ?: "Not accessible"



    //Making the order function classes, basic for now

    //Start Order Method

    /**
     * Changes the status of an order to started
     * If an order has already been started or is canceled, then it throws an error message
     */
    fun startOrder() {

        if(status == OrderStatus.STARTED || status == OrderStatus.CANCELED){

            throw InvalidOrderStatusChange("Order has already been started or canceled")

        }

        status = OrderStatus.STARTED

    }



    //Complete Order

    fun completeOrder(){
        //Using require to ensure only orders of status started are able to be passed through
        //If not of status STARTED this will throw an error

        require(status == OrderStatus.STARTED){
            "Order has to be started before completing!"
        }

        //Marking as complete, and marking the time when it was done

        status = OrderStatus.COMPLETED
        closeTime = LocalDateTime.now()
    }


    //Cancel Order

    fun cancelOrder(){

        //Using check for internal validation

        check(status != OrderStatus.COMPLETED && status != OrderStatus.CANCELED){
            "Order has already been closed"
        }

        closeTime = LocalDateTime.now()

        status = OrderStatus.CANCELED



    }

    //Error Message Class

    class InvalidOrderStatusChange(message: String) : Exception(message)


}
