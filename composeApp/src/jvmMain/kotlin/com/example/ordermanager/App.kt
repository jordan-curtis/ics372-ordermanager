package com.example.ordermanager


import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {

    quickDemo()

}


    /**
     * Creating UI composable components
     * @author Ruben
     */

    @Composable
    fun horizontalLayout(){
        //Creating placeholders for UI guidance

        Text("Left")

        Text("Middle")

        Text("Right")
    }


    @Composable

    fun quickDemo() {
        val sampleOrder = remember {
            Order(
                "Dine-in", mutableListOf(
                    Item("Burger", 8.99, 1),
                    Item("Fries", 3.99, 2),
                    Item("Milkshake", 5.99, 1)
                )
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            Text(
                text = "Order Management Demo",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Order #${sampleOrder.orderId}", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text("Type: ${sampleOrder.orderType}")
                    Text("Status: ${sampleOrder.status}")

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Items:", fontWeight = FontWeight.Bold)
                    sampleOrder.items.forEach { item ->
                        Text("  • ${item.name} - $${item.price} x ${item.quantity}")
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Total: $${"%.2f".format(sampleOrder.total)}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
