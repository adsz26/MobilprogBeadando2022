package com.example.uq53lqmobilprogbeadando

import android.content.Context
import androidx.core.content.ContextCompat
import com.example.uq53lqmobilprogbeadando.databinding.ActivityDiceGameBinding
import com.example.uq53lqmobilprogbeadando.databinding.ActivityMemoryGameBinding

class Player : java.io.Serializable {

    var id: Int
    var name: String
    var isActivePlayer: Boolean = false
    var points: Int = 0
    var tempPointsDiceGame: Int = 0
    var wins: Int = 0

    constructor(id: Int, name: String) {
        this.id = id
        this.name = name
    }

    constructor(id: Int, name: String, isActivePlayer: Boolean, points: Int, tempPointsDiceGame: Int, wins: Int) {
        this.id = id
        this.name = name
        this.isActivePlayer = isActivePlayer
        this.points = points
        this.tempPointsDiceGame = tempPointsDiceGame
        this.wins = wins
    }
}

