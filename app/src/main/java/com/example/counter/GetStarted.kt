package com.example.counter

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class GetStarted : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = Intent(this,MainActivity::class.java)
        val sharedpreference = getSharedPreferences("startPageFlag", MODE_PRIVATE)
        val startPageFlag = sharedpreference.getInt("startPageFlag",0)
        val sharedPreferencesUsername = getSharedPreferences("usernamePrefs", MODE_PRIVATE)
        val username = sharedPreferencesUsername.getString("usernamePrefs","user")

        val db = Firebase.database

        if (startPageFlag == 0) {
            setContentView(R.layout.activity_get_started)
            val startBtn = findViewById<Button>(R.id.startBtn)
            startBtn.setOnClickListener {

                val username = findViewById<EditText>(R.id.username).text.toString()
                val dbRef = db.getReference(username)
                dbRef.addListenerForSingleValueEvent(object :ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if(snapshot.exists()){
                            Toast.makeText(this@GetStarted,"Username already exists",Toast.LENGTH_SHORT).show()
                        }
                        else{
                            sharedPreferencesUsername.edit().putString("usernamePrefs",username).apply()
                            sharedpreference.edit().putInt("startPageFlag",1).apply()
                            intent.putExtra("username",username)
                            startActivity(intent)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")                    }
                })


//                sharedpreference.edit().putInt("startPageFlag", 1).apply()
//                sharedPreferencesUsername.edit().putString("usernamePrefs",findViewById<EditText>(R.id.username).text.toString()).apply()
//                intent.putExtra("username",sharedPreferencesUsername.getString("usernamePrefs","user"))
//                startActivity(intent)
            }
        }

        else{
                intent.putExtra("username",sharedPreferencesUsername.getString("usernamePrefs","user"))
                startActivity(intent)
            }


    }
}