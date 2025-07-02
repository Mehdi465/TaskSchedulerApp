package com.example.taskscheduler.ui.HelperDialog

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalDensity

@Composable
fun InfiniteTimePickerWheel(
    themeColor: Color,
    initialHour: Int,
    initialMinute: Int,
    onTimeSelected: (hour: Int, minute: Int) -> Unit
) {
    var selectedHour by remember { mutableStateOf(initialHour) }
    var selectedMinute by remember { mutableStateOf(initialMinute) }

    val hours = (0..23).map { it.toString().padStart(2, '0') }
    val minutes = (0..59).map { it.toString().padStart(2, '0') }

    val wheelHeight = 32.dp * 3 // must match wheel column height

    Row(
        modifier = Modifier
            .background(Color(0xFF2C2C2C), RoundedCornerShape(16.dp))
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        LoopingTimeWheelColumn(
            items = hours,
            onItemSelected = {
                selectedHour = it
                onTimeSelected(selectedHour, selectedMinute)
            },
            themeColor = themeColor
        )

        Box(
            modifier = Modifier
                .height(wheelHeight)
                .width(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = ":",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = themeColor
            )
        }

        LoopingTimeWheelColumn(
            items = minutes,
            onItemSelected = {
                selectedMinute = it
                onTimeSelected(selectedHour, selectedMinute)
            },
            themeColor = themeColor
        )
    }
}

@Composable
fun LoopingTimeWheelColumn(
    items: List<String>,
    onItemSelected: (Int) -> Unit,
    themeColor: Color,
    visibleItems: Int = 3,
    itemHeight: Dp = 32.dp
) {
    val infiniteItems = List(1000) { index -> items[index % items.size] }
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = 500)

    val density = LocalDensity.current

    val centerIndex by remember {
        derivedStateOf {
            val index = listState.firstVisibleItemIndex
            val offset = listState.firstVisibleItemScrollOffset

            // Convert itemHeight to px inside a density scope
            val pxPerItem = with(density) { itemHeight.toPx() }

            if (offset < pxPerItem / 2) index else index + 1
        }
    }

    val selectedValue by remember {
        derivedStateOf {
            centerIndex % items.size
        }
    }

    LaunchedEffect(centerIndex) {
        onItemSelected(selectedValue)
    }

    Box(
        modifier = Modifier
            .height(itemHeight * visibleItems)
            .width(72.dp),
        contentAlignment = Alignment.Center
    ) {
        LazyColumn(
            state = listState,
            contentPadding = PaddingValues(vertical = itemHeight * (visibleItems / 2)),
            horizontalAlignment = Alignment.CenterHorizontally,
            flingBehavior = rememberSnapFlingBehavior(lazyListState = listState)
        ) {
            itemsIndexed(infiniteItems) { index, item ->
                val isSelected = index == centerIndex
                Text(
                    text = item,
                    fontSize = if (isSelected) 36.sp else 24.sp,
                    color = if (isSelected) themeColor else Color.Gray,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    modifier = Modifier
                        .height(itemHeight)
                        .wrapContentHeight()
                )
            }
        }
    }
}


