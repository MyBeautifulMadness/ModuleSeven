package com.example.photoeditor

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import android.graphics.Path

class DrawingView(context: Context) : View(context) {
    private val paint = Paint().apply {
        color = Color.BLACK
        strokeWidth = 10f
        style = Paint.Style.FILL }
    private val linepaint = Paint().apply {
        color = Color.BLACK
        strokeWidth = 5f
        style = Paint.Style.STROKE
    }
    private var pointsArr = mutableListOf<Point>()
    private var check=0
    fun drawAntialiasedLine(canvas: Canvas, startX: Float, startY: Float, endX: Float, endY: Float, paint: Paint) {
        // Получаем абсолютные значения смещений по осям X и Y
        val dx = Math.abs(endX - startX)
        val dy = Math.abs(endY - startY)
        var xst=startX
        var yst=startY
        var xend=endX
        var yend=endY

        val isSteep = dy > dx

        // Если смещение по Y больше, меняем оси местами
        if (isSteep) {
            val newPair = swap(Pair(xst, yst))
            xst = newPair.first
            yst = newPair.second
            val newEndPair = swap(Pair(xend, yend))
            xend = newPair.first
            yend = newPair.second
        }

        // Если направление по X - обратное, меняем начало и конец линии
        if (startX > endX) {
            val newPair = swap(Pair(xst, yst))
            xst = newPair.first
            yst = newPair.second
        }

        // Вычисляем смещения
        val xIncrement = 1f
        val yIncrement = if (isSteep) dx.toFloat() / dy else dy.toFloat() / dx

        // Вычисляем начальную точку по Y
        var y = yst
        if (isSteep) {
            y += if (yst < yend) 0.5f else -0.5f
        } else {
            y += if (yst > yend) 0.5f else -0.5f
        }

        // Инициализируем объект Path для рисования линии
        val path = Path()
        path.moveTo(xst, y)

        // Перебираем точки по оси X
        var x = xst
        while (x < xend) {
            // Определяем текущее смещение по Y
            val currentY = if (isSteep) x else y
            // Добавляем точку в Path
            path.lineTo(x, currentY)

            // Переходим к следующей точке по X
            x += xIncrement
            // Обновляем значение Y
            y += yIncrement
        }

        // Рисуем Path с помощью заданной кисти
        canvas.drawPath(path, paint)
    }

    // Функция для обмена значений двух переменных
    private fun swap(pair: Pair<Float, Float>): Pair<Float, Float> {
        return pair.second to pair.first
    }
    fun drawBezierCurve(canvas: Canvas, paint: Paint?, startX: Float, startY: Float, endX: Float, endY: Float, controlX: Float, controlY: Float) {
        // Шаг по оси X для отрисовки точек кривой
        val step = 0.01f

        // Начальная точка
        var x = startX
        var y = startY

        // Итерация по промежуточным точкам кривой
        var t = 0f
        while (t <= 1) {

            // Вычисление координат точки на кривой Безье по формуле
            val x1 =
                (1 - t) * (1 - t) * (1 - t) * startX + 3 * (1 - t) * (1 - t) * t * controlX + 3 * (1 - t) * t * t * controlX + t * t * t * endX
            val y1 =
                (1 - t) * (1 - t) * (1 - t) * startY + 3 * (1 - t) * (1 - t) * t * controlY + 3 * (1 - t) * t * t * controlY + t * t * t * endY

            // Рисуем линию от предыдущей точки к текущей
            canvas.drawLine(x, y, x1, y1, paint!!)

            // Обновляем координаты предыдущей точки
            x = x1
            y = y1
            t += step
        }

        // Рисуем последнюю точку
        canvas.drawPoint(endX, endY, paint!!)
    }
    private data class Point(val x: Float, val y: Float)
    fun change(){
        check=1
        invalidate()
    }
    fun toNormal()
    {
        check=0
    }
    fun turnAntiAliason()
    {
        check=2
    }
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        for (point in pointsArr) {
            canvas.drawCircle(point.x, point.y, 10f, paint)
        }

        for (i in 0 until pointsArr.size - 1) {
            // Соединяем каждую точку с следующей
            canvas.drawLine(pointsArr[i].x, pointsArr[i].y, pointsArr[i + 1].x, pointsArr[i + 1].y, paint)
        }
        if (check==1|| check ==2)
        {
            canvas.drawColor(Color.WHITE)
            for (point in pointsArr) {
                canvas.drawCircle(point.x, point.y, 10f, paint)
            }
            for (i in 0 until pointsArr.size - 1)
            {
                val controlPoint1 = Point((pointsArr[i].x + pointsArr[i+1].x) / 2, pointsArr[i].y)
                if (check==1){
                    drawBezierCurve(canvas, linepaint, pointsArr[i].x, pointsArr[i].y, pointsArr[i + 1].x, pointsArr[i + 1].y, controlPoint1.x, controlPoint1.y)
                }
                else {
                    drawAntialiasedLine(canvas, pointsArr[i].x, pointsArr[i].y, pointsArr[i + 1].x, pointsArr[i + 1].y, linepaint)//controlPoint1.x, controlPoint1.y)
                }
            }
        }
        super.onDraw(canvas)
        toNormal()
    }
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            pointsArr.add(Point(event.x, event.y))
            invalidate() // Перерисовываем view
        }
        return true
    }
}

class SecondaryActivity : AppCompatActivity() {
    private lateinit var drawingView: DrawingView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        val drawingContainer = findViewById<FrameLayout>(R.id.drawing_container)
        drawingView = DrawingView(this)
        drawingContainer.addView(drawingView)

        val btnDrawSpines = findViewById<Button>(R.id.btn_draw_splines)
        val btnDrawAnti = findViewById<Button>(R.id.btn_draw_anti)
        btnDrawSpines.setOnClickListener {
            drawingView.change()
            //drawingContainer.invalidate()
        }
        btnDrawAnti.setOnClickListener {
            drawingView.turnAntiAliason()
            drawingContainer.invalidate()
        }
    }
}

