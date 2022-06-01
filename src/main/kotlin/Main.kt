// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

const val START_ANGLE_RADIANS = 3 * Math.PI / 2
const val MINIMUM_MULTIPLIER = 2
const val MINIMUM_MODULUS = 9
const val MAXIMUM_RANDOM_MODULUS = 2000
const val MAXIMUM_RANDOM_MULTIPLIER = 2000
const val TINY_INC_DEC = 1
const val SMALL_INC_DEC = 10
const val MEDIUM_INC_DEC = 100
const val LARGE_INC_DEC = 1000
const val START_BUTTON_PADDING = 5
const val BUTTON_WIDTH = 80
const val RANDOM_BUTTON_WIDTH = 240
const val NUM_BUTTON_STATES = 3
const val BUTTON_HEIGHT = 35
const val FOOTER_BUTTON_TP = 5
const val HEADER_HEIGHT0 = 138
const val PAGE1_FRACTION = 0.89f
const val PAGE2_FRACTION = 0.92f
const val FOOTER_HEIGHT = BUTTON_HEIGHT + 5
const val STATE_BUTTON_BAR = 0
const val STATE_FULL_SCREEN = 1
const val STATE_SLIDE_SHOW = 2
const val CIRCLE_NUMBER_LABEL_TP = 15

val HEADER_BACKGROUND_COLOR1 = Color(244,244,36)
val HEADER_BACKGROUND_COLOR2 = Color(242,242, 135)
val TEXT_BACKGROUND_COLOR = Color.Transparent

fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "Circle Numbers") {
        app()
    }
}

@Composable
fun app() {
    val modulus = remember { mutableStateOf(MINIMUM_MODULUS) }
    val multiplier = remember { mutableStateOf(MINIMUM_MULTIPLIER) }
    val buttonState = remember { mutableStateOf(STATE_BUTTON_BAR) }
    val index = remember { mutableStateOf(0) }
    Column {
        when (buttonState.value) {
            STATE_BUTTON_BAR -> {
                Row(
                    modifier = Modifier.fillMaxWidth().height(HEADER_HEIGHT0.dp)
                        .padding(top = 10.dp)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    HEADER_BACKGROUND_COLOR1,
                                    HEADER_BACKGROUND_COLOR2
                                )
                            )
                        ),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    buttonGroup(label = "Multiplier: ${multiplier.value}", minimum = 2, category = multiplier)
                    buttonGroup(label = "Modulus: ${modulus.value}", minimum = 3, category = modulus)
                }
                drawCanvas(multiplier, modulus, buttonState.value + 1)
                footer(multiplier, modulus, buttonState, index, circleData.size)
            }
            STATE_FULL_SCREEN -> {
                drawCanvas(multiplier, modulus, buttonState.value + 1)
                footer(multiplier, modulus, buttonState, index, circleData.size)
            }
            STATE_SLIDE_SHOW -> {
                multiplier.value = circleData[index.value][0]
                modulus.value = circleData[index.value][1]
                drawCanvas(multiplier, modulus, STATE_SLIDE_SHOW + 1)
                footer(multiplier, modulus, buttonState, index, circleData.size)
            }
        }
    }
}

@Composable
fun buttonGroup(
    label: String,
    minimum: Int,
    buttonWidth: Dp = BUTTON_WIDTH.dp,
    category: MutableState<Int>
) {

    Column {
        Text(
            label, Modifier.align(Alignment.CenterHorizontally).background(TEXT_BACKGROUND_COLOR)
                .padding(START_BUTTON_PADDING.dp)
        )

        Row {
            for (i in 0..3) {
                Button(
                    onClick = { addClick(category, incDecSize[i]) },
                    colors = ButtonDefaults.buttonColors(Color.Gray),
                    modifier = Modifier.padding(START_BUTTON_PADDING.dp).width(buttonWidth)
                ) {
                    Text("+${incDecSize[i]}")
                }
            }
        }

        Row {
            for (i in 0..3) {
                Button(
                    onClick = { subtractClick(category, incDecSize[i], minimum) },
                    colors = ButtonDefaults.buttonColors(Color.Gray),
                    modifier = Modifier.padding(
                        START_BUTTON_PADDING.dp
                    ).width(buttonWidth)
                ) {
                    Text("-${incDecSize[i]}")
                }
            }
        }
    }
}

@Composable
fun drawCanvas(
    multiplier: MutableState<Int>,
    modulus: MutableState<Int>,
    canvasState: Int
) {

    Canvas(
        modifier = Modifier
            .background(Color.Black)
            .fillMaxHeight(getCanvasFraction(canvasState))
            .fillMaxWidth()
    ) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val centerX = canvasWidth / 2
        val centerY = canvasHeight / 2
        val circleRadius = canvasHeight / 2 - 24
        val segmentAngleRadians = (2 * Math.PI) / modulus.value

        drawCircle(
            center = Offset(centerX, centerY),
            color = Color(40, 150, 239),
            radius = circleRadius,
            style = Stroke(width = 1.0f)
        )

        var num1Loc: Offset
        var num2Loc: Offset
        var num2: Int
        var destinationAngle1: Double
        var destinationAngle2: Double

        for (i in 1..modulus.value) {
            num2 = i * multiplier.value % modulus.value
            destinationAngle1 = START_ANGLE_RADIANS + segmentAngleRadians * i
            destinationAngle2 = START_ANGLE_RADIANS + segmentAngleRadians * num2

            num1Loc = Offset(
                (centerX + circleRadius * cos(destinationAngle1)).toFloat(),
                (centerY + circleRadius * sin(destinationAngle1)).toFloat()
            )

            num2Loc = Offset(
                (centerX + circleRadius * cos(destinationAngle2)).toFloat(),
                (centerY + circleRadius * sin(destinationAngle2)).toFloat()
            )

            drawLine(
                start = num1Loc,
                end = num2Loc,
                color = Color(40, 150, 239)
            )
        }
    }
}

@Composable
fun footer(
    multiplier: MutableState<Int>, modulus: MutableState<Int>,
    buttonState: MutableState<Int>, iState: MutableState<Int>,
    numOfCircles: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth().height(FOOTER_HEIGHT.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Button(
            onClick = { buttonState.value = pageBefore(buttonState.value, iState) },
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.Gray),
            modifier = Modifier.padding(top = FOOTER_BUTTON_TP.dp).height(BUTTON_HEIGHT.dp),
            enabled = buttonState.value != STATE_BUTTON_BAR
        ) {
            Text("<")
        }

        if (buttonState.value == STATE_BUTTON_BAR) {
            randomButton(multiplier, modulus)
        } else {
            showParametersButton(buttonState)
        }

        Button(
            onClick = {
                buttonState.value = pageAfter(
                    buttonState.value, NUM_BUTTON_STATES, iState,
                    circleData.lastIndex
                )
            },
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.Gray),
            modifier = Modifier.padding(top = FOOTER_BUTTON_TP.dp).height(BUTTON_HEIGHT.dp)
        ) {
            Text(">")
        }

        if (buttonState.value == STATE_SLIDE_SHOW) {
            showCircleNumber(iState.value + 1, numOfCircles)
        }
    }
}

@Composable
fun showCircleNumber(circleIndex: Int, numCircles: Int) {
    if (numCircles == 0) return
    Text("Circle: $circleIndex / $numCircles", modifier = Modifier.padding(top = CIRCLE_NUMBER_LABEL_TP.dp))
}

@Composable
fun randomButton(
    multiplier: MutableState<Int>,
    modulus: MutableState<Int>,
    buttonWidth: Dp = RANDOM_BUTTON_WIDTH.dp,
    buttonHeight: Dp = BUTTON_HEIGHT.dp
) {
    Button(
        onClick = {
            multiplier.value = (Math.random() * MAXIMUM_RANDOM_MULTIPLIER).roundToInt()
            if (multiplier.value < MINIMUM_MULTIPLIER) {
                multiplier.value = MINIMUM_MULTIPLIER
            }

            modulus.value = (Math.random() * MAXIMUM_RANDOM_MODULUS).roundToInt()
            if (modulus.value < MINIMUM_MODULUS) {
                modulus.value = MINIMUM_MODULUS
            }
        },

        colors = ButtonDefaults.buttonColors(backgroundColor = Color.Gray),
        modifier = Modifier.padding(top = 5.dp)
            .width(buttonWidth).height(buttonHeight),
    ) {
        Text("Generate Random Circle")
    }
}

@Composable
fun showParametersButton(
    buttonState: MutableState<Int>,
    buttonWidth: Dp = RANDOM_BUTTON_WIDTH.dp,
    buttonHeight: Dp = BUTTON_HEIGHT.dp
) {
    Button(
        onClick = { buttonState.value = STATE_BUTTON_BAR },

        colors = ButtonDefaults.buttonColors(backgroundColor = Color.Gray),
        modifier = Modifier.padding(top = 5.dp)
            .width(buttonWidth).height(buttonHeight)
    ) {
        Text("Show Circle Parameters")
    }
}

fun subtract(number: Int, amount: Int, minimum: Int): Int {
    val temp = number - amount
    return if (temp < minimum) {
        minimum
    } else temp
}

fun getCanvasFraction(page: Int): Float {
    return when (page) {
        1 -> PAGE1_FRACTION
        else -> PAGE2_FRACTION
    }
}

fun pageBefore(x: Int, indexState: MutableState<Int>): Int {
    if (x == STATE_SLIDE_SHOW) {
        val i = indexState.value - 1
        return if (i < 0) {
            STATE_FULL_SCREEN
        } else {
            indexState.value = i
            STATE_SLIDE_SHOW
        }
    }
    return if (x - 1 < STATE_BUTTON_BAR) {
        STATE_BUTTON_BAR
    } else {
        x - 1
    }
}

fun pageAfter(x: Int, numStates: Int, iState: MutableState<Int>, maxi: Int): Int {
    if (x == STATE_SLIDE_SHOW) {
        val i = iState.value + 1
        if (i > maxi) {
            iState.value = 0
            return STATE_BUTTON_BAR
        } else {
            iState.value = i
        }
        return STATE_SLIDE_SHOW
    }
    return if (x + 1 >= numStates) STATE_BUTTON_BAR else {
        x + 1
    }
}

val addClick: (MutableState<Int>, Int) -> Unit = { c, inc -> c.value += inc }
val subtractClick: (MutableState<Int>, Int, Int) -> Unit = { c, inc, min -> c.value = subtract(c.value, inc, min) }
val incDecSize = listOf(TINY_INC_DEC, SMALL_INC_DEC, MEDIUM_INC_DEC, LARGE_INC_DEC)

val circleData = listOf(
    listOf(219, 60), listOf(714, 634), listOf(866, 944), listOf(1457, 397),
    listOf(1717, 728), listOf(947, 727), listOf(758, 454), listOf(1429, 1110), listOf(209, 243),
    listOf(337, 564), listOf(308, 404), listOf(375, 102), listOf(726, 659), listOf(124, 31)
)
