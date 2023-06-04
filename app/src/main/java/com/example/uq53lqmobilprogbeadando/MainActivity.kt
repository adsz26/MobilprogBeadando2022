package com.example.uq53lqmobilprogbeadando

import android.app.Application
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {

    //var players: List<Player> = listOf(Player(1,"P1"), Player(2,"P2"))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun buttonStartOnClick(view : View){
        val changeActivity = Intent(this, MemoryGame::class.java)
        startActivity(changeActivity)
        finish()
    }

    fun buttonExitOnClick(view : View){
        finish()
    }


}