package org.pondar.pacmankotlin

import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    //reference to the game class.
    var game: Game? = null
    var timer : Timer = Timer()
    var gametimer : Timer = Timer()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setContentView(R.layout.activity_main)
        game = Game(this,pointsView, timerView)

        game?.setGameView(gameView)
        gameView.setGame(game)
        //game?.newGame()

        timer.schedule(object : TimerTask() {
            override fun run() {
                timerMethod()
            }

        }, 0, 40)

        gametimer.schedule(object : TimerTask() {
            override fun run() {
                gameTimerMethod()
            }

        }, 0, 1000)

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId
        if (id == R.id.action_settings) {
            Toast.makeText(this, "settings clicked", Toast.LENGTH_LONG).show()
            return true
        } else if (id == R.id.action_newGame) {
            Toast.makeText(this, "New Game clicked", Toast.LENGTH_LONG).show()
            game?.gameActive = 0
            game?.newGame()
            return true
        }
         else if (id == R.id.action_startGame) {
            //Toast.makeText(this, "Start game clicked", Toast.LENGTH_LONG).show()
            if(game?.gameActive == 0){
                game?.gameActive = 1
                game?.running = true
            }
            return true
        }
        else if (id == R.id.action_stopGame) {
            //Toast.makeText(this, "Stop game clicked", Toast.LENGTH_LONG).show()
            game?.Win(false)
            game?.running = false
            return true
        }
        else if(id == R.id.action_continue){
            if(game?.gameActive == 1){
                game?.running = true
            }
            return true
        }
        else if(id == R.id.action_pause){
            if(game?.gameActive == 1){
                game?.running = false
            }
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    fun onClickMovement(view : View){

        when (view.id){
            R.id.moveRight -> game?.pacDir = 0
            R.id.moveLeft -> game?.pacDir = 1
            R.id.moveUp -> game?.pacDir = 2
            R.id.moveDown -> game?.pacDir = 3
        }

    }

    private fun timerMethod() {
        this.runOnUiThread(game?.timerTick)

    }

    private fun gameTimerMethod(){
        this.runOnUiThread(game?.gameTimerTick)
    }





}
