package org.pondar.pacmankotlin

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.absoluteValue
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sqrt
import kotlin.random.Random


/**
 *
 * This class should contain all your game logic
 */

class Game(private var context: Context,view: TextView, timerView: TextView) {

        var gameActive : Int = 0 //0 = not started, 1 = started, 2 = dead
        var gameSetup : Boolean = false
        var running = false
        var updatingLevel = false
        private var counter : Int = 0
        private var gameTimeLeft = 60
        private var pointsView: TextView = view
        private var timerView: TextView = timerView
        private var points : Int = 0
        //bitmap of the pacman
        var pacBitmap: Bitmap
        var pacx: Int = 0
        var pacy: Int = 0
        var pacSpeed : Int = 10
        var pacDir : Int = 0

        //did we initialize the coins?
        var coinsInitialized = false

        var enemyBitmap: Bitmap
        var coinBitmap: Bitmap
        //the list of goldcoins - initially empty
        var coins = ArrayList<GoldCoin>()
        var enemies = ArrayList<Enemy>()
        var enemySpawnPosX = IntArray(2)
        var enemySpawnPosY = IntArray(2)

        //a reference to the gameview
        private var gameView: GameView? = null
        private var h: Int = 0
        private var w: Int = 0 //height and width of screen

        private var level = 1

    init {
        pacBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.pacman)
        coinBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.coin)
        enemyBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.ghost)

        pacBitmap = Bitmap.createScaledBitmap(pacBitmap, 100,100, true)
        coinBitmap = Bitmap.createScaledBitmap(coinBitmap, 50,50, true)
        enemyBitmap = Bitmap.createScaledBitmap(enemyBitmap, 75,75, true)

    }

    fun setGameView(view: GameView) {
        this.gameView = view
    }

    //TODO initialize goldcoins also here
    fun initializeGoldcoins()
    {
        coinsInitialized = true
    }


    fun newGame() {
        pacx = (w * 0.5f - pacBitmap.width).roundToInt()
        pacy = (h * 0.5f - pacBitmap.height).roundToInt() //just some starting coordinates - you can change this.

        counter = 0
        running = false
        gameTimeLeft = 60
        coinsInitialized = false
        points = 0
        level = 1
        pointsView.text = "P: $points  "
        timerView.text = "T: $gameTimeLeft"

        enemySpawnPosX[0] = 0
        enemySpawnPosX[1] = w - enemyBitmap.width
        enemySpawnPosY[0] = 0
        enemySpawnPosY[1] = h -enemyBitmap.height

        Log.wtf("game", enemySpawnPosX[1].toString())
        Log.wtf("game", enemySpawnPosY[1].toString())

        coins.clear()
        for(i in 1..10){
            coins.add(GoldCoin(Random.nextInt(30, w - 30), Random.nextInt(30, h - 30)))
        }

        enemies.clear()

        val enemyXPos = Random.nextInt(0,2)
        val enemyYPos = Random.nextInt(0,2)

        enemies.add(Enemy(enemySpawnPosX[enemyXPos], enemySpawnPosY[enemyYPos], gameView!!))
        gameView?.invalidate() //redraw screen
    }

    fun newLevel(){
        level++

        Toast.makeText(gameView?.context, "Level $level", Toast.LENGTH_SHORT).show()

        pacx = (w * 0.5f - pacBitmap.width).roundToInt()
        pacy = (h * 0.5f - pacBitmap.height).roundToInt()

        enemies.add(Enemy(0,0, gameView!!))
        for(e in enemies){
            e.isAlive = true
            val enemyXPos = Random.nextInt(0,2)
            val enemyYPos = Random.nextInt(0,2)
            e.setPos(enemySpawnPosX[enemyXPos],enemySpawnPosY[enemyYPos])
        }
        for(c in coins){
            c.taken = false
            c.x = Random.nextInt(30, w - 30)
            c.y = Random.nextInt(30, h - 30)
        }

        for(i in 1..(level * 3)){
            coins.add(GoldCoin(Random.nextInt(30, w - 30),
                    Random.nextInt(30, h - 30)))
        }
        gameTimeLeft = 60
        running = true
        updatingLevel = false
        gameView?.invalidate()

    }

    fun setSize(h: Int, w: Int) {
        this.h = h
        this.w = w
        if(!gameSetup){
            newGame()
            gameSetup = true
        }
    }

    fun movePacMan(x:Int, y: Int){
        pacx += x * pacSpeed
        pacy += y * pacSpeed

        if (pacx + pacBitmap.width > w) { pacx = w - pacBitmap.width }
        if (pacx < 0) { pacx = 0 }
        if (pacy + pacBitmap.height > h) { pacy = h - pacBitmap.height }
        if (pacy < 0) { pacy = 0}

        doCollisionCheck()
        gameView!!.invalidate()
    }

    fun distance(x1 : Float, x2 : Float, y1 : Float, y2 : Float) : Double {
        return sqrt((x2 - x1).toDouble().pow(2) + (y2 - y1).toDouble().pow(2)).absoluteValue
    }

    //TODO check if the pacman touches a gold coin
    //and if yes, then update the neccesseary data
    //for the gold coins and the points
    //so you need to go through the arraylist of goldcoins and
    //check each of them for a collision with the pacman
    fun doCollisionCheck() {

        for(c in coins){
            if(!c.taken){
                if(distance(pacx + pacBitmap.width * 0.5f, c.x + coinBitmap.width * 0.5f ,
                                pacy + pacBitmap.height * 0.5f,c.y + coinBitmap.height *0.5f) < 50){
                    c.taken = true
                    points += 1
                    pointsView.text = "P: $points  "

                    CheckWin()
                }
            }
        }
        for(e in enemies){
            if(e.isAlive){
                if(distance(pacx + pacBitmap.width * 0.5f, e.x +enemyBitmap.width*0.5f,
                        pacy + pacBitmap.height * 0.5f, e.y + enemyBitmap.height *0.5f) < 50){
                    Win(false)
                }
            }
        }
    }


    fun CheckWin(){
        var coinsLeft = 0
        for(c in coins){
            if(!c.taken){
                coinsLeft++
            }
        }

        if(coinsLeft == 0){
            Win(true)
        }

        if(gameTimeLeft <= 0){
            Win(false)
        }
    }

    fun Win(won : Boolean){
        running = false
        if(won){
            updatingLevel = true
        }
        if(!won){
            gameActive = 2
            Toast.makeText(gameView?.context, "Game Over. Click on New Game to play again", Toast.LENGTH_LONG).show()
        }

    }

    val gameTimerTick = Runnable {
        if(running){
            gameTimeLeft--
            timerView.text = "T: $gameTimeLeft"
        }

    }

    val timerTick = Runnable {
        //This method runs in the same thread as the UI.
        // so we can draw
        if (running) {
            counter++
            //update the counter - notice this is NOT seconds in this example
            //you need TWO counters - one for the timer count down that will
            // run every second and one for the pacman which need to run
            //faster than every second
            //textView.text = getString(R.string.timerValue,counter)
            for(e in enemies){
                e.enemyMove()
            }

            when (pacDir){
                0 -> movePacMan(1, 0)
                1 -> movePacMan(-1, 0)
                2 -> movePacMan(0, -1)
                3 -> movePacMan(0, 1)
            }

            if(updatingLevel){
                newLevel()
            }
        }
    }
}