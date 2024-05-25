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

class DrawingView(context: Context) : View(context) {
    private val paint = Paint().apply {
        color = Color.BLACK
        isAntiAlias = true
        strokeWidth = 10f
        style = Paint.Style.FILL }
    private val linepaint = Paint().apply {
        color = Color.BLACK
        strokeWidth = 5f
        style = Paint.Style.STROKE
        isAntiAlias = true
    }
    private val splinepaint = Paint().apply {
        color = Color.GREEN
        strokeWidth = 5f
        style = Paint.Style.FILL
        isAntiAlias = true
    }
    private var pointsArr = mutableListOf<Point>()
    private var splineArr = mutableListOf<Point>()
    private var check=0

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
    private data class Point(var x: Float, var y: Float)
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
            if (check==2)
            {
                linepaint.isAntiAlias=true
            }
            for (i in 0 until pointsArr.size - 1)
            {
                val controlPoint = Point((pointsArr[i].x + pointsArr[i+1].x) / 2, pointsArr[i].y)
                if(splineArr.size<i+1){
                    splineArr.add(Point(controlPoint.x, controlPoint.y))
                }
                for (i in 0 until splineArr.size)
                {
                    drawBezierCurve(canvas, linepaint, pointsArr[i].x, pointsArr[i].y, pointsArr[i + 1].x, pointsArr[i + 1].y, splineArr[i].x, splineArr[i].y)
                }
            }
            for (spline in splineArr) {
                canvas.drawCircle(spline.x, spline.y, 10f, splinepaint)
            }
        }
        super.onDraw(canvas)
    }
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if(check==1)
        {
            when (event.action) {
                MotionEvent.ACTION_MOVE -> {
                    var selectedPoint = findPoint(event.x, event.y)
                    if (selectedPoint != -1) {
                        // Перемещаем выбранную точку
                        pointsArr[selectedPoint].x = event.x
                        pointsArr[selectedPoint].y = event.y
                        invalidate() // Перерисовываем холст
                    }
                    else {
                        var selectedsplinePoint = findsplinePoint(event.x, event.y)
                        if (selectedsplinePoint != -1) {
                            // Перемещаем выбранную точку
                            splineArr[selectedsplinePoint].x = event.x
                            splineArr[selectedsplinePoint].y = event.y
                            invalidate() // Перерисовываем холст
                        }
                    }
                }
            }
        }
        else if(check==2 && event.action == MotionEvent.ACTION_DOWN)
        {
            var selectedPoint = findPoint(event.x, event.y)
            if (selectedPoint != -1) {
                pointsArr.removeAt(selectedPoint)
                if(selectedPoint<splineArr.size)
                {
                    splineArr.removeAt(selectedPoint)
                }
                else if (splineArr.size!=0){splineArr.removeAt(selectedPoint-1)}
                invalidate()
            }
            else {
                var selectedsplinePoint = findsplinePoint(event.x, event.y)
                if (selectedsplinePoint != -1) {
                    splineArr[selectedsplinePoint]= Point(pointsArr[selectedsplinePoint].x, pointsArr[selectedsplinePoint].y)
                }
                invalidate()
            }
        }
        else if (event.action == MotionEvent.ACTION_DOWN) {
            pointsArr.add(Point(event.x, event.y))
            invalidate() // Перерисовываем view
        }
        return true
    }
    fun findsplinePoint(x: Float, y: Float): Int {
        // Ищем точку, ближайшую к нажатию
        for (i in 0 until splineArr.size) {
            if (distance(splineArr[i].x, splineArr[i].y, x, y) <= 20f) {
                return i // Возвращаем найденную точку
            }
        }
        return -1 // Точка не найдена
    }
    fun findPoint(x: Float, y: Float): Int {
        // Ищем точку, ближайшую к нажатию
        for (i in 0 until pointsArr.size) {
            if (distance(pointsArr[i].x, pointsArr[i].y, x, y) <= 20f) {
                return i // Возвращаем найденную точку
            }
        }
        return -1 // Точка не найдена
    }
    fun distance(x1: Float, y1: Float, x2: Float, y2: Float): Float {
        return Math.sqrt(((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1)).toDouble()).toFloat()
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

        val drawSpines = findViewById<Button>(R.id.draw_splines)
        val delPoints = findViewById<Button>(R.id.delete_points)
        val tolines = findViewById<Button>(R.id.to_lines)
        drawSpines.setOnClickListener {
            drawingView.change()
            //drawingContainer.invalidate()
        }
        delPoints.setOnClickListener {
            drawingView.turnAntiAliason()
            drawingContainer.invalidate()
        }
        tolines.setOnClickListener {
            drawingView.toNormal()
            drawingContainer.invalidate()
        }
    }
}

