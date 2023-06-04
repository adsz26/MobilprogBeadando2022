package com.example.uq53lqmobilprogbeadando

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AccelerateInterpolator
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.view.animation.AnimationUtils
import android.view.animation.DecelerateInterpolator
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.uq53lqmobilprogbeadando.databinding.ActivityMemoryGameBinding

class MemoryGame : AppCompatActivity() {

    // bind layouts and UI components to data sources declaratively
    private lateinit var binding: ActivityMemoryGameBinding

    // lists for store multiple imageButtons and cards
    private lateinit var buttons: List<ImageButton>
    private lateinit var cards: List<MemoryCard>
    // helper variable for the setOnClickListener to store previous clicked card index
    private var indexOfSelectedCard: Int? = null
    // set players id and name
    var players: List<Player> = listOf(Player(1,"P1"), Player(2,"P2"))
    // helper variable for the delay
    private var selectedCards: Int = 1


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMemoryGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // initialize players attributes if you play more than one times while the app running
        for (p in players) {
            p.points = 0
            p.wins = 0
            p.tempPointsDiceGame = 0
            p.isActivePlayer = false
        }

        // mutableListOf for the cards
        val images = mutableListOf(
            R.drawable.p1, R.drawable.p2,
            R.drawable.p3, R.drawable.p4,
            R.drawable.p5, R.drawable.p6,
            R.drawable.p7, R.drawable.p8
        )

        // set P1 for the active player
        players[0].isActivePlayer = true
        binding.memoryTvp1.text = players[0].name + ": " + players[0].points
        binding.memoryTvp2.text = players[1].name + ": " + players[1].points


        // adds the list items again
        images.addAll(images)
        // shuffles the list to random order
        images.shuffle()

        // stores the IDs of the image buttons in a list
        buttons = listOf(
            binding.imageButton1, binding.imageButton2, binding.imageButton3,
            binding.imageButton4, binding.imageButton5, binding.imageButton6,
            binding.imageButton7, binding.imageButton8, binding.imageButton9,
            binding.imageButton10, binding.imageButton11, binding.imageButton12,
            binding.imageButton13, binding.imageButton14, binding.imageButton15,
            binding.imageButton16
        )

        cards = buttons.indices.map {index ->
            MemoryCard(images[index])
        }

        buttons.forEachIndexed { index, imageButton ->
            imageButton.setOnClickListener {

                updateModels(index)
                updateView()

                // delay if the 2 selected cards are not the same
                cards.forEachIndexed {index, card ->
                    val button = buttons[index]
                    if (card.isFaceUp && !card.isMatched && selectedCards == 2) {
                        for(b in buttons) {
                            b.isClickable = false
                        }
                        val handler = Handler(Looper.getMainLooper())
                        handler.postDelayed({
                            button.setImageResource(R.drawable.ic_cardback)
                            for(b in buttons) {
                                b.isClickable = true
                            }
                            setActivePlayerView(getActivePlayer())
                        }, 1500)
                    }
                }
                checkGameOver()
            } // setOnClickListener end
        } // forEachIndexed end
    } // onCreate end

    private fun updateModels(position: Int) {
        val card = cards[position]
        selectedCards = 1

        if (card.isFaceUp) {
            Toast.makeText(this, "A kártya már fel van fordítva!", Toast.LENGTH_SHORT).show()
            return
        }

        if (indexOfSelectedCard == null) {
            restoreCards()
            indexOfSelectedCard = position
        }
        else {
            var activePlayer = getActivePlayer()
            if (isAPair(position, indexOfSelectedCard!!)) {
                activePlayer.points++
                selectedCards = 2
                setActivePlayerPointsView(getActivePlayer())
            } else {
                changeActivePlayer()
                activePlayer = getActivePlayer()
                selectedCards = 2
            }
            indexOfSelectedCard = null
        }
        card.isFaceUp = !card.isFaceUp
    }

    private fun updateView() {
        cards.forEachIndexed {index, card ->
            val button = buttons[index]
            if (card.isMatched) {
                button.alpha = 0.2f
                button.isClickable = false
            }
            button.setImageResource(if (card.isFaceUp) card.identifier else R.drawable.ic_cardback)
        }
        //setActivePlayerView(getActivePlayer())
        //setActivePlayerPointsView(getActivePlayer())

    }

    private fun isAPair(position1: Int, position2: Int): Boolean {
        if (cards[position1].identifier == cards[position2].identifier) {
            cards[position1].isMatched = true
            cards[position2].isMatched = true
            return true
        }
        return false
    }

    private fun restoreCards() {
        for (card in cards) {
            if (!card.isMatched) {
                card.isFaceUp = false
            }
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

    private fun getWinner(): String {
        var msg: String = "Döntetlen"
        when {
            players[0].points > players[1].points -> msg = "${players[0].name} nyert"
            players[0].points < players[1].points -> msg = "${players[1].name} nyert"
        }
        return msg
    }

    private fun myFunctionAlertDialogBuilder() {

        val winner: String = getWinner()

        val builder = AlertDialog.Builder(this)
        builder
            .setTitle("A memória játéknak vége van")
            .setMessage(winner + "\n${players[0].name} győzelmek:\t${players[0].wins}\n${players[1].name} győzelmek:\t${players[1].wins}")
            .setPositiveButton("Folytatás") { dialog, which ->
                val intent = Intent(this, DiceGame::class.java)
                val b = Bundle()
                b.putSerializable("player1", players[0])
                b.putSerializable("player2", players[1])
                intent.putExtras(b)

                startActivity(intent)
                finish()
            }
            .setNegativeButton("Kilépés") { dialog, which ->
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
            .setCancelable(false)
            .show()
    }

    private fun checkGameOver() {
        if (cards.all { c -> c.isMatched }) {
            if (players[0].points > players[1].points) {
                players[0].wins++
            } else if (players[0].points < players[1].points) {
                players[1].wins++
            }
            myFunctionAlertDialogBuilder()
        }
    }

    private fun setActivePlayerView(p: Player) {
        var active = getActivePlayer()

        if (active.id == 1) {
            binding.memoryTvp1.setBackgroundResource(R.color.green)
            binding.memoryTvp2.setBackgroundResource(R.color.gray)
        } else if (active.id == 2) {
            binding.memoryTvp2.setBackgroundResource(R.color.green)
            binding.memoryTvp1.setBackgroundResource(R.color.gray)
        }
    }

    private fun setActivePlayerPointsView(p: Player) {
        if (p.id == 1) {
            binding.memoryTvp1.text = "${p.name}: ${p.points}"
        } else if (p.id == 2) {
            binding.memoryTvp2.text = "${p.name}: ${p.points}"
        }
    }





}