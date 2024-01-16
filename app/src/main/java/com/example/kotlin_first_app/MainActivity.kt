package com.example.kotlin_first_app

import ApiService
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val login_btn: Button = findViewById(R.id.login_btn)
        val identifier: EditText = findViewById(R.id.identifier)
        val password: EditText = findViewById(R.id.password)
        val error_text: TextView = findViewById(R.id.error)

        login_btn.setOnClickListener {
            val requestBody = JSONObject().apply {
                put("identifier", identifier.text.toString())
                put("password", password.text.toString())
            }

            val apiService = ApiService(
                body = requestBody,
                url = "https://api1.studybridge.ca/api/v1/auth/login",
                headers = null,
                type_request = "POST"
            )

            // Using coroutines to handle the asynchronous network call
            lifecycleScope.launch {
                try {
                    val responseJson = apiService.executeRequest()

                    // Handle the response as needed
                    if (responseJson?.getBoolean("success") == true) {
                        // Successful response
                        val intent = Intent(this@MainActivity, HomePageActivity::class.java)
                        startActivity(intent)
                        finish() // This will finish the current activity
                    } else {
                        // Unsuccessful response
                        error_text.text = responseJson?.getString("message") ?: "Unknown error"
                    }
                } catch (e: JSONException) {
                    // Handle JSON parsing errors
                    error_text.text = "Error parsing JSON response: ${e.message}"
                } catch (e: IOException) {
                    // Handle network or other errors
                    error_text.text = "Network error: ${e.message}"
                }
            }
        }
    }
}
