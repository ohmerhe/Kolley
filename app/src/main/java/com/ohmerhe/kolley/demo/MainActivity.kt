package com.ohmerhe.kolley.demo

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.ohmerhe.kolley.request.request

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        request<Weather>(this) {

            url("http://api.openweathermap.org/data/2.5/weather")

            modelType(Weather::class.java)

            params {
                "q" - "shanghai"
                "appid" - "d7a98cf22463b1c0c3df4adfe5abbc77"
            }

            start { Log.d("MainActivity", "on start") }

            success { weather ->
                Log.d("MainActivity", "on success ${weather.toString()}")
            }

            fail { error ->
                Log.d("MainActivity", "on fail ${error.toString()}")
            }

            finish { Log.d("MainActivity", "on finish") }

        }
    }
}
