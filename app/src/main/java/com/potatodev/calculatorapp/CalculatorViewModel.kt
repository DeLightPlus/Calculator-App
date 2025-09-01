package com.potatodev.calculatorapp

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.mozilla.javascript.Context
import org.mozilla.javascript.Scriptable
import kotlin.math.sqrt

class CalculatorViewModel : ViewModel() {

    private val _equationText = MutableLiveData("")
    val equationText: LiveData<String> = _equationText

    private val _resultText = MutableLiveData("0")
    val resultText: LiveData<String> = _resultText

    private var memoryValue: Double = 0.0
    private var lastAnswer: String = "0"
    private var lastEvaluated = false

    fun onButtonClick(btn: String) {
        Log.i("Clicked", btn)

        var currentInput = _resultText.value ?: "0"
        var equation = _equationText.value ?: ""

        when (btn) {
            // === MEMORY FUNCTIONS ===
            "MC", "MR", "M+", "M-", "MS" -> {
                val result = currentInput.toDoubleOrNull() ?: 0.0
                when (btn) {
                    "MC" -> memoryValue = 0.0
                    "MR" -> _resultText.value = cleanResult(memoryValue.toString())
                    "M+" -> memoryValue += result
                    "M-" -> memoryValue -= result
                    "MS" -> memoryValue = result
                }
            }

            "C" -> {
                _equationText.value = ""
                _resultText.value = "0"
                lastEvaluated = false
            }

            "CE" -> _resultText.value = "0"

            "⌫" -> {
                if (lastEvaluated) {
                    _equationText.value = ""
                    _resultText.value = lastAnswer
                    lastEvaluated = false
                    return
                }
                _resultText.value = if (currentInput.length > 1) {
                    currentInput.dropLast(1)
                } else "0"
            }

            "Ans" -> {
                if (lastEvaluated) _equationText.value = ""
                _resultText.value = lastAnswer
                lastEvaluated = false
            }

            "=" -> {
                try {
                    // Append current input if it's not "0" and not already ending with ")"
                    if (currentInput != "0" && !equation.endsWith(")")) {
                        equation += currentInput
                    }

                    // Balance parentheses
                    val open = equation.count { it == '(' }
                    val close = equation.count { it == ')' }
                    repeat(open - close) { equation += ")" }

                    val result = calculateResult(equation)
                    _resultText.value = result
                    _equationText.value = equation
                    lastAnswer = result
                    lastEvaluated = true
                } catch (e: Exception) {
                    _resultText.value = "Error"
                    _equationText.value = ""
                }
            }

            "+", "-", "*", "/", "%" -> {
                if (lastEvaluated) {
                    equation = lastAnswer
                    lastEvaluated = false
                }

                // Append current input if it's not "0" and not already ending with ")"
                if (currentInput != "0" && !equation.endsWith(")")) {
                    equation += currentInput
                }

                // Replace last operator if the equation ends with an operator
                while (equation.isNotEmpty() && equation.last() in "+-*/%") {
                    equation = equation.dropLast(1)
                }

                equation += btn
                _equationText.value = equation
                _resultText.value = "0"
            }

            "(" -> {
                if (lastEvaluated) {
                    equation = ""
                    _resultText.value = "0"
                    lastEvaluated = false
                }

                // First, flush current input if it's not "0"
                if (currentInput != "0") {
                    equation += currentInput
                    _resultText.value = "0"
                }

                // Auto-insert '*' before '(' if needed
                val last = equation.lastOrNull()
                if (last != null && (last.isDigit() || last == ')')) {
                    equation += "*("
                } else {
                    equation += "("
                }

                _equationText.value = equation
            }

            ")" -> {
                val open = equation.count { it == '(' }
                val close = equation.count { it == ')' }

                if (open > close) {
                    // Append current input if it's not "0" and equation doesn't end with ")"
                    if (currentInput != "0" && !equation.endsWith(")")) {
                        equation += currentInput
                    }

                    equation += ")"
                    _equationText.value = equation

                    // Only try to evaluate if we have a complete balanced expression
                    try {
                        val testOpen = equation.count { it == '(' }
                        val testClose = equation.count { it == ')' }

                        if (testOpen == testClose) {
                            val result = calculateResult(equation)
                            _resultText.value = result
                        } else {
                            _resultText.value = "0"
                        }
                    } catch (e: Exception) {
                        _resultText.value = "0" // Don't show error for incomplete expressions
                    }

                    lastEvaluated = false
                }
            }

            "x²" -> {
                currentInput.toDoubleOrNull()?.let {
                    val squared = it * it
                    _resultText.value = cleanResult(squared.toString())
                }
            }

            "√" -> {
                currentInput.toDoubleOrNull()?.let {
                    _resultText.value = cleanResult(sqrt(it).toString())
                }
            }

            "1/x" -> {
                currentInput.toDoubleOrNull()?.let {
                    _resultText.value = if (it != 0.0)
                        cleanResult((1 / it).toString()) else "∞"
                }
            }

            "+/-" -> {
                _resultText.value = if (currentInput.startsWith("-"))
                    currentInput.drop(1) else "-$currentInput"
            }

            "." -> {
                if (!currentInput.contains(".")) {
                    _resultText.value = currentInput + "."
                }
            }

            in "0".."9" -> {
                if (lastEvaluated) {
                    _equationText.value = ""
                    _resultText.value = btn
                    lastEvaluated = false
                } else {
                    // Auto-insert '*' if equation ends with ')'
                    if (equation.endsWith(")")) {
                        equation += "*"
                        _equationText.value = equation
                        _resultText.value = btn
                    } else {
                        _resultText.value = if (currentInput == "0") btn else currentInput + btn
                    }
                }
            }

            else -> {
                Log.w("Unhandled Button", btn)
            }
        }
    }

    private fun calculateResult(equation: String): String {
        val context = Context.enter()
        context.optimizationLevel = -1
        val scope: Scriptable = context.initStandardObjects()
        val result = context.evaluateString(scope, equation, "JavaScript", 1, null).toString()
        Context.exit()
        return cleanResult(result)
    }

    private fun cleanResult(raw: String): String {
        return raw.toDoubleOrNull()?.let {
            if (it % 1 == 0.0) it.toInt().toString()
            else "%.6f".format(it).trimEnd('0').trimEnd('.')
        } ?: raw
    }
}