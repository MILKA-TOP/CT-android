package com.example.app_calculator_hw1

import android.content.ClipData
import android.content.ClipboardManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import android.widget.Button
import android.widget.TextView
import expression.MathMode.CheckedDouble
import expression.exceptions.ExpressionParser
import java.lang.Exception
import java.lang.StringBuilder
import java.util.*

import android.content.Context
import android.widget.Toast
import expression.TripleGenExpression
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity(), OnClickListener {

    companion object {
        const val ERROR = "Error"
        const val INFINITY_PLUS = "Infinity"
        const val INFINITY_MINUS = "-Infinity"
        const val NAN = "NaN"
        const val BRACKET_LEVEL = "BRACKET_LEVEL"
        const val EXPRESSION = "EXPRESSION"
        const val COULD_BE_DOT = "COULD_BE_DOT"
        const val STACK = "STACK"
        const val MESSAGE_COPY = "Выражение скопировано"
        const val MESSAGE_MAX_LENGTH = "Максимальное число символов в выражении"

    }

    private lateinit var previousType: ButtonsType
    private var goodButtonType = mapOf(
        ButtonsType.NUMBER to listOf(
            ButtonsType.NUMBER,
            ButtonsType.OPERATION,
            ButtonsType.DOT,
            ButtonsType.RIGHT_BRACKET,
            ButtonsType.MINUS
        ),
        ButtonsType.OPERATION to listOf(
            ButtonsType.NUMBER, ButtonsType.LEFT_BRACKET, ButtonsType.OPERATION, ButtonsType.MINUS
        ),

        ButtonsType.DOT to listOf(ButtonsType.NUMBER),

        ButtonsType.MINUS to listOf(
            ButtonsType.MINUS, ButtonsType.NUMBER, ButtonsType.LEFT_BRACKET
        ),

        ButtonsType.NULL to listOf(ButtonsType.NUMBER, ButtonsType.LEFT_BRACKET, ButtonsType.MINUS),

        ButtonsType.LEFT_BRACKET to listOf(
            ButtonsType.NUMBER, ButtonsType.LEFT_BRACKET, ButtonsType.MINUS
        ),

        ButtonsType.RIGHT_BRACKET to listOf(ButtonsType.OPERATION, ButtonsType.RIGHT_BRACKET),

        ButtonsType.ERROR to listOf(
            ButtonsType.NUMBER, ButtonsType.LEFT_BRACKET, ButtonsType.MINUS
        ),
        ButtonsType.ANSWER to listOf(
            ButtonsType.OPERATION, ButtonsType.MINUS
        )
    )

    private val textToButtonType = mapOf(
        "-" to ButtonsType.MINUS,
        "(" to ButtonsType.LEFT_BRACKET,
        ")" to ButtonsType.RIGHT_BRACKET,
        "C" to ButtonsType.CLEAR,
        "DEL" to ButtonsType.CLEAR_ONE,
        "=" to ButtonsType.RESULT,
        "." to ButtonsType.DOT
    )

    private val isOperations = listOf("+", "×", "÷")
    private val errorExpressions = listOf(ERROR, INFINITY_PLUS, INFINITY_MINUS, NAN)

    private var buttonTypeStack = Stack<ButtonsType>()
    private var couldBeDot = true
    private var bracketLevel = 0

    private val maxLengthExpression = 92
    private val mode = CheckedDouble()
    private val zero = mode.getFromNumber(0)
    private var expressionToTextView = String()
    private val parserNow = ExpressionParser<Double>()

    private lateinit var textPanel: TextView

    private val makeDotTrue =
        listOf(ButtonsType.OPERATION, ButtonsType.MINUS, ButtonsType.RIGHT_BRACKET)
    private val endOfExpression = listOf(ButtonsType.RIGHT_BRACKET, ButtonsType.NUMBER)
    private var expressionLine = StringBuilder()


    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(BRACKET_LEVEL, bracketLevel)
        outState.putString(EXPRESSION, expressionLine.toString())
        outState.putBoolean(COULD_BE_DOT, couldBeDot)
        outState.putParcelableArrayList(STACK, ArrayList(buttonTypeStack))
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        bracketLevel = savedInstanceState.getInt(BRACKET_LEVEL)
        val expressionLineString = savedInstanceState.getString(EXPRESSION)
        expressionLine = StringBuilder(expressionLineString.toString())
        couldBeDot = savedInstanceState.getBoolean(COULD_BE_DOT)
        val arrayListButtonType = savedInstanceState.getParcelableArrayList<ButtonsType>(STACK)
        for (element in arrayListButtonType!!) {
            buttonTypeStack.push(element)
        }
        previousType = if (buttonTypeStack.isEmpty()) ButtonsType.NULL else buttonTypeStack.peek()
        textPanel.text = expressionLine
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        previousType = ButtonsType.NULL
        textPanel = findViewById(R.id.text_panel)

        copyExpression()

    }

    private fun copyExpression() {
        textPanel.setOnLongClickListener {
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText(EXPRESSION, textPanel.text)
            clipboard.setPrimaryClip(clip)
            toastShowMessage(MESSAGE_COPY)
            true
        }
    }

    private fun toastShowMessage(text: String) {
        val toast = Toast.makeText(applicationContext, text, Toast.LENGTH_SHORT)
        toast.show()
    }

    override fun onClick(view: View) {

        val nowButton = findViewById<Button>(view.id)
        val nowButtonType = getButtonType(nowButton)

        if (nowButtonType is ButtonsType) {
            /*
            * Очистка выражения
            * */
            if (nowButtonType == ButtonsType.CLEAR) {

                clearTextPanel()
                return

                /*
                * Вывод результата
                * */
            } else if (nowButtonType == ButtonsType.RESULT) {

                if (getResultBoolean()) {
                    expressionToTextView = try {
                        val expressionParsing =
                            parserNow.parse(expressionLine.toString(), mode)
                        val result = evaluating(expressionParsing)
                        if (someFunction()) return
                        getStringResult(result)
                    } catch (e: Exception) {
                        ERROR
                    }
                    textPanel.text = expressionToTextView
                    val outTextType =
                        if (expressionToTextView in errorExpressions) ButtonsType.ERROR else ButtonsType.ANSWER
                    clear(expressionToTextView, false, outTextType)
                    buttonTypeStack.push(outTextType)
                    return
                }


                /*
                * Удаление последнего символа
                * */
            } else if (nowButtonType == ButtonsType.CLEAR_ONE && !buttonTypeStack.isEmpty()) {
                val deletedSymbol = buttonTypeStack.pop()

                if (buttonTypeStack.empty()) clearTextPanel()

                correctReplace(deletedSymbol)
                previousType =
                    if (buttonTypeStack.isEmpty()) ButtonsType.NULL else buttonTypeStack.peek()
                if (expressionLine.isNotEmpty()) expressionLine.setLength(expressionLine.length - 1)

                /*
                * Конкатенация выражения и символа кнопки
                * */
            } else if (goodButtonType[previousType]?.contains(nowButtonType) == true) {

                if (specialExceptions(nowButtonType)) return

                correctButtonAdd(nowButton, nowButtonType)
                correctBracketLevel(nowButtonType)
                if (nowButtonType in makeDotTrue) {
                    couldBeDot = true
                } else if (nowButtonType == ButtonsType.DOT) {
                    couldBeDot = false
                }
            }
            textPanel.text = expressionLine
        }

    }

    private fun clearTextPanel() {
        clear()
        textPanel.text = expressionLine
    }

    private fun getButtonType(nowButton: Button?): ButtonsType? {
        if (nowButton == null) return ButtonsType.NULL
        val text = nowButton.text

        if (text[0].isDigit()) return ButtonsType.NUMBER

        if (text in isOperations) return ButtonsType.OPERATION

        return textToButtonType[text]
    }

    private fun getStringResult(result: Double): String {
        val resultString: String = if (isWhole(result)) {
            (result.toInt()).toString()
        } else {
            result.toString()
        }
        return resultString
    }

    private fun getResultBoolean(): Boolean = previousType in endOfExpression && bracketLevel == 0


    private fun evaluating(expressionParsing: TripleGenExpression<Double>) =
        expressionParsing.evaluate(zero, zero, zero, CheckedDouble())

    private fun tryChangeOperation(nowType: ButtonsType, button: Button): Boolean {
        if (nowType == previousType && nowType == ButtonsType.OPERATION) {
            expressionLine.setLength(expressionLine.length - 1)
            expressionLine.append(button.text)
            return true
        }
        return false
    }

    private fun correctReplace(deletedSymbol: ButtonsType) {
        when (deletedSymbol) {
            ButtonsType.LEFT_BRACKET -> bracketLevel--
            ButtonsType.RIGHT_BRACKET -> bracketLevel++
            ButtonsType.DOT -> couldBeDot = true
            else -> return
        }
    }

    private fun correctBracketLevel(nowType: ButtonsType) {
        if (nowType == ButtonsType.LEFT_BRACKET) bracketLevel++
        else if (nowType == ButtonsType.RIGHT_BRACKET) bracketLevel--
    }

    private fun specialExceptions(nowType: ButtonsType): Boolean {
        return dotException(nowType) || rightBracketException(nowType)
    }

    private fun dotException(nowType: ButtonsType): Boolean =
        nowType == ButtonsType.DOT && !couldBeDot

    private fun rightBracketException(nowType: ButtonsType): Boolean =
        bracketLevel <= 0 && nowType == ButtonsType.RIGHT_BRACKET


    private fun clear(
        text: String = "",
        dot: Boolean = true,
        type: ButtonsType = ButtonsType.NULL
    ) {
        expressionLine = StringBuilder(text)
        bracketLevel = 0
        couldBeDot = dot
        previousType = type
        buttonTypeStack.clear()
    }

    private fun correctButtonAdd(nowButton: Button, nowType: ButtonsType) {
        if (tryChangeOperation(nowType, nowButton) || checkExpressionLength()) return
        if (expressionLine.toString() in errorExpressions) clear()
        expressionLine.append(nowButton.text)
        previousType = nowType
        buttonTypeStack.push(nowType)
    }

    private fun checkExpressionLength(): Boolean {
        if (expressionLine.length >= maxLengthExpression) {
            toastShowMessage(MESSAGE_MAX_LENGTH)
            return true
        }
        return false
    }

    private fun isWhole(value: Double): Boolean {
        return value - value.toInt() == 0.0
    }

    private fun someFunction(): Boolean {
        if (expressionLine.toString() == "1000-7") {
            clear()
            textPanel.text = "DEAD INSIDE"
            buttonTypeStack.push(ButtonsType.ERROR)
            return true
        }
        return false
    }

}
