package com.example.testgame2

import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.core.graphics.withScale
import kotlin.math.pow
import kotlin.math.sqrt


class GameView(context: Context): View(context) {
    private val screenHeight = 1017f
    private val screenWidth =  2184f
    private val fields = Array(20) { Array(12){ RectF() } }
    private var chosenX: Float
    private var chosenY: Float
    private val widthSize = 19
    private val heightSize = 12
    private val radius = 20f
    private var ball = Pair(500f,500f)
    private var footballer1 = RectF(10f+7*84, 5f+4*84,94f+7*84, 89f+4*84)
    private var footballer2 = RectF(10f+11*84, 5f+7*84,94f+11*84, 89f+7*84)
    private var footballer3 = RectF(10f+11*84, 5f+4*84,94f+11*84, 89f+4*84)
    private var footballer4 = RectF(10f+7*84, 5f+7*84,94f+7*84, 89f+7*84)
    private var shootButton = RectF(1700f, 850f,2100f, 950f)
    private var activePlayer = 1
    private var ballPosession = 1
    private var shootResult = ""
    private val paint: Paint = Paint()
    private val paintPlayer1: Paint
    private val paintPlayer2: Paint
    private val paintPlayer3: Paint
    private val paintPlayer4: Paint
    private val paintBall: Paint
    private val paintButton: Paint
    private val playerPaints: MutableList<Paint>
    private val footballers = mutableListOf(footballer1, footballer2, footballer3, footballer4)
    private var teamTokens = mutableListOf(mutableListOf(0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1),mutableListOf(0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1))
    init {
        paint.color = Color.BLACK
        paint.strokeWidth = 1f
        paint.textSize = 40f
        paint.style = Paint.Style.STROKE

        paintButton = Paint()
        paintButton.color = Color.BLACK
        paintButton.textSize = 50f

        paintBall = Paint()
        paintBall.color = Color.BLACK

        paintPlayer1 = Paint()
        paintPlayer1.color = Color.parseColor("#2bf0cf")
        paintPlayer1.strokeWidth = 1f
        paintPlayer1.textSize = 40f
        paintPlayer1.style = Paint.Style.FILL

        paintPlayer2 = Paint()
        paintPlayer2.color = Color.parseColor("#faf746")
        paintPlayer2.strokeWidth = 1f
        paintPlayer2.textSize = 40f

        paintPlayer3 = Paint()
        paintPlayer3.color = Color.parseColor("#fa3232")
        paintPlayer3.strokeWidth = 1f
        paintPlayer3.textSize = 40f

        paintPlayer4 = Paint()
        paintPlayer4.color = Color.parseColor("#ae2bf0")
        paintPlayer4.strokeWidth = 1f
        paintPlayer4.textSize = 40f

        chosenX = 0f
        chosenY = 0f
        //footballer1 = Pair(0f,0f)
        playerPaints = mutableListOf(paintPlayer1, paintPlayer2, paintPlayer3, paintPlayer4)
    }

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)
        var rect: RectF
        canvas?.drawColor(Color.parseColor("#d2ff85"))
        canvas?.drawRect(shootButton, paintButton)
        for ((i,footballer) in footballers.withIndex()) {
            //canvas?.drawRect(footballer.left + 10,footballer.top - 25,footballer.right - 40,footballer.bottom + 25, playerPaints[i])
            canvas?.drawRect(footballer, playerPaints[i])
        }
        canvas?.drawCircle(ball.first, ball.second, radius, paintBall)
        //val bitmap = BitmapFactory.decodeResource(resources, R.drawable.stick)
        //canvas?.drawBitmap(bitmap, footballer1.first, footballer1.second, paintPlayer1)

        for (w in 0 until widthSize)
            for (h in 0 until heightSize) {
                rect = RectF(10f+w*84, 5f+h*84,94f+w*84, 89f+h*84)
                canvas?.drawRect(rect, paint)
                fields[w][h] = rect
            }

        canvas?.drawText(shootResult, 1800f, 750f, paintButton)
        if (activePlayer == 0)
            canvas?.drawText("Replace Ball", 1800f, 50f, paint)
        else
            canvas?.drawText("Player $activePlayer turn", 1800f, 50f, playerPaints[activePlayer-1])
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        chosenX = event!!.x
        chosenY = event.y
        var ignore = false

        if(activePlayer==0)
            ignore = true

        if (contains(shootButton, chosenX, chosenY))
            shoot()

        for (footballer in footballers) {
            if (contains(footballer, chosenX, chosenY))
                activePlayer = footballers.indexOf(footballer) + 1
        }

        if (containsBall(ball,chosenX, chosenY))
            activePlayer = 0

        for (w in 0 until widthSize)
            for (h in 0 until heightSize) {
                if (contains(fields[w][h],chosenX, chosenY))
                {
                    if(activePlayer==0)
                        ball = center(fields[w][h])
                    else
                        footballers[activePlayer-1] = fields[w][h]

                    if (ignore) {
                        ball = center(fields[w][h])
                        ballPosession = activePlayer
                    }
                    if (activePlayer == ballPosession)
                        ball = center(fields[w][h])
                    invalidate()
                }
            }
        return super.onTouchEvent(event)
    }

    private fun shoot(){
        teamTokens = mutableListOf(mutableListOf(0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1),mutableListOf(0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1))
        var successes = 0
        for(i in 0..0) {
            var team = 1

            if (ballPosession == 1 || ballPosession == 4) {
                team = 0
                teamTokens[team].add(1)
                teamTokens[team].add(1)
                teamTokens[team].add(1)
                teamTokens[team].add(2)
            }
            teamTokens[team].add(1)

            val attacker = drawTokens(3, team)
            val defender = drawTokens(2, team)
            successes += calculateResult(attacker, defender)
        }
        shootResult = successes.toString()
        invalidate()
    }

    private fun drawTokens(number: Int, team: Int): MutableList<Int> {
        teamTokens[team].shuffle()
        val randomTokens = mutableListOf<Int>()
        for (n in 1..number) {
            if(teamTokens[team].size==0){
                teamTokens[team].addAll(mutableListOf(0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1))
            }

            randomTokens.add(teamTokens[team][0])
            teamTokens[team].removeAt(0)
        }
        return randomTokens
    }

    private fun calculateResult(a: MutableList<Int>, d: MutableList<Int>): Int {
        var goals = a.count {it == 1}
        var defenses = d.count {it == 0}
        var misses = d.count {it == 2} + a.count {it == 2}
        if (misses>0 || defenses >= goals)
            return 0
        return 1
    }

    private fun contains(rect: RectF, x: Float, y: Float): Boolean {
        if(x>rect.left && x<rect.right && y>rect.top && y<rect.bottom)
            return true
        return false
    }

    private fun containsBall(ball: Pair<Float,Float>,x: Float, y: Float): Boolean {
        val d = sqrt((ball.first-x).pow(2)+(ball.second-y).pow(2))
        if (d < radius)
            return true
        return false
    }

    private fun center(rect: RectF): Pair<Float, Float> {
        return Pair((rect.left+rect.right)/2,(rect.top+rect.bottom)/2)
    }
}