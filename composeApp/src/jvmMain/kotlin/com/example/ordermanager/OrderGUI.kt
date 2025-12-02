package com.example.ordermanager

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jfree.chart.title.Title

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
        }

        // Three columns
        Row(modifier = Modifier.fillMaxSize()) {
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


        }

    }
}
