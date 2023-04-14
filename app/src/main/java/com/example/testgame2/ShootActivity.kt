package com.example.testgame2

import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.cardview.widget.CardView

class ShootActivity : AppCompatActivity() {
    private var attackerDrawAmount = 3
    private var defenderDrawAmount = 2
    private var activeTeam = 1
    private var teamOneTokens = arrayListOf(0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1)
    private var teamTwoTokens = arrayListOf(0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1)

    private var teamOneScore = 0
    private var teamTwoScore = 0
    private var attackerIcons = mutableListOf<ImageView>()
    private var defenderIcons = mutableListOf<ImageView>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shoot)

        val aIcon1 = findViewById<ImageView>(R.id.attackerDraw3)
        val aIcon2 = findViewById<ImageView>(R.id.attackerDraw2)
        val aIcon3 = findViewById<ImageView>(R.id.attackerDraw4)
        val aIcon4 = findViewById<ImageView>(R.id.attackerDraw1)
        val aIcon5 = findViewById<ImageView>(R.id.attackerDraw5)

        attackerIcons = mutableListOf(aIcon1,aIcon2, aIcon3, aIcon4, aIcon5)

        val dIcon1 = findViewById<ImageView>(R.id.defenderDraw2)
        val dIcon2 = findViewById<ImageView>(R.id.defenderDraw1)
        val dIcon3 = findViewById<ImageView>(R.id.defenderDraw3)

        defenderIcons = mutableListOf(dIcon1,dIcon2, dIcon3)

        val teamOne = findViewById<CardView>(R.id.team1)
        val teamTwo = findViewById<CardView>(R.id.team2)
        teamOne.setOnClickListener{
            teamOne.background.setTint(resources.getColor(R.color.blue))
            teamTwo.background.setTint(resources.getColor(R.color.redgrayed))
            activeTeam = 1
        }

        teamTwo.setOnClickListener{
            teamOne.background.setTint(resources.getColor(R.color.bluegrayed))
            teamTwo.background.setTint(resources.getColor(R.color.red))
            activeTeam = 2
        }

        val numbers = resources.getStringArray(R.array.Numbers)
        val attackerSpinner = findViewById<Spinner>(R.id.attackerSpinner)
        if (attackerSpinner != null) {
            val adapter = ArrayAdapter(this,
                android.R.layout.simple_spinner_item, numbers)
            attackerSpinner.adapter = adapter
            attackerSpinner.setSelection(2)
            attackerSpinner.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>,view: View, position: Int, id: Long) {
                    attackerDrawAmount = numbers[position].toInt()
                }
                override fun onNothingSelected(parent: AdapterView<*>) {
                    // write code to perform some action
                }
            }
        }

        val defenderSpinner = findViewById<Spinner>(R.id.defenderSpinner)
        if (defenderSpinner != null) {
            val adapter = ArrayAdapter(this,
                android.R.layout.simple_spinner_item, numbers)
            defenderSpinner.adapter = adapter
            defenderSpinner.setSelection(1)
            defenderSpinner.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>,view: View, position: Int, id: Long) {
                    defenderDrawAmount = numbers[position].toInt()
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // write code to perform some action
                }
            }
        }

        findViewById<Button>(R.id.shoot).setOnClickListener {
            draw(attackerDrawAmount,defenderDrawAmount)
        }
    }

    private fun draw(attackerDraw: Int, defenderDraw: Int) {
        var sack = teamOneTokens
        if (activeTeam == 2)
            sack = teamTwoTokens
        sack.shuffle()
        val chosenAttacker = arrayListOf<Int>()
        for (d in 0 until attackerDraw) {
            chosenAttacker.add(sack[0])
            sack.removeAt(0)
        }
        val chosenDefender = arrayListOf<Int>()
        for (d in 0 until defenderDraw){
            chosenDefender.add(sack[0])
            sack.removeAt(0)
        }
        Log.e("chosenAttacker", chosenAttacker.toString())
        Log.e("chosenDefender", chosenDefender.toString())
        Log.e("sack", sack.size.toString())

        val goals = chosenAttacker.count {it == 1}
        val defenses = chosenDefender.count {it == 0}
        val misses = chosenDefender.count {it == 2} + chosenAttacker.count {it == 2}
        if (misses==0 && defenses < goals)
        {
            if (activeTeam == 2)
                teamTwoScore += 1
            else
                teamOneScore += 1
            findViewById<TextView>(R.id.score).text = "$teamOneScore : $teamTwoScore"
            Toast.makeText(this, "Team $activeTeam scores!", Toast.LENGTH_SHORT).show()
        }

        for (a in 0..4){
            attackerIcons[a].visibility = View.VISIBLE
            if(a<chosenAttacker.size)
                when(chosenAttacker[a]){
                    0 -> attackerIcons[a].setImageResource(R.drawable.ic_baseline_front_hand_24)
                    1 -> attackerIcons[a].setImageResource(R.drawable.ic_baseline_sports_soccer_24)
                    2 -> attackerIcons[a].setImageResource(R.drawable.ic_baseline_check_box_outline_blank_24)
                }
            else
                attackerIcons[a].visibility = View.INVISIBLE

        }
        for (d in 0..2){
            defenderIcons[d].visibility = View.VISIBLE
            if(d<chosenDefender.size)
                when(chosenDefender[d]){
                    0 -> defenderIcons[d].setImageResource(R.drawable.ic_baseline_front_hand_24)
                    1 -> defenderIcons[d].setImageResource(R.drawable.ic_baseline_sports_soccer_24)
                    2 -> defenderIcons[d].setImageResource(R.drawable.ic_baseline_check_box_outline_blank_24)
                }
            else
                defenderIcons[d].visibility = View.INVISIBLE
        }
    }

}