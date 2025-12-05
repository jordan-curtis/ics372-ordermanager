package com.example.ordermanager

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import org.jfree.chart.title.Title
import java.io.File

/**
 * Main portion of the order management UI, the screen with the three columns:
 * Incoming, Started and Completed
 *
 * @author Ruben Vallejo
 */

@Composable
fun OrderGUI(){

    //Create an Ordermanager instance

    val orderManager = remember { OrderManager() }

    //This is the trigger for recomposing the screen

    var recompTrigger by remember { mutableStateOf(0) }

    //var for data analytics, will remember if it is being shown or not

    var showAnalytics by remember { mutableStateOf(false) }

    //Registering the callback to actually refresh the UI when orders change

    //Recomposed each and every time there is an order change

    LaunchedEffect(orderManager){
        orderManager.onOrderChange = {
            recompTrigger++
        }
    }


    //Reading in the current orders and triggering a recomposition when orders change

    val incomingOrders = remember(recompTrigger) {orderManager.getIncomingOrders()}
    val startedOrders = remember(recompTrigger) {orderManager.getStartedOrders()}
    val completedOrders = remember(recompTrigger) {orderManager.getCompletedOrders()}

    /**
     * Setting up the UI elements
     */

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)){
        //This is my header

        Text(text = "New Order Manager", fontSize = 28.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 16.dp))


        //This is my button row for testing

        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)){

            //Add sample order button

            Button(
                onClick = {
                    val sampleOrder = Order(
                        orderType = "Dine-in",
                        items = mutableListOf(
                            Item("Burger", 8.99, 1),
                            Item("Fries", 3.99, 2)
                        )
                    )
                    orderManager.addOrder(sampleOrder)
                }
            ) {
                Text("Add Sample Order")
            }

            // Import from directory button
            Button(
                onClick = {
                    orderManager.importOrdersFromDirectory()
                }
            ) {
                Text("Import Orders")
            }


            Spacer(modifier = Modifier.weight(1f))

            //Analytics Button, for a new pop up of analytics

            Button(onClick = {showAnalytics = true}, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)){
                Text("Click to view analytics")
            }

        }

        // Three columns, our main section of our UI

        Row(modifier = Modifier.weight(1f)) {
            // Incoming Orders Column
            OrderColumn(
                title = "Incoming (${incomingOrders.size})",
                orders = incomingOrders,
                modifier = Modifier.weight(1f),
                actionButton = { order ->
                    Button(
                        onClick = { orderManager.startOrder(order.orderId) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Start")
                    }
                }
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Started Orders Column
            OrderColumn(
                title = "Started (${startedOrders.size})",
                orders = startedOrders,
                modifier = Modifier.weight(1f),
                actionButton = { order ->
                    Column {
                        Button(
                            onClick = { orderManager.completeOrder(order.orderId) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Complete")
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedButton(
                            onClick = { orderManager.cancelOrder(order.orderId) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Cancel")
                        }
                    }
                }
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Completed Orders Column
            OrderColumn(
                title = "Completed (${completedOrders.size})",
                orders = completedOrders,
                modifier = Modifier.weight(1f),
                actionButton = null // No actions for completed orders
                    )
            }
        }

    // Analytics popup dialog
    if (showAnalytics) {
        OrderAnalytics(
            completedOrders = completedOrders,
            onDismiss = { showAnalytics = false }
        )
    }

    }

/**
 * This is for the content of a column itself, implement each column as needed
 *
 */
@Composable
fun OrderColumn(
    title: String, orders: List<Order>, modifier: Modifier = Modifier, actionButton: (@Composable (Order) -> Unit)? = null
){
    Card(
        modifier = modifier.fillMaxHeight(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ){
        Column(modifier = Modifier.padding(12.dp)){

            //This is the header of the COLUMN itself

            Text(
                text = title, fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 12.dp)
            )

            //Implementing a scrollable list of orders

            LazyColumn(
                modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(8.dp)
            ){
                items(orders, key = {it.orderId}) {order -> OrderCard(order = order, actionButton = actionButton)}

            }

        }

    }
}

/**
 * This part is for the order card, meaning each single order being given its own card, a list is of a set of cards
 */

@Composable
fun OrderCard(order: Order, actionButton: (@Composable (Order) -> Unit)? = null)
{
    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface))
    {

        Column(
            modifier = Modifier.padding(12.dp)
        ){
            //Headers for order data

            //For order ID
            Text(
                text = "Order: #${order.orderId}", fontWeight = FontWeight.Bold, fontSize =  16.sp
            )

            //For order type
            Text(
                text = "Type: #${order.orderType}", fontWeight = FontWeight.Bold, fontSize =  16.sp, color = MaterialTheme.colorScheme.onSurfaceVariant
            )


            //For order status
            Text(
                text = "Status: #${order.status}", fontWeight = FontWeight.Bold, fontSize =  16.sp, color = MaterialTheme.colorScheme.onSurfaceVariant
            )


            Spacer(modifier = Modifier.height(8.dp))

            //NOW WE FINALLY GET TO THE ACTUAL LIST POPULATING INTO THE UI CARD

            order.items.forEach { item -> Text(
                text = "• ${item.name} x${item.quantity} - $${String.format("%.2f", item.price)}",
                fontSize = 16.sp,
            )
            }

            Spacer(modifier = Modifier.height(8.dp))

            //For showing totals

            Text(text = "Total: $${String.format("%.2f", order.total)}",
                fontWeight = FontWeight.Bold)

            if (actionButton != null) {
                Spacer(modifier = Modifier.height(8.dp))
                actionButton(order)
            }


        }

    }
}

/**
 * Carrying over the chart display function
 * @param orders The list of orders, currently takes in ALL orders
 * @author Ruben
 */
@Composable
fun chartDisplay(orders : List<Order>){

    var chartImage by remember { mutableStateOf<ImageBitmap?>(null) }

    //Generating chart when app loads

    Column {
        Button(onClick = {
            // Initialize ChartBuilders
            val profitBuilder: ProfitChartBuilder = ProfitChartBuilder();
            val quantityBuilder: QuantityChartBuilder = QuantityChartBuilder();
            // Build charts
            profitBuilder.buildChart(orders);
            quantityBuilder.buildChart(orders);
            // Get references to Files
            val profitFile: File = profitBuilder.getFile();
            val quantityFile: File = quantityBuilder.getFile();
            // Update chartImage
            val chartBytes = profitFile.readBytes();
            chartImage = org.jetbrains.skia.Image.makeFromEncoded(chartBytes).toComposeImageBitmap()

            /*
            //This WILL create a file from the current running application but will default to our test file
            if(orders.isNotEmpty()){
                calculator.createProfitChart(orders)
                val chartFile = File("Output-chart.png")
                if(chartFile.exists()){
                    //Png to bitmap
                    val chartBytes = chartFile.readBytes()
                    chartImage = org.jetbrains.skia.Image.makeFromEncoded(chartBytes).toComposeImageBitmap()
                }
            }
            else {
                //Use our test chart if no orders exist
                calculator.chartTest()
                val chartFile = File("chart.png")
                if(chartFile.exists()){
                    //Png to bitmap
                    val chartBytes = chartFile.readBytes()
                    chartImage = org.jetbrains.skia.Image.makeFromEncoded(chartBytes).toComposeImageBitmap()
                }
            }
             */


        }){
            Text("Generate Profit Chart")
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    if(chartImage != null){
        Image(bitmap = chartImage!!, contentDescription = "Profit Stats", modifier = Modifier.fillMaxWidth())
    }
}

/**
 * New analytics section with our chart display implemented
 * @param completedOrders List, since we only use the completed orders to calculate profit
 * @author Ruben
 */

@Composable
fun OrderAnalytics(completedOrders: List<Order>, onDismiss: () -> Unit){

    //onDismiss to tuck this away and call it back with a button click

    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)){
        Card(
            modifier = Modifier.width(600.dp).height(500.dp).padding(16.dp),colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        )
        {
            Column(modifier = Modifier.fillMaxSize().padding(24.dp)){

                //Adding a header for the button title and the close button

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = " Order Analytics",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Button(onClick = onDismiss) {
                        Text("Close")
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                //Actual stats section

            }
        }

        Row(modifier = Modifier.fillMaxSize().padding(16.dp)){

            //Displaying the order stats on the left side

            Column(modifier = Modifier.weight(1f)){

                //Quick important stats

                val totalRevenue = completedOrders.sumOf { it.total }

                val totalOrders = completedOrders.size

                val avgOrder = if (totalOrders > 0) totalRevenue / totalOrders else 0.0

                StatCard("Completed Orders", totalOrders.toString())
                Spacer(modifier = Modifier.height(12.dp))
                StatCard("Total current revenue", "$${String.format("%.2f", totalRevenue)}")
                Spacer(modifier = Modifier.height(12.dp))
                StatCard("Average Order Value","$${String.format("%.2f", avgOrder)}" )

            }

            Spacer(modifier = Modifier.width(24.dp))

            //Displaying the chart to the right

            Column (modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally){
                chartDisplay(completedOrders)
            }

        }
    }
}

/**
 * Small stat card for analytics makes it very easy to just slap the data
 * in here and call it inside of the analytics section
 */
@Composable
fun StatCard(label: String, value: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = label,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = value,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}
