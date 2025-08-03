package com.example.weatherforcast.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.weatherforcast.data.WeatherModel


@Composable
fun MainList(list:List<WeatherModel>, currentDays: MutableState<WeatherModel>){
    LazyColumn (
        Modifier.fillMaxSize()
    ){
        itemsIndexed(list) { _, item -> WeatherListItem(item) }

    }
}
@Composable
fun WeatherListItem(item: WeatherModel) {
    Card (
        modifier = Modifier.fillMaxWidth().padding(top = 3.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF4DD3FD).copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape = RoundedCornerShape(5.dp)
    ) {
        Row (
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(text = item.time , style = TextStyle(fontSize = 14.sp), color = Color.Black)

            Text(text = "${item.currentTemp} °C", style = TextStyle(fontSize = 16.sp), color = Color.White)


        }
    }
}

@Composable
fun DialogSearch(dialogState: MutableState<Boolean>, onSubmit: (String)->Unit){

    val dialogText = remember { mutableStateOf("") }
    AlertDialog(onDismissRequest = {
        dialogState.value = false

    },
        confirmButton = {
            TextButton(onClick = {
                onSubmit(dialogText.value)
                dialogState.value = false
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = {
                dialogState.value = false
            }) {
                Text("Cancel")
            }
        },
        title = {
            Column (
                Modifier.fillMaxWidth()
            ){
                Text("Enter the name of the city:")
                TextField(value = dialogText.value, onValueChange ={
                    dialogText.value = it
                } )
            }

        })
}
















