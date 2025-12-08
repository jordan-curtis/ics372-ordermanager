package com.example.ordermanager


import java.io.File
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.*

object DirectoryThread {
    var shouldRun : Boolean = true;

    fun runThread() {
        GlobalScope.launch {
            withContext(Dispatchers.Default) { // this: CoroutineScope
                // Ensure data directory is created
                val dataDir: File = File("data");
                if (!dataDir.exists()) { dataDir.mkdir() };

                while (shouldRun) {
                    val files: Array<File> = dataDir.listFiles();
                    if (files.isNotEmpty()) {
                        val jsonParser: JsonOrderParser = JsonOrderParser();
                        val xmlParser: XmlOrderParser = XmlOrderParser();

                        val orders : MutableList<Order> = mutableListOf<Order>();
                        orders.addAll(jsonParser.parse(dataDir));
                        orders.addAll(xmlParser.parse(dataDir));

                        // Delete files after parsing
                        for (file in files) {
                            println("Deleting: ${file.name}");
                            file.delete();
                        }
                        OrderManager.addManyOrders(orders);
                    }
                    delay(1.seconds)
                }
            }
        }
    }
}