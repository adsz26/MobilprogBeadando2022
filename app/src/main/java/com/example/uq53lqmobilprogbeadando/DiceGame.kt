package com.example.uq53lqmobilprogbeadando

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.uq53lqmobilprogbeadando.databinding.ActivityDiceGameBinding
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.delay

class DiceGame : AppCompatActivity() {

    private lateinit var binding: ActivityDiceGameBinding
    private lateinit var players: List<Player>
    private var rolledNumber: Int = -1
    private val targetScore: Int = 60
    private lateinit var activePlayer: Player
    private var isSetTempPoint: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDiceGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.diceTvtargetscore.text = "Cél: ${targetScore} pont"

        val diceImages = mutableListOf(
            R.drawable.dice1, R.drawable.dice2,
            R.drawable.dice3, R.drawable.dice4,
            R.drawable.dice5, R.drawable.dice6
        )

        players = listOf(
            intent.getSerializableExtra("player1") as Player,
            intent.getSerializableExtra("player2") as Player
        )

        for(p in players) {
            p.points = 0
            p.tempPointsDiceGame = 0
        }

        binding.buttonRollDice.setOnClickListener {
            binding.buttonRollDice.isClickable = false
            binding.buttonSaveScore.isClickable = false
            rolledNumber = ((1..6)).random()
            binding.imageButton.startAnimation(AnimationUtils.loadAnimation(this, R.anim.rotate))
            val handler = Handler(Looper.getMainLooper())
            handler.postDelayed({
                // do something after 500ms
                binding.imageButton.setImageResource(diceImages[rolledNumber-1])
            }, 500)

            handler.postDelayed({
                // do something after 750ms
                updateModels(rolledNumber)
                updateView()
                binding.buttonRollDice.isClickable = true
                binding.buttonSaveScore.isClickable = true
            }, 750)
        }

        binding.buttonSaveScore.setOnClickListener {
            saveScore(getActivePlayer())
            changeActivePlayer()
            updateView()
        }

    }



    private fun updateModels (number: Int) {
        activePlayer = getActivePlayer()

        if(!isSetTempPoint) {
            activePlayer.tempPointsDiceGame = activePlayer.points
            isSetTempPoint = true
        }

        if(number == 6) {
            isSetTempPoint = false
            activePlayer.points = activePlayer.tempPointsDiceGame
            changeActivePlayer()
        } else {
            activePlayer.points += number
        }

        if (players.any { p -> p.points >= targetScore }) {
            gameOver()
        }
    }



    private fun updateView(){
        setActivePlayerView(activePlayer)
        setActivePlayerPointsView(activePlayer)
    }

    private fun setActivePlayerPointsView(p: Player) {
        if (p.id == 1) {
            binding.diceTvp1.text = ("${p.name}: ${p.points}")
            //Toast.makeText(this, "${p.name} - +${rolledNumber} = ${p.points}", Toast.LENGTH_LONG).show()
        } else if (p.id == 2) {
            binding.diceTvp2.text = "${p.name}: ${p.points}"
            //Toast.makeText(this, "${p.name} - +${rolledNumber} = ${p.points}", Toast.LENGTH_LONG).show()
        }
    }
    private fun setActivePlayerView(p: Player) {
        var active = getActivePlayer()

        if (active.id == 1) {
            binding.diceTvp1.setBackgroundResource(R.color.green)
            binding.diceTvp2.setBackgroundResource(R.color.gray)
        } else if (active.id == 2) {
            binding.diceTvp2.setBackgroundResource(R.color.green)
            binding.diceTvp1.setBackgroundResource(R.color.gray)
        }


    }

    private fun getActivePlayer(): Player {
        return players.first() { p -> p.isActivePlayer }
    }
    private fun changeActivePlayer() {
        val currentPlayer: Player = getActivePlayer()
        val otherPlayer: Player = players.first() { p -> !p.isActivePlayer}

        currentPlayer.isActivePlayer = false
        otherPlayer.isActivePlayer = true
    }


    private fun gameOver(){
        players.first { p -> p.points >= targetScore }.wins++
        myFunctionAlertDialogBuilder()
    }

    private fun myFunctionAlertDialogBuilder() {
        val builder = AlertDialog.Builder(this)
        builder
            .setTitle("Vége a kocka játéknak")
            .setMessage(winnerOfDiceGame() + "\n" + "${players[0].name} győzelmek: ${players[0].wins}\n${players[1].name} győzelmek: ${players[1].wins}" + "\n\n" + winnerOfAllGames())
            .setPositiveButton("OK") {dialog, which ->
                val intent = Intent(this, MainActivity::class.java)
                val b = Bundle()
                b.putSerializable("player1", players[0])
                b.putSerializable("player2", players[1])
                intent.putExtras(b)
                startActivity(intent)
                finish()
            }
            .setCancelable(false)
            .show()
    }

    private fun winnerOfDiceGame(): String {
        return "${players.first { p -> p.points >= targetScore }.name} nyert"
    }

    private fun winnerOfAllGames(): String {
        var msg: String = "JÁTÉK VÉGE - DÖNTETLEN"
        when {
            players[0].wins > players[1].wins -> return "JÁTÉK VÉGE - ${players[0].name} NYERT"
            players[0].wins < players[1].wins -> return "JÁTÉK VÉGE - ${players[1].name} NYERT"
        }
        return msg
    }

    private fun saveScore(p: Player) {
        p.tempPointsDiceGame = p.points
    }





}