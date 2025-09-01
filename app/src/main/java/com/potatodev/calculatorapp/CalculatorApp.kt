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

val memBtnList = listOf( "MC", "MR", "M+", "M-", "MS", "M^")
val buttonList = listOf(
    "C", "CE", "", "⌫",
    "1/x", "x²", "√", "Ans",
    "(", ")", "%", "/",
    "7", "8", "9", "*",
    "4", "5", "6", "-",
    "1", "2", "3", "+",
    "+/-", "0", ".", "="
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
            modifier = modifier
                .fillMaxSize()
                .padding(8.dp), // optional padding
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = equationText.value ?: "",
                    style = TextStyle(
                        fontSize = 30.sp,
                        textAlign = TextAlign.End
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

                Text(
                    text = resultText.value ?: "0",
                    style = TextStyle(
                        fontSize = 60.sp,
                        textAlign = TextAlign.End
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(horizontal = 8.dp, vertical = 4.dp),
//                horizontalArrangement = Arrangement.SpaceBetween
//            ) {
//                listOf("🕒", "📏", "🔢", "🛠").forEach { label ->
//                    Text(
//                        text = label,
//                        fontSize = 14.sp,
//                        modifier = Modifier
//                            .clickable { /* TODO: implement later */ }
//                    )
//                }
//            }


            // Buttons Section (Anchored at Bottom)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp, 8.dp)
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.wrapContentHeight() // ✅ This makes height dynamic
                ) {
                    items(buttonList) {
                        CalculatorButton(btn = it, onClick = {
                            viewModel.onButtonClick(it)
                        })
                    }
                }
            }

        }

    }

}

@Composable
fun CalculatorButton(btn: String, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .height(60.dp) // or 64.dp, 72.dp — tweak to your preference
            .fillMaxWidth()
            .padding(4.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = getButtonColor(btn),
        shadowElevation = 1.dp
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = btn,
                fontSize = 20.sp,
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
        in listOf("=", "/", "*", "-", "+") -> Color(0xFF2196F3) // blue
        in listOf("MC", "MR", "M+", "M-", "MS", "M^") -> Color(0xFFBDBDBD) // gray
        "AC", "C", "CE", "⌫" -> Color(0xFFE53935) // red
        else -> Color(0xFFF5F5F5)
    }
}

fun getTextColor(label: String): Color {
    return when (label) {
        "=", "/", "*", "-", "+", "AC", "C", "CE", "⌫" -> Color.White
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
