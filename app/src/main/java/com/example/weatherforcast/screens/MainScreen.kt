package com.example.weatherforcast.screens


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.weatherforcast.R
import com.example.weatherforcast.data.WeatherModel

import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject


@Composable
fun MainCard(currentDay: MutableState<WeatherModel> , onClickSync: ()->Unit, onClickSearch: () -> Unit) {
    Column (
        Modifier.fillMaxWidth().padding(5.dp)
    ){
        Card (
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFADD8E6).copy(alpha = 0.5f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            shape = RoundedCornerShape(10.dp)
        ){
            Column(
                Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row (
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Text(
                        modifier = Modifier.padding(top = 30.dp),
                        text = currentDay.value.time,
                        style = TextStyle(fontSize = 15.sp),
                        color = Color.Black
                    )
                    AsyncImage(model = "https:"+ currentDay.value.icon ,
                        contentDescription = "img2",
                        modifier = Modifier.padding(35.dp)
                            .padding(top = 3.dp, end = 8.dp)
                            .size(35.dp))
                }

                Text(
                    text = currentDay.value.city,
                    style = TextStyle(fontSize = 24.sp),
                    color = Color.White
                )
                Row (
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ){
                    IconButton(
                        onClick = {
                            onClickSearch.invoke()
                        }
                    ){
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_search_24),
                            contentDescription = "Search icon",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Text(
                    text = currentDay.value.currentTemp,
                    style = TextStyle(fontSize = 65.sp),
                    color = Color.White
                )

                Text(
                    text = "${currentDay.value.maxTemp}C/${currentDay.value.minTemp}C",
                    style = TextStyle(fontSize = 16.sp),
                    color = Color.White
                )

                Row (
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ){
                    IconButton(
                        onClick={onClickSync.invoke()}
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_cloud_sync_24),
                            contentDescription = "im3",
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }
}



@Composable
fun TabLayout(daysList: MutableState<List<WeatherModel>>, currentDay: MutableState<WeatherModel>) {
    val tabList = listOf("HOURS", "DAYS")
    val pagerState = rememberPagerState(pageCount = { tabList.size })
    var tabIndex by remember { mutableStateOf(0) }
    val coroutineScope = rememberCoroutineScope()

    // Sync tabIndex with pagerState
    LaunchedEffect(pagerState.currentPage) {
        tabIndex = pagerState.currentPage
    }

    Column(
        modifier = Modifier
            .padding(start = 5.dp, end = 5.dp)
            .clip(RoundedCornerShape(5.dp))
    ) {
        TabRow(
            selectedTabIndex = tabIndex,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[tabIndex]),
                    color = Color.Blue
                )
            },
            containerColor = Color.White,
            contentColor = Color.Black
        ) {
            tabList.forEachIndexed { index, title ->
                Tab(
                    text = {
                        Text(
                            text = title,
                            color = if (tabIndex == index) Color.Blue else Color.Gray
                        )
                    },
                    selected = tabIndex == index,
                    onClick = {
                        tabIndex = index
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    }
                )
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth()
        ) { pageIndex ->
            if (pageIndex == 0) {
                // Hours
                val hoursList = getWeatherByHours(currentDay.value.hours)
                MainList(hoursList, currentDay)
            } else {
                // Days
                MainList(daysList.value, currentDay)
            }
        }

    }
}

@Composable
fun MainScreen(currentDay: MutableState<WeatherModel>, daysList: MutableState<List<WeatherModel>>) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        MainCard(currentDay, onClickSync = {}, onClickSearch = {})
        TabLayout(daysList, currentDay)
    }
}


@Preview(showBackground = true)
@Composable
fun CompleteWeatherScreen() {

    val sampleCurrentDay = remember {
        mutableStateOf(WeatherModel(
            "London",
            "10:00",
            "25°C",
            "Sunny",
            "https://cdn.weatherapi.com/weather/64x64/day/389.png",
            "26°C",
            "24°C",
            "Cloudy with outbreaks of rain"
        ))
    }
    val sampleDaysList = remember {
        mutableStateOf(listOf<WeatherModel>())
    }
    MainScreen(currentDay = sampleCurrentDay, daysList = sampleDaysList)
}


private fun getWeatherByHours(hours: String): List<WeatherModel> {
    if (hours.isEmpty()) return listOf()
    val hoursArray = JSONArray(hours)
    val list = ArrayList<WeatherModel>()
    for (i in 0 until hoursArray.length()) {
        val item = hoursArray.getJSONObject(i)
        list.add(
            WeatherModel(
                city = "",
                time = item.getString("time"),
                currentTemp = item.getString("temp_c"),
                condition = item.getJSONObject("condition").getString("text"),
                icon = item.getJSONObject("condition").getString("icon"),
                maxTemp = "",
                minTemp = "",
                hours = ""
            )
        )
    }
    return list
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