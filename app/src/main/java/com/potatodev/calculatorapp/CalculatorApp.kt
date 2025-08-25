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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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

@Composable
fun CalculatorApp() {
    var display by remember { mutableStateOf("0") }
    var previousExpression by remember { mutableStateOf("") }
    var operand1 by remember { mutableStateOf("") }
    var operand2 by remember { mutableStateOf("") }
    var operator by remember { mutableStateOf<String?>(null) }
    var resultDisplayed by remember { mutableStateOf(false) }

    fun clear() {
        display = "0"
        previousExpression = ""
        operand1 = ""
        operand2 = ""
        operator = null
        resultDisplayed = false
    }

    fun calculate(): String? {
        val num1 = operand1.toDoubleOrNull()
        val num2 = operand2.toDoubleOrNull()
        return if (num1 != null && num2 != null && operator != null) {
            when (operator) {
                "+" -> (num1 + num2).toString()
                "-" -> (num1 - num2).toString()
                "*" -> (num1 * num2).toString()
                "/" -> if (num2 != 0.0) (num1 / num2).toString() else "Error"
                else -> null
            }
        } else null
    }

    fun onButtonClick(label: String) {
        when (label) {
            "AC" -> clear()

            "C", "->" -> {
                if (operator == null) {
                    operand1 = operand1.dropLast(1)
                    display = operand1.ifEmpty { "0" }
                } else {
                    operand2 = operand2.dropLast(1)
                    display = operand2.ifEmpty { "0" }
                }
            }

            "=" -> {
                val result = calculate()
                if (result != null) {
                    val cleaned = cleanResult(result)
                    previousExpression = "$operand1 $operator $operand2 ="
                    display = cleaned
                    operand1 = cleaned
                    operand2 = ""
                    operator = null
                    resultDisplayed = true
                }
            }

            "+", "-", "*", "/" -> {
                if (resultDisplayed) {
                    // Continue using the displayed result as operand1
                    resultDisplayed = false
                } else if (operand1.isNotEmpty() && operator != null && operand2.isNotEmpty()) {
                    val result = calculate()
                    if (result != null) {
                        val cleaned = cleanResult(result)
                        operand1 = cleaned
                        operand2 = ""
                        display = ""
                        previousExpression = "$cleaned $label"
                    }
                }
                operator = label
                previousExpression = "$operand1 $operator"
            }

            "+/-" -> {
                if (operator == null && operand1.isNotEmpty()) {
                    operand1 = if (operand1.startsWith("-")) operand1.drop(1) else "-$operand1"
                    display = operand1
                } else if (operand2.isNotEmpty()) {
                    operand2 = if (operand2.startsWith("-")) operand2.drop(1) else "-$operand2"
                    display = operand2
                }
            }

            "." -> {
                if (resultDisplayed) {
                    clear()
                }
                if (operator == null && !operand1.contains(".")) {
                    operand1 += "."
                    display = operand1
                } else if (operator != null && !operand2.contains(".")) {
                    operand2 += "."
                    display = operand2
                }
                resultDisplayed = false
            }

            "%" -> {
                if (operator == null && operand1.isNotEmpty()) {
                    val result = operand1.toDoubleOrNull()?.div(100)
                    result?.let {
                        operand1 = cleanResult(it.toString())
                        display = operand1
                    }
                } else if (operand2.isNotEmpty()) {
                    val result = operand2.toDoubleOrNull()?.div(100)
                    result?.let {
                        operand2 = cleanResult(it.toString())
                        display = operand2
                    }
                }
            }

            else -> { // Digit or input
                if (resultDisplayed) {
                    // Start new calculation
                    operand1 = label
                    operand2 = ""
                    operator = null
                    previousExpression = ""
                    display = operand1
                    resultDisplayed = false
                } else {
                    if (operator == null) {
                        operand1 += label
                        display = operand1
                    } else {
                        operand2 += label
                        display = operand2
                    }
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.End
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .border(1.dp, Color.LightGray, RoundedCornerShape(12.dp))
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = previousExpression,
                    fontSize = 22.sp,
                    color = Color.Gray
                )

                Divider(
                    color = Color.LightGray,
                    thickness = 1.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                )

                Text(
                    text = display,
                    fontSize = 52.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
        }


        Spacer(modifier = Modifier.weight(1f))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 360.dp)
                .padding(8.dp, 16.dp)
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(buttons) { label ->
                    CalculatorButton(label) { onButtonClick(label) }
                }
            }
        }
    }
}

@Composable
fun CalculatorButton(label: String, onClick: () -> Unit) {
    Surface (
        modifier = Modifier
            .aspectRatio(1f)
            .padding(4.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = getButtonColor(label),
        shadowElevation = 4.dp
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = label,
                fontSize = 24.sp,
                fontWeight = FontWeight.Medium,
                color = getTextColor(label)
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
