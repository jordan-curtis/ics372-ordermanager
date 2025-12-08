package com.example.ordermanager

import java.io.File
import java.io.FileReader
import javax.json.Json
import javax.json.JsonArray


object OrderManager : IOrderManager {

    /**
     * This class handles all the orders: incoming, started, and completed
     */

    /**
     * Setting up basic constructors
     */

    private val incomingOrders = mutableListOf<Order>()
    private val startedOrders = mutableListOf<Order>()
    private val completedOrders = mutableListOf<Order>()
    private val cancelledOrders = mutableListOf<Order>()


    //Setting up a UI update callback, to ensure anytime a list is changed the UI reflects

    var onOrderChange: (() -> Unit)? = null

    //Return whatever ya got if ya got something that is not null


    /**
     * Getters for all the lists
     */

    override fun getIncomingOrders(): List<Order> = incomingOrders.toList()
    override fun getStartedOrders(): List<Order> = startedOrders.toList()
    override fun getCompletedOrders(): List<Order> = completedOrders.toList()
    override fun getCancelledOrders(): List<Order> = cancelledOrders.toList()


    /**
     * A function to add orders, one to add a single order, one to add multiple orders
     *
     * Both times calling our UI info update function, onOrderChange
     */

    override fun addOrder(order: Order) {
        incomingOrders.add(order)
        onOrderChange?.invoke()
    }

    override fun addManyOrders(orders: List<Order>) {
        incomingOrders.addAll(orders)
        if (orders.isNotEmpty()) {
            onOrderChange?.invoke()
        }
    }


    /**
     * Instantiates the FileHandler class which returns an ArrayList of Orders.
     * Then adds each of the returned Orders to the incomingOrders ArrayList.
     * @author Tommy Fenske
     */
    fun fileFromJSON() {
        // TODO: Commented out code since we don't use this function. Should we delete it?
        //JsonParser jp = new JsonParser();
        //List<Order> newOrders = jp.jsonParsing();
        //incomingOrders.addAll(newOrders);

        // Update GUIController after new orders have been added
    }

    /**
     * Prints each Order object in incomingOrders ArrayList
     * @author Tommy Fenske
     */
    fun printIncomingOrders() {
        //Debug Statement
        //System.out.println("DEBUG: Incoming orders count = " + incomingOrders.size());
        for (order in incomingOrders) {
            println(order.toString())
        }
    }

    /**
     * Prints each Order object in startedOrders ArrayList
     * @author Tommy Fenske
     */
    fun printStartedOrders() {
        for (order in startedOrders) {
            println(order.toString())
        }
    }

    /**
     * Prints each Order object in completedOrders ArrayList
     * @author Tommy Fenske
     */
    fun printCompletedOrders() {
        for (order in completedOrders) {
            println(order.toString())
        }
    }


    /**
     * Searches the incomingOrders List for the orderID,
     * if a matching ID is found, move the order from incomingOrders to startedOrders List and return true
     * if no matching ID is found, return false
     * @return true if ID found, otherwise false
     * @author Majid  Farah
     */
    override fun startOrder(startOrderId: Int): Boolean {
        val iterator = incomingOrders.iterator()

        while (iterator.hasNext()) {
            val order = iterator.next()
            if (order.orderID == startOrderId) {
                try {
                    order.startOrder()
                } catch (e: InvalidOrderStatusChange) {
                    return false
                }

                iterator.remove()
                startedOrders.add(order)
                onOrderChange?.invoke()
                return true
            }
        }

        return false
    }

    /**
     * Searches the startedOrders List for the orderID,
     * if a matching ID is found, move the order from startedOrders to completedOrders List and return true
     * if no matching ID is found, return false
     * @author Majid Farah
     */
    override fun completeOrder(completeOrderId: Int): Boolean {
        val iterator = startedOrders.iterator()

        while (iterator.hasNext()) {
            val order = iterator.next()
            if (order.orderID == completeOrderId) {
                try {
                    order.completeOrder()
                } catch (e: InvalidOrderStatusChange) {
                    return false
                }

                iterator.remove()
                completedOrders.add(order)
                onOrderChange?.invoke()
                return true
            }
        }

        return false
    }

    /**
     * Search incoming and started orders; cancel the match and move it to completed.
     *
     * @param orderID id of the order to cancel
     * @return true when the order is found and cancelled
     */
    override fun cancelOrder(cancelOrderId: Int): Boolean {
        var iterator = incomingOrders.iterator()

        while (iterator.hasNext()) {
            val order = iterator.next()
            if (order.orderID == cancelOrderId) {
                try {
                    order.cancelOrder()
                } catch (e: InvalidOrderStatusChange) {
                    return false
                }

                iterator.remove()
                cancelledOrders.add(order)
                onOrderChange?.invoke()
                return true
            }
        }

        //Check on started orders

        iterator = startedOrders.iterator()

        while (iterator.hasNext()) {
            val order = iterator.next()
            if (order.orderID == cancelOrderId) {
                try {
                    order.cancelOrder()
                } catch (e: InvalidOrderStatusChange) {
                    return false
                }

                iterator.remove()
                cancelledOrders.add(order)
                onOrderChange?.invoke()
                return true
            }
        }

        return false
    }

    /**
     * Searches the all Order Lists for the orderID,
     * Searches all Order Lists for the orderID,
     * if a matching ID is found, return the order
     * if no matching ID is found, return null
     * @author Majid Farah
     */
    override fun getOrder(getOrderId: Int): Order? {
        for (order in incomingOrders) {
            if (order.orderID == getOrderId) {
                return order
            }
        }

        for (order in startedOrders) {
            if (order.orderID == getOrderId) {
                return order
            }
        }

        for (order in completedOrders) {
            if (order.orderID == getOrderId) {
                return order
            }
        }

        return null
    }

    override fun getAllOrders(): List<Order> {
        TODO("Not yet implemented")
    }

    override fun clearAllOrders() {
        TODO("Not yet implemented")
    }


    /**
     * Now imports orders from Json and XML files in a directory, using the parsers.kt
     * @param directory - The directory you want to scan for order files
     * @author Ruben Vallejo
     */
    fun importOrdersFromDirectory(directory: File = File("data")) {

        if (!directory.exists()) {
            directory.mkdir()
            return
        }

        val jsonParser = JsonOrderParser()

        val xmlParser = XmlOrderParser()

        val jsonOrders = jsonParser.parse(directory)

        val xmlOrders = xmlParser.parse(directory)

        addManyOrders(jsonOrders + xmlOrders)

        //Delete the files once processed

        directory.listFiles()?.forEach { file ->
            if (file.extension.lowercase() in listOf("json", "xml")) {
                if (file.delete()) {
                    println("Now deleted ${file.name}")
                }
            }
        }
    }

    /**
     * Data class holding order statistics
     */
    data class OrderStats(
        val incoming: Int,
        val started: Int,
        val completed: Int,
        val cancelled: Int
    ) {
        val total: Int get() = incoming + started + completed + cancelled
    }



    /**
     * Adding functions for saving the state and loading the state of the
     * program
     * @author Ruben
     * @param file A File that is the current "state file"
     */

    fun saveState(file: File = File("data/state.json")) {

        val jsonBuilder = StringBuilder()
        jsonBuilder.append("{\n")
        jsonBuilder.append("\"incoming\": ${ordersToJson((incomingOrders))},\n")
        jsonBuilder.append("\"started\": ${ordersToJson((startedOrders))
        },\n")
        jsonBuilder.append("\"completed\": ${ordersToJson((completedOrders))},\n")
        jsonBuilder.append("\"cancelled\": ${ordersToJson((cancelledOrders))
        }\n")
        jsonBuilder.append("}")



        //Write to the file

        file.writeText(jsonBuilder.toString())
        println("State is now saved to ${file.path}")



    }

    fun loadState(file: File = File("data/state.json")) {

        //If no file just return

        if(!file.exists()) return

        //Read and parse the file

        val reader = Json.createReader(FileReader(file))

        val root = reader.readObject()

        reader.close()

        incomingOrders.clear()
        startedOrders.clear()
        completedOrders.clear()
        cancelledOrders.clear()

        incomingOrders.addAll(parseOrderArray(root.getJsonArray("incoming")))
        startedOrders.addAll(parseOrderArray(root.getJsonArray("started")))
        completedOrders.addAll(parseOrderArray(root.getJsonArray("completed")))
        cancelledOrders.addAll(parseOrderArray(root.getJsonArray("cancelled")))

        onOrderChange?.invoke()


    }

    /**
     * I had to write a quick parsing method specific for the loading and
     * saving parts
     */

    private fun parseOrderArray(jsonArray: JsonArray): List<Order>{

        val orders = mutableListOf<Order>()

        for(i in 0 until jsonArray.size){

            val orderObj = jsonArray.getJsonObject(i)

            val orderType = orderObj.getString("orderType")
            val statusName = orderObj.getString("status")
            val status = Order.OrderStatus.valueOf(statusName)

            //Parsing the items

            val items = mutableListOf<Item>()

            val itemsArray = orderObj.getJsonArray("items")

            for (j in 0 until itemsArray.size){
                val itemObj = itemsArray.getJsonObject(j)
                items.add(Item(itemObj.getString("name"),itemObj.getJsonNumber
                    ("price").doubleValue(),
                    itemObj.getInt("quantity")
                    ))
            }

            //Now generating the loaded orders into proper data for the program

            orders.add(OrderGenerator.orderGenerator(orderType,items,status))

        }

        return orders

    }

    data class StateData(
        val incoming: List<Order>,
        val started: List<Order>,
        val completed: List<Order>,
        val cancelled: List<Order>
    )


    /**Helper functions for orders to json
     * One is for a list of orders
     * The other for a single order
     */

    //List of orders
    private fun ordersToJson(orders: List<Order>): String {
        if (orders.isEmpty()) return "[]"

        val items = orders.map { order -> singleOrderToJson(order) }
        return "[\n${items.joinToString(",\n")}\n ]"

    }

    //Single order

    private fun singleOrderToJson(order: Order): String {


        val jsonBuilder = StringBuilder()
        jsonBuilder.append("{\n")
        jsonBuilder.append("\"orderId\": ${order.orderID},\n")
        jsonBuilder.append("\"orderType\": \"${order.orderType}\",\n")
        jsonBuilder.append("\"status\": \"${order.status}\",\n")
        jsonBuilder.append("\"items\": [\n")

        //Looping through multiples items or multiples of same item

        order.items.forEachIndexed { index, item ->
            jsonBuilder.append("{\"name\": \"${item.name}\", \"price\": ${item.price}, " +
                    "\"quantity\": ${item.quantity}}")

            if (index < order.items.size - 1) jsonBuilder.append(",")
            jsonBuilder.append("\n")

        }
        jsonBuilder.append("]\n")
        jsonBuilder.append("}")

        return jsonBuilder.toString()


    }
}




/* commenting out for now, the Kotlin refactoring allows for a different use, where we can call the

/**
 * Sets the value of the pollDirectory boolean, starting or stopping polling of the data directory.
 * @param isPolling determines if Thread should poll directory.
 */
fun toggleWatcher(isPolling: Boolean) {
    ordersystem.OrderManager.Companion.pollDirectory = isPolling
}

companion object {
    private var guiController: GUIController? = null
    private var pollDirectory = true



    /**
     * Called in the GUIController's start() function.
     * Creates a new thread that sleeps for 1000ms, before calling the FileImporterFacade to parse any files
     * in the data directory. Files are then deleted to prevent duplication.
     * @author Tommy Fenske
     */
    fun setupWatcher() {
        val t = Thread(Runnable {
            val dataDir = File("data")
            if (!dataDir.exists()) {
                dataDir.mkdir()
            }
            while (ordersystem.OrderManager.Companion.pollDirectory) {
                try {
                    //System.out.println("Sleep");
                    Thread.sleep(1000)
                } catch (e: InterruptedException) {
                    throw RuntimeException(e)
                }
                Platform.runLater({
                    if (dataDir.list().size <= 0) return@runLater
                    // Setup FileImporterFacade
                    val facade: FileImporterFacade = FileImporterFacade()
                    // Get parsed orders from importer
                    val incoming: MutableList<Order?> = facade.fileImport()

                    // If new orders need to be added
                    if (!incoming.isEmpty()) {
                        // Add parsed orders to the incomingOrders ArrayList, then update GUI
                        ordersystem.OrderManager.Companion.incomingOrders.addAll(incoming)
                        ordersystem.OrderManager.Companion.guiController.updateGUIOrders()
                    }

                    // Delete each file so it isn't parsed again
                    for (s in dataDir.list()) {
                        // Get reference to individual file
                        val currentFile = File(dataDir.getPath() + "/" + s)
                        // Delete file
                        if (currentFile.delete()) {
                            println("Deleted the file: " + currentFile.getName())
                        } else {
                            println("Failed to delete the file: " + currentFile.getName())
                        }
                    }
                })
            }
        })
        t.setName("Polling Thread")
        t.start()
    }
}
}
}
 */