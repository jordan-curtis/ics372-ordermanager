package com.example.ordermanager


import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ordermanager.composeapp.generated.resources.Res
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import androidx.compose.ui.graphics.toComposeImageBitmap

import java.io.File

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
    fun chartDisplay(){

        var chartImage by remember { mutableStateOf<ImageBitmap?>(null) }

        //Generating chart when app loads

        Column(modifier = Modifier.padding(16.dp)) {

            Button(onClick = {

                val calculator = Calculator()
                val chartFile = calculator.chartTest()

                //Png to bitmaP

                val chartBytes = chartFile.readBytes()
                chartImage = org.jetbrains.skia.Image.makeFromEncoded(chartBytes).toComposeImageBitmap()
            }){
                Text("Generate Profit Chart")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if(chartImage != null){
            Image(bitmap = chartImage!!, contentDescription = "Profit Stats", modifier = Modifier.fillMaxWidth())
        }


    }

    @Composable
    fun horizontalLayout(){
        //Creating placeholders for UI guidance

        Text("Left")

        Text("Middle")

        Text("Right")
    }


    @Composable
    fun quickDemo() {


            // Testing chart making functions - Tommy
        val c: Calculator = Calculator();
        c.chartTest();

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
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Analytics",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    chartDisplay()
                }


            }

        }

    }


