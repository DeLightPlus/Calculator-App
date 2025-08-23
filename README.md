# 📱 Calculator App (Jetpack Compose)

A modern, minimal calculator app built using **Jetpack Compose**.  
Supports basic arithmetic operations with clean UI and real-time expression previewing.

---

## Features

- Basic operations: `+`, `-`, `×`, `÷`, `%`
- Handles chained operations and respects math flow (similar to BODMAS, left-to-right evaluation)
- Expression preview (e.g. shows `2 *` above `3`)
- Smart result formatting:
  - Removes `.0` for whole numbers (e.g., `5.0` ➝ `5`)
  - Rounds to 6 decimal places
  - Always uses `.` as the decimal separator
- Edge case handling:
  - Division by zero ➝ `"Error"`
  - Clear (`AC`), Backspace (`C`)
  - Sign toggle (`+/-`), Percent (`%`)
- Dynamic input behavior:
  - Continues expressions after result (`=`)
  - Clears current input when chaining operations

---

## 🧠 Architecture & Logic

- UI is built with **Jetpack Compose**, using `LazyVerticalGrid` for buttons layout.
- State management is handled using `remember { mutableStateOf(...) }`
- Core logic lives inside `onButtonClick(label: String)`
- Calculations and result formatting are handled separately for clarity and accuracy.

---

