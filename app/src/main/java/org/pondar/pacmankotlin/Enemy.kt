package org.pondar.pacmankotlin

import android.content.res.Resources
import kotlin.math.absoluteValue
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sqrt
import kotlin.random.Random

class Enemy (x : Int, y : Int, gameView: GameView) {

    var x = x
    var y = y
    private var gameView = gameView
    private var targetX : Int = 0
    private var targetY : Int = 0
    private var speed : Int = 5
    var isAlive : Boolean = true

    fun enemyMove(){
        val length = distance(x.toFloat(), targetX.toFloat(), y.toFloat(), targetY.toFloat())

        if(length == (0).toDouble()){
            newTarget()
            enemyMove()
            return
        }

        val xMove = ((targetX - x) / length).roundToInt()
        val yMove = ((targetY - y) /length).roundToInt()

        x += xMove * speed
        y+= yMove * speed

        if(distance(x.toFloat(), targetX.toFloat(), y.toFloat(), targetY.toFloat()) < speed){
            newTarget()
        }
    }

    fun newTarget(){
        targetX = Random.nextInt(0, gameView?.w)
        targetY = Random.nextInt(0, gameView?.h)
    }

    fun distance(x1 : Float, x2 : Float, y1 : Float, y2 : Float) : Double {
        return sqrt((x2 - x1).toDouble().pow(2) + (y2 - y1).toDouble().pow(2)).absoluteValue
    }

    fun setSpeed(speed : Int){
        this.speed = speed
    }

    fun setPos(x : Int, y : Int){
        this.x = x
        this.y = y
    }

}