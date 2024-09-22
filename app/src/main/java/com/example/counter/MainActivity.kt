
package com.example.counter

import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase

import com.google.firebase.database.ktx.database
//import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val intentt = Intent(this,SaveFiles::class.java)

        val usernameFetched = intent.getStringExtra("username")
        intentt.putExtra("username",usernameFetched)

        findViewById<TextView>(R.id.wish).text = "Hi $usernameFetched!"

        val sharedPreference = getSharedPreferences("counterPref", MODE_PRIVATE)
        val savedCounterValue = sharedPreference.getInt("counterValue",0)

        val resetBtn = findViewById<ImageButton>(R.id.resetBtn)
        val plusBtn = findViewById<ImageButton>(R.id.plusBtn)
        val confirmText = findViewById<TextView>(R.id.confirmText)
        val yesBtn = findViewById<Button>(R.id.yesBtn)
        val noBtn = findViewById<Button>(R.id.noBtn)
        val saveBtn = findViewById<ImageButton>(R.id.saveButton)
        val listBtn = findViewById<ImageButton>(R.id.listButton)

        val firebase = Firebase.database

        val counterVal = findViewById<TextView>(R.id.counterVal)
        var counterValue = savedCounterValue
        counterVal.text=counterValue.toString()

        plusBtn.setOnClickListener{
            counterValue++
            counterVal.text = counterValue.toString()

            sharedPreference.edit().putInt("counterValue", counterValue).apply()
        }

        resetBtn.setOnClickListener{
            confirmText.visibility = View.VISIBLE
            yesBtn.visibility = View.VISIBLE
            noBtn.visibility = View.VISIBLE
            yesBtn.setOnClickListener(){

                counterValue = 0
                counterVal.text = counterValue.toString()
                sharedPreference.edit().putInt("counterValue",counterValue).apply()
                confirmText.visibility = View.INVISIBLE
                yesBtn.visibility = View.INVISIBLE
                noBtn.visibility = View.INVISIBLE

            }
            noBtn.setOnClickListener(){
                confirmText.visibility = View.INVISIBLE

                yesBtn.visibility = View.INVISIBLE
                noBtn.visibility = View.INVISIBLE
            }

        }

        saveBtn.setOnClickListener(){
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Save Progress")
            val inflate = layoutInflater.inflate(R.layout.filename_inputfield,null)
            builder.setView(inflate)
            val filename = inflate.findViewById<EditText>(R.id.filenamefield).text
            builder.setPositiveButton("Save"){dialog,which ->
                var dbRef = firebase.getReference("$usernameFetched").child("$filename")
                dbRef.addListenerForSingleValueEvent(object :ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()){
                            Toast.makeText(this@MainActivity,"filename already exists!",Toast.LENGTH_SHORT).show()
                        }
                        else{
                            dbRef.setValue(counterValue)
                            Toast.makeText(this@MainActivity,"File saved successfully",Toast.LENGTH_SHORT).show()

                        }
                    }
                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })

            }
            builder.setNegativeButton("Cancel",null)
            builder.show()
        }

        listBtn.setOnClickListener(){
//            db.getReference("users").child("value").setValue("3")
            startActivity(intentt)

        }
    }

    override fun onBackPressed() {
        finishAffinity()
    }


}