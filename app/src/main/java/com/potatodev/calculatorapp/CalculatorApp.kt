package com.potatodev.calculatorapp

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*

import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items

import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


// Buttons
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
    var operand1 by remember { mutableStateOf("") }
    var operand2 by remember { mutableStateOf("") }
    var operator by remember { mutableStateOf<String?>(null) }

    fun clear() {
        display = "0"
        operand1 = ""
        operand2 = ""
        operator = null
    }

    fun calculate() {
        val num1 = operand1.toDoubleOrNull()
        val num2 = operand2.toDoubleOrNull()
        if (num1 != null && num2 != null && operator != null) {
            val result = when (operator) {
                "+" -> num1 + num2
                "-" -> num1 - num2
                "*" -> num1 * num2
                "/" -> if (num2 != 0.0) num1 / num2 else null
                else -> null
            }
            display = result?.toString() ?: "Error"
            operand1 = result?.toString() ?: ""
            operand2 = ""
            operator = null
        }
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
            "=" -> if (operand1.isNotEmpty() && operand2.isNotEmpty()) calculate()
            "+", "-", "*", "/" -> if (operand1.isNotEmpty()) operator = label
            "+/-" -> {
                if (operator == null && operand1.isNotEmpty()) {
                    operand1 = if (operand1.startsWith("-")) operand1.drop(1) else "-$operand1"
                    display = operand1
                } else if (operator != null && operand2.isNotEmpty()) {
                    operand2 = if (operand2.startsWith("-")) operand2.drop(1) else "-$operand2"
                    display = operand2
                }
            }
            "." -> {
                if (operator == null && !operand1.contains(".")) {
                    operand1 += "."
                    display = operand1
                } else if (operator != null && !operand2.contains(".")) {
                    operand2 += "."
                    display = operand2
                }
            }
            "%" -> {
                if (operator == null && operand1.isNotEmpty()) {
                    val result = operand1.toDoubleOrNull()?.div(100)
                    if (result != null) {
                        operand1 = result.toString()
                        display = operand1
                    }
                } else if (operand2.isNotEmpty()) {
                    val result = operand2.toDoubleOrNull()?.div(100)
                    if (result != null) {
                        operand2 = result.toString()
                        display = operand2
                    }
                }
            }
            else -> {
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Display
        Text(
            text = display,
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.End,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp)
        )

        Spacer(modifier = Modifier.weight(1f)) // Push the buttons down

        // Buttons Grid in fixed-height Box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 360.dp) // Optional: adjust this to your desired height
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
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .background(color = getColor(label))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 24.sp,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}

fun getColor(label: String): Color {
    return when (label) {
        "AC", "C" -> Color(0xFFE53935) // Red
        "+", "-", "*", "/", "=", "%" -> Color(0xFFF57C00) // Orange
        else -> Color(0xFF009688) // Teal
    }
}
