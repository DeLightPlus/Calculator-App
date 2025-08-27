package com.potatodev.calculatorapp

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider

import androidx.compose.material3.Surface
import androidx.compose.material3.Text

import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

val buttons = listOf(
    "AC", "C", "%", "/",
    "7", "8", "9", "*",
    "4", "5", "6", "-",
    "1", "2", "3", "+",
    "+/-", "0", ".", "=",
)

val buttonList = listOf(
    "C", "(", ")" , "/",
    "7", "8" , "9", "*",
    "4", "5" , "6", "+",
    "1", "2" , "3", "-",
    "AC", "0" , ".", "="
)

@Composable
fun CalculatorApp(
    modifier: Modifier = Modifier,
    viewModel: CalculatorViewModel
) {
    val equationText = viewModel.equationText.observeAsState()
    val resultText = viewModel.resultText.observeAsState()
    //hosea primary school

    Box(modifier = modifier){
        Column(
            modifier = modifier.fillMaxSize(),
            horizontalAlignment = Alignment.End
        ){
            Text(
                text = equationText.value?:"",
                style = TextStyle(
                    fontSize = 30.sp,
                    textAlign= TextAlign.End
                ),
                maxLines = 5,
                overflow = TextOverflow.Ellipsis
            )

            Divider(
                color = Color.LightGray,
                thickness = 1.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
            )
            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = resultText.value?:"0",
                style = TextStyle(
                    fontSize = 60.sp,
                    textAlign= TextAlign.End
                ),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(10.dp))

            LazyVerticalGrid(columns = GridCells.Fixed(4),
            ) {
                items(buttonList){
                    CalculatorButton(btn = it, onClick = {
                        viewModel.onButtonClick(it)
                    })
                }
            }

        }
    }

}

@Composable
fun CalculatorButton(btn: String, onClick: () -> Unit) {
    Surface (
        modifier = Modifier
            .aspectRatio(1f)
            .padding(4.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = getButtonColor(btn),
        shadowElevation = 4.dp
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = btn,
                fontSize = 24.sp,
                fontWeight = FontWeight.Medium,
                color = getTextColor(btn)
            )
        }
    }
}

fun getColor(label: String): Color {
    return when (label) {
        "AC", "C" -> Color(0xFFE53935)
        "+", "-", "*", "/", "=", "%" -> Color(0xFFF57C00)
        else -> Color(0xFF009688)
    }
}

fun getButtonColor(label: String): Color {
    return when (label) {
        "AC", "C" -> Color(0xFFE57373) // red-ish
        "+", "-", "*", "/", "=", "%" -> Color(0xFF4CAF50) // green accent
        else -> Color(0xFFEEEEEE) // light gray for numbers
    }
}

fun getTextColor(label: String): Color {
    return when (label) {
        "AC", "C", "+", "-", "*", "/", "=", "%" -> Color.White
        else -> Color.Black
    }
}

fun cleanResult(result: String): String {
    val number = result.toDoubleOrNull() ?: return result

    val symbols = DecimalFormatSymbols(Locale.US).apply {
        decimalSeparator = '.'
    }

    val df = DecimalFormat("#.######", symbols)
    df.roundingMode = RoundingMode.HALF_UP

    return df.format(number)
}
