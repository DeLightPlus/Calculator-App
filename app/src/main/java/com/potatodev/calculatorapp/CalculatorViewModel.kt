package com.potatodev.calculatorapp

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.mozilla.javascript.Context
import org.mozilla.javascript.Scriptable

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

        val currentInput = _resultText.value ?: "0"
        val equation = _equationText.value ?: ""

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

            // === CLEAR ALL ===
            "C" -> {
                _equationText.value = ""
                _resultText.value = "0"
            }

            // === CLEAR CURRENT INPUT ONLY ===
            "CE" -> {
                _resultText.value = "0"
            }

            // === DELETE LAST DIGIT ===
            "⌫" -> {
                if (lastEvaluated) {
                    _equationText.value = ""
                    _resultText.value = lastAnswer
                    lastEvaluated = false
                    return
                }
                _resultText.value = if (currentInput.length > 1) {
                    currentInput.dropLast(1)
                } else {
                    "0"
                }
            }

            // === ANSWER ===
            "Ans" -> {
                if (lastEvaluated) _equationText.value = ""
                _resultText.value = lastAnswer
                lastEvaluated = false
            }

            // === EQUALS ===
            "=" -> {
                try {
                    val fullEquation = equation + currentInput
                    val result = calculateResult(fullEquation)
                    _resultText.value = result
                    _equationText.value = fullEquation // <-- Show full expression in equation
                    lastAnswer = result
                    lastEvaluated = true
                } catch (e: Exception) {
                    _resultText.value = "Error"
                    _equationText.value = ""
                }
            }


            // === OPERATOR ===
            "+", "-", "*", "/", "%" -> {
                if (lastEvaluated) {
                    _equationText.value = lastAnswer + btn
                    lastEvaluated = false
                } else {
                    _equationText.value = equation + currentInput + btn
                }
                _resultText.value = "0"
            }

            // === PARENTHESES ===
            "(", ")" -> {
                if (lastEvaluated) {
                    _equationText.value = ""
                    _resultText.value = "0"
                    lastEvaluated = false
                }
                _equationText.value = equation + btn
            }

            // === SPECIAL FUNCTIONS ===
            "x²" -> {
                val num = currentInput.toDoubleOrNull()
                num?.let {
                    val squared = it * it
                    _resultText.value = cleanResult(squared.toString())
                }
            }

            "√" -> {
                val num = currentInput.toDoubleOrNull()
                num?.let {
                    val root = kotlin.math.sqrt(it)
                    _resultText.value = cleanResult(root.toString())
                }
            }

            "1/x" -> {
                val num = currentInput.toDoubleOrNull()
                num?.let {
                    if (it != 0.0) {
                        _resultText.value = cleanResult((1 / it).toString())
                    } else {
                        _resultText.value = "∞"
                    }
                }
            }

            "+/-" -> {
                _resultText.value = if (currentInput.startsWith("-")) {
                    currentInput.drop(1)
                } else {
                    "-$currentInput"
                }
            }

            // === DECIMAL OR DIGIT ===
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
                    _resultText.value = if (currentInput == "0") btn else currentInput + btn
                }
            }

            // === FALLBACK ===
            else -> {
                Log.w("Unhandled Button", btn)
            }
        }
    }

    fun calculateResult(equation: String): String {
        val context: Context = Context.enter()
        context.optimizationLevel = -1
        val scriptable: Scriptable = context.initStandardObjects()
        var finalResult = context.evaluateString(scriptable, equation, "Javascript", 1, null).toString()
        Context.exit()

        return cleanResult(finalResult)
    }

    private fun cleanResult(result: String): String {
        return try {
            val double = result.toDouble()
            if (double % 1 == 0.0) {
                double.toInt().toString()
            } else {
                "%.6f".format(double).trimEnd('0').trimEnd('.')
            }
        } catch (e: NumberFormatException) {
            result
        }
    }
}
