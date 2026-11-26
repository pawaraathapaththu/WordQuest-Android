package com.example.gameapp

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import org.json.JSONArray
import java.io.IOException
import org.json.JSONObject

class LeaderboardActivity : AppCompatActivity() {

    private lateinit var leaderboardListView: ListView
    private val leaderboardScores = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_leaderboard)

        leaderboardListView = findViewById(R.id.leaderboardListView)

        fetchLeaderboardScores()
    }

    private fun fetchLeaderboardScores() {
        val dreamloAPI = "http://dreamlo.com/lb/o8oY93VuC0egjS0zPBs3IgTjQfcRYrS0CY-3d_d9Yq3A"

        val client = OkHttpClient()
        val request = Request.Builder()
            .url(dreamloAPI)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@LeaderboardActivity, "Failed to fetch leaderboard", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val jsonResponse = response.body?.string() ?: ""
                    val jsonObject = JSONObject(jsonResponse)
                    val leaders = jsonObject.getJSONObject("dreamlo").getJSONArray("leaderboard")

                    leaderboardScores.clear()
                    for (i in 0 until leaders.length()) {
                        val entry = leaders.getJSONObject(i)
                        val name = entry.getString("name")
                        val score = entry.getString("score")
                        leaderboardScores.add("$name: $score")
                    }

                    runOnUiThread {
                        displayLeaderboard()
                    }
                }
            }
        })
    }

    private fun displayLeaderboard() {
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, leaderboardScores)
        leaderboardListView.adapter = adapter
    }
}
