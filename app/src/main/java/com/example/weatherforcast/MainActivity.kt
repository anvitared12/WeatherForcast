package com.example.weatherforcast

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.weatherforcast.data.WeatherModel
import com.example.weatherforcast.screens.CompleteWeatherScreen
import com.example.weatherforcast.screens.DialogSearch
import com.example.weatherforcast.screens.MainCard
import com.example.weatherforcast.screens.MainScreen
import com.example.weatherforcast.screens.TabLayout
import com.example.weatherforcast.ui.theme.WeatherForcastTheme
import org.json.JSONObject



const val API_KEY = "9e98992a7e4d47a7b6871152253107"


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            WeatherForcastTheme {
                val daysList = remember {
                    mutableStateOf(listOf<WeatherModel>())
                }

                val dialogState = remember {
                    mutableStateOf(false)
                }

                val currentDay = remember {
                    mutableStateOf(WeatherModel(
                        "",
                        "",
                        "0.0 °C",
                        "",
                        "",
                        "0.0 °C",
                        "0.0 °C",
                        ""
                    ))
                }

                if (dialogState.value){
                    DialogSearch(dialogState, onSubmit = {
                        getData(it, this, daysList, currentDay)
                    }
                    )
                }
                getData("London",this, daysList, currentDay)
                Image(
                    painter = painterResource(id = R.drawable.back),
                    contentDescription = "img1",
                    modifier = Modifier.fillMaxSize().alpha(0.5f),
                    contentScale = ContentScale.FillBounds
                )
                Column {
                    MainCard(currentDay, onClickSync = {
                        getData("London",this@MainActivity,daysList,currentDay)
                    }, onClickSearch = {
                        dialogState.value = true

                    })
                    TabLayout(daysList,currentDay)
                }



            }
        }
    }
}

private fun getData(city: String, context: Context,
                    daysList: MutableState<List<WeatherModel>>,
                    currentDay: MutableState<WeatherModel>){
    val url = "https://api.weatherapi.com/v1/forecast.json?key=" +
            "9e98992a7e4d47a7b6871152253107" +
            "&q=$city" +
            "&days=" +
            "3" +
            "&aqi=no&alerts=no"

    val queue = Volley.newRequestQueue(context)
    val sRequest = StringRequest(
        Request.Method.GET,
        url,
        {
            response ->
            val list = getWeatherByDays(response)
            currentDay.value = list[0]
            daysList.value = list

        },
        {
            Log.d("MyLog","Volley error: $it")
        }
    )
    queue.add(sRequest)
}

private fun getWeatherByDays(response: String): List<WeatherModel> {
    if (response.isEmpty()) return listOf()
    val list = ArrayList<WeatherModel>()
    val mainObject = JSONObject(response)
    val city = mainObject.getJSONObject("location").getString("name")
    val days = mainObject.getJSONObject("forecast").getJSONArray("forecastday")
    for (i in 0 until days.length()) {
        val item = days.getJSONObject(i)
        val dayObj = item.getJSONObject("day")
        val conditionObj = dayObj.getJSONObject("condition")
        list.add(
            WeatherModel(
                city = city,
                time = item.getString("date"),
                currentTemp = "", // Will update for current day after loop
                condition = conditionObj.getString("text"),
                icon = conditionObj.getString("icon"),
                maxTemp = dayObj.getString("maxtemp_c"),
                minTemp = dayObj.getString("mintemp_c"),
                hours = item.getJSONArray("hour").toString()
            )
        )
    }
    if (list.isNotEmpty()) {
        val current = mainObject.getJSONObject("current")
        list[0] = list[0].copy(
            time = current.getString("last_updated"),
            currentTemp = current.getString("temp_c")
        )
    }
    return list
}



