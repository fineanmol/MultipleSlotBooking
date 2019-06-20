package com.example.slotbookingv2

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import java.util.*


class UserHome : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var firebaseAuth: FirebaseUser
    lateinit var ref: DatabaseReference
    lateinit var slotList: MutableList<slotsData>
    lateinit var listview: ListView
    private val currentUser = FirebaseAuth.getInstance().currentUser
    var d = " "
    var m = " "
    var y = " "

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_home)
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        slotList = mutableListOf()
        listview = findViewById(R.id.listview)

        val calendar = Calendar.getInstance()

        var month = calendar.get(Calendar.MONTH)
        var Week = calendar.get(Calendar.WEEK_OF_YEAR)
        var year = calendar.get(Calendar.YEAR)
        Log.d("TAGD", Week.toString() + "/" + month.toString() + "/" + year)





        ref = FirebaseDatabase.getInstance().getReference("Slots").child("Nikhil Nishad")
        var query = ref.orderByChild("status").equalTo("NB")

        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    slotList.clear()
                    for (e in p0.children) {
                        val employee = e.getValue(slotsData::class.java)
                        var date = employee?.date
                        d = date?.split("/")?.first().toString()
                        m = date?.split("/")?.get(1).toString()
                        y = "20" + date?.split("/")?.last().toString().replace("]", "")
                        val targetCalendar = Calendar.getInstance()
                        targetCalendar.set(Calendar.YEAR, y.toInt())
                        targetCalendar.set(Calendar.MONTH, month)
                        targetCalendar.set(Calendar.DATE, d.toInt())
                        val targetWeek = targetCalendar.get(Calendar.WEEK_OF_YEAR)
                        val targetYear = targetCalendar.get(Calendar.YEAR)
                        val targetMonth = targetCalendar.get(Calendar.MONTH)

                        Log.d("TAGD", targetWeek.toString() + "/" + targetMonth.toString() + "/" + targetYear)
                        if (Week == targetWeek && year == targetYear) {
                            slotList.add(employee!!)
                            Log.d("TAGD", "ADDED")
                        }
                    }
                    val adapter = customAdapter(this@UserHome, R.layout.listview_custom, slotList)
                    listview.adapter = adapter
                }
            }

            override fun onCancelled(p0: DatabaseError) {
            }
        })


    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.user_home_v2, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId


        if (id == R.id.action_logout) {

            logout()

            return true

        }
        if (id == R.id.contactUs) {
            startActivity(Intent(this, AboutDeveloper::class.java))
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}