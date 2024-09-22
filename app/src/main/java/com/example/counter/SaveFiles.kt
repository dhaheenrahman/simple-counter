package com.example.counter

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.FIND_VIEWS_WITH_TEXT
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.ListView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.ArrayList
import java.util.EventListener

class SaveFiles : AppCompatActivity() {
    var selectedFlag = false
    lateinit var selectedItem: View
    lateinit var backBtn: ImageButton
    lateinit var removeBtn: ImageButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_save_files)

        val sharedPreference = getSharedPreferences("counterPref", MODE_PRIVATE)

        backBtn = findViewById<ImageButton>(R.id.backBtn)
        removeBtn = findViewById<ImageButton>(R.id.removeBtn)
        removeBtn.visibility = View.INVISIBLE

        val username = intent.getStringExtra("username")
        var dbRef = Firebase.database.getReference("$username")
        var saveFiles = ArrayList<String>()
        val listView = findViewById<ListView>(R.id.listview)
        val arrayAdapter = ArrayAdapter(this,android.R.layout.simple_list_item_1,saveFiles)
        listView.adapter = arrayAdapter
        listView.onItemClickListener = object : AdapterView.OnItemClickListener{
            override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                removeBtn.visibility = View.INVISIBLE
                val alertDialog = AlertDialog.Builder(this@SaveFiles)
                alertDialog.setTitle("Alert!")
                alertDialog.setMessage("Are you sure to load this saved value?")
                alertDialog.setPositiveButton("Yes"){ dialog,which ->
                    var clickedKey = saveFiles[p2]
                    dbRef.addListenerForSingleValueEvent(object : ValueEventListener{
                        override fun onDataChange(snapshot: DataSnapshot) {
                            var savedValue = snapshot.child(clickedKey).getValue().toString()
                            sharedPreference.edit().putInt("counterValue",savedValue.toInt()).apply()
                            val intenttt = Intent(this@SaveFiles,MainActivity::class.java)
                            intenttt.putExtra("username", username)
                            startActivity(intenttt)
//                        Toast.makeText(this@SaveFiles,"$savedValue",Toast.LENGTH_SHORT).show()
                        }

                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }
                    })
                }

                alertDialog.setNegativeButton("No",null)
                alertDialog.show()


            }

        }

        listView.onItemLongClickListener = object : AdapterView.OnItemLongClickListener{
            override fun onItemLongClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long): Boolean {
                selectedItem = listView.getChildAt(p2)
                selectedItem.setBackgroundColor(Color.parseColor("#E0E0E0"))
                removeBtn.visibility = View.VISIBLE
                selectedFlag = true

                removeBtn.setOnClickListener{
                    val alertDialog = AlertDialog.Builder(this@SaveFiles)
                    alertDialog.setTitle("Alert")
                    alertDialog.setMessage("Are you sure want to remove this file?")
                    alertDialog.setPositiveButton("Yes"){ dialog, which ->
                        dbRef.child("${saveFiles[p2]}").removeValue().addOnCompleteListener{task ->
                            if(task.isSuccessful){
                                selectedItem.setBackgroundColor(Color.TRANSPARENT)
                                removeBtn.visibility = View.INVISIBLE
                                selectedFlag = false
                                Toast.makeText(this@SaveFiles,"File removed successfully",Toast.LENGTH_SHORT).show()
                            }
                            else
                                Toast.makeText(this@SaveFiles,"Failed to remove the selected file",Toast.LENGTH_SHORT).show()

                        }
                    }
                    alertDialog.setNegativeButton("Cancel"){dialog ,which->
                        removeBtn.visibility = View.INVISIBLE
                        selectedItem.setBackgroundColor(Color.TRANSPARENT)
                        selectedFlag = false
                        
                    }
                    alertDialog.show()
                }
//                backBtn.setOnClickListener(){
//                    removeBtn.visibility = View.INVISIBLE
//                    selectedItem.setBackgroundColor(Color.TRANSPARENT)
//                    selectedFlag = false
//                }
                return true
            }
        }

        dbRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                saveFiles.clear()
                for(savefiles in snapshot.children){
                    var filename = savefiles.key.toString()
                    saveFiles.add(filename)
                }
                arrayAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        backBtn.setOnClickListener{
            if(selectedFlag){
                removeBtn.visibility = View.INVISIBLE
                selectedItem.setBackgroundColor(Color.TRANSPARENT)
                selectedFlag = false
            }
            else
                super.onBackPressed()
        }
    }


    override fun onBackPressed(){
        if(selectedFlag){
            removeBtn.visibility = View.INVISIBLE
            selectedItem.setBackgroundColor(Color.TRANSPARENT)
            selectedFlag = false

        }
        else
            super.onBackPressed()
    }
}