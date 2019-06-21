package com.sibmentor.appointmentbooking

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase


class customAdapter2(val mCtx: Context, val layoutId: Int, val slotList: List<slotsData>) :
    ArrayAdapter<slotsData>(mCtx, layoutId, slotList) {
    val currentUser = FirebaseAuth.getInstance().currentUser
    val ref = FirebaseDatabase.getInstance().getReference("Slots")
    val userref = FirebaseDatabase.getInstance().getReference("users")
    private var myClipboard: ClipboardManager? = null
    private var myClip: ClipData? = null

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val layoutInflater: LayoutInflater = LayoutInflater.from(mCtx)
        val view: View = layoutInflater.inflate(layoutId, null)

        val name = view.findViewById<TextView>(R.id.student_Name)
        val date = view.findViewById<TextView>(R.id.dateslot)
        val number = view.findViewById<TextView>(R.id.studentno)
        val studentId = view.findViewById<TextView>(R.id.studentid)
        val slotTiming = view.findViewById<TextView>(R.id.slot_timing)
        val call = view.findViewById<TextView>(R.id.callbtn)

        //val book = view.findViewById<TextView>(R.id.bookbtn)
        //val deleteBtn = view.findViewById<TextView>(R.id.delete)

        val slot = slotList[position]

        name.text = "Name: ${slot.reserved_by}"
        date.text = "${slot.date.split("/").first()} - ${slot.date.split("/")[1]}"
        number.text = "${slot.studentNumber}"
        studentId.text = slot.studentId
        slotTiming.text = slot.begins_At.split("[").last().toString() + ("-").toString() + slot.stop_At
        call.setOnClickListener(View.OnClickListener {
            Toast.makeText(mCtx, "Number is ${slot.studentNumber}", Toast.LENGTH_LONG).show()
            try {
          /*      val call = Intent(Intent.ACTION_CALL,Uri.parse("tel:${slot.studentNumber}"))

            call.data = Uri.parse("tel:${slot.studentNumber}")
                startActivity(mCtx,call,null)
                val callIntent = Intent(mCtx,UserHomeV2::class.java)

                startActivity(mCtx,callIntent, Bundle.EMPTY)*/

            } catch (e: Exception) {
                e.printStackTrace()

            }
            // copyText(view,"${slot.studentNumber}")



        })
        return view
    }
  

}


