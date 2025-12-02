package com.example.ordermanager

import java.io.File

class OrderManager {
    package ordersystem

    import javafx.application.Platform
    /**
     * This class handles all the orders: incoming, started, and completed
     */
    class OrderManager {
        constructor() {
            ordersystem.OrderManager.Companion.incomingOrders = ArrayList<Order>()
            ordersystem.OrderManager.Companion.startedOrders = ArrayList<Order>()
            ordersystem.OrderManager.Companion.completedOrders = ArrayList<Order>()
            ordersystem.OrderManager.Companion.cancelledOrders = ArrayList<Order?>()
        }

        constructor(controller: GUIController) {
            ordersystem.OrderManager.Companion.guiController = controller

            ordersystem.OrderManager.Companion.incomingOrders = ArrayList<Order>()
            ordersystem.OrderManager.Companion.startedOrders = ArrayList<Order>()
            ordersystem.OrderManager.Companion.completedOrders = ArrayList<Order>()
            ordersystem.OrderManager.Companion.cancelledOrders = ArrayList<Order?>()
        }

        fun setGUIController(controller: GUIController) {
            ordersystem.OrderManager.Companion.guiController = controller
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
            for (order in ordersystem.OrderManager.Companion.incomingOrders) {
                System.out.println(order.toString())
            }
        }

        /**
         * Prints each Order object in startedOrders ArrayList
         * @author Tommy Fenske
         */
        fun printStartedOrders() {
            for (order in ordersystem.OrderManager.Companion.startedOrders) {
                System.out.println(order.toString())
            }
        }

        /**
         * Prints each Order object in completedOrders ArrayList
         * @author Tommy Fenske
         */
        fun printCompletedOrders() {
            for (order in ordersystem.OrderManager.Companion.completedOrders) {
                System.out.println(order.toString())
            }
        }

        val incomingOrders: MutableList<Order>
            // Return the list of incoming orders
            get() = ordersystem.OrderManager.Companion.incomingOrders

        val startedOrders: MutableList<Order>
            // Return the list of started orders
            get() = ordersystem.OrderManager.Companion.startedOrders
        val completedOrders: MutableList<Order>
            // Return the list of completed orders
            get() = ordersystem.OrderManager.Companion.completedOrders


        /**
         * Searches the incomingOrders List for the orderID,
         * if a matching ID is found, move the order from incomingOrders to startedOrders List and return true
         * if no matching ID is found, return false
         * @return true if ID found, otherwise false
         * @author Majid  Farah
         */
        fun startOrder(orderID: Int): Boolean {
            val iterator: MutableIterator<Order> = ordersystem.OrderManager.Companion.incomingOrders.iterator()

            while (iterator.hasNext()) {
                val order = iterator.next()
                if (order.getOrderID() === orderID) {
                    try {
                        order.startOrder()
                    } catch (e: InvalidOrderStatusChange) {
                        return false
                    }

                    iterator.remove()
                    ordersystem.OrderManager.Companion.startedOrders.add(order)
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
        fun completeOrder(orderID: Int): Boolean {
            val iterator: MutableIterator<Order> = ordersystem.OrderManager.Companion.startedOrders.iterator()

            while (iterator.hasNext()) {
                val order = iterator.next()
                if (order.getOrderID() === orderID) {
                    try {
                        order.completeOrder()
                    } catch (e: InvalidOrderStatusChange) {
                        return false
                    }

                    iterator.remove()
                    ordersystem.OrderManager.Companion.completedOrders.add(order)
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
        fun cancelOrder(orderID: Int): Boolean {
            var iterator: MutableIterator<Order> = ordersystem.OrderManager.Companion.incomingOrders.iterator()

            while (iterator.hasNext()) {
                val order = iterator.next()
                if (order.getOrderID() === orderID) {
                    try {
                        order.cancelOrder()
                    } catch (e: InvalidOrderStatusChange) {
                        return false
                    }

                    iterator.remove()
                    ordersystem.OrderManager.Companion.cancelledOrders.add(order)
                    return true
                }
            }

            iterator = ordersystem.OrderManager.Companion.startedOrders.iterator()

            while (iterator.hasNext()) {
                val order = iterator.next()
                if (order.getOrderID() === orderID) {
                    try {
                        order.cancelOrder()
                    } catch (e: InvalidOrderStatusChange) {
                        return false
                    }

                    iterator.remove()
                    ordersystem.OrderManager.Companion.cancelledOrders.add(order)
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
        fun getOrder(orderID: Int): Order? {
            for (order in ordersystem.OrderManager.Companion.incomingOrders) {
                if (order.getOrderID() === orderID) {
                    return order
                }
            }

            for (order in ordersystem.OrderManager.Companion.startedOrders) {
                if (order.getOrderID() === orderID) {
                    return order
                }
            }

            for (order in ordersystem.OrderManager.Companion.completedOrders) {
                if (order.getOrderID() === orderID) {
                    return order
                }
            }

            return null
        }

        /**
         * Calling our FileExport to be used by the user to export the orders
         * into one Json file
         * @author Ruben Vallejo
         */
        fun fileExport() {
            val fileToExport: ExportFile = ExportFile()
            fileToExport.exportOrdersToJSON(
                this.incomingOrders,
                this.startedOrders, this.completedOrders
            )
        }

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

            private val incomingOrders: MutableList<Order>
            private val startedOrders: MutableList<Order>
            private val completedOrders: MutableList<Order>
            private val cancelledOrders: MutableList<Order?>

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