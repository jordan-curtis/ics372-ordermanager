package com.example.ordermanager

data class Item(var name: String = "default",
    var price : Double = 0.0, var quantity : Int = 0) {

    fun getTotal() : Double = price * quantity

    override fun toString(): String {
        return "Item: $name, Price: ${"%.2f".format(price)}, Quantity $quantity"
    }

}