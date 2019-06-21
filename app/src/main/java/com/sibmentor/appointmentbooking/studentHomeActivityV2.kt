package com.sibmentor.appointmentbooking

import android.content.ActivityNotFoundException
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.mikepenz.iconics.IconicsColor
import com.mikepenz.iconics.IconicsDrawable
import com.mikepenz.iconics.IconicsSize
import com.mikepenz.iconics.typeface.library.fontawesome.FontAwesome
import com.mikepenz.iconics.typeface.library.googlematerial.GoogleMaterial
import com.mikepenz.materialdrawer.AccountHeader
import com.mikepenz.materialdrawer.AccountHeaderBuilder
import com.mikepenz.materialdrawer.Drawer
import com.mikepenz.materialdrawer.DrawerBuilder
import com.mikepenz.materialdrawer.model.*
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem
import com.mikepenz.materialdrawer.model.interfaces.IProfile
import com.sibmentor.appointmentbooking.drawerItems.CustomPrimaryDrawerItem
import com.sibmentor.appointmentbooking.drawerItems.CustomUrlPrimaryDrawerItem
import kotlinx.android.synthetic.main.content_user_home_v2.*

class UserHomeV2 : AppCompatActivity() {
    val userref = FirebaseDatabase.getInstance().getReference("users")
    val currentUser = FirebaseAuth.getInstance().currentUser
    private lateinit var headerResult: AccountHeader
    private lateinit var result: Drawer

    private lateinit var profile: IProfile<*>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_home_v2)
        new_book_btn.setOnClickListener(View.OnClickListener {
            currentUser?.let { user ->
                if (user.phoneNumber.isNullOrEmpty()) {
                    Toast.makeText(
                        this,
                        "Please verify your Phone Number for hustle free Appointment Booking",
                        Toast.LENGTH_LONG
                    )
                    startActivity(Intent(this, UserPhoneVerify::class.java))
                } else {
                    var bookintent = Intent(this, UserHome::class.java)
                    startActivity(bookintent)
                }
            }
        })
        show_appointment_btn.setOnClickListener(View.OnClickListener {
            var showintent= Intent(this,student_show_reserved_slot_Activity::class.java)
            startActivity(showintent)
        })

        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener { view ->

            val intent = Intent(
                Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto", "agarwal.anmol2004@gmail.com", null
                )
            )
            intent.putExtra(Intent.EXTRA_SUBJECT, "Report of Bugs,Improvements")
            intent.putExtra(Intent.EXTRA_TEXT, "Hi\n I would like to inform you that")
            startActivity(Intent.createChooser(intent, "Choose an Email client :"))
        }
        currentUser?.let { user ->
            if (user.displayName.isNullOrEmpty()) {
                val userNameRef = userref.parent?.child("users")?.orderByChild("email")?.equalTo(user.email)
                val eventListener = object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) = if (!dataSnapshot.exists()) {
                        //create new user
                        Toast.makeText(this@UserHomeV2, "User details not found", Toast.LENGTH_LONG).show()
                    } else {
                        for (e in dataSnapshot.children) {
                            val employee = e.getValue(Data::class.java)
                            var Name = employee!!.name
                            var Email = employee.email
                            Log.d("TAGDDD", Name + Email)
                            createNavBar(Name, Email, savedInstanceState)
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                    }
                }
                userNameRef?.addListenerForSingleValueEvent(eventListener)
            } else {
                createNavBar(user.displayName.toString(), user.email.toString(), savedInstanceState)
            }



        }


    }

    private fun createNavBar(name: String, email: String, savedInstanceState: Bundle?) {
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        Log.d("TAGDDD", name + email)

        // Create a few sample profile
        profile =
            ProfileDrawerItem().withName(name).withEmail(email).withIcon(resources.getDrawable(R.drawable.profile))


        // Create the AccountHeader
        buildHeader(false, savedInstanceState)

        //Create the drawer
        result = DrawerBuilder()
            .withActivity(this)
            .withToolbar(toolbar)
            .withAccountHeader(headerResult) //set the AccountHeader we created earlier for the header
            .addDrawerItems(
                PrimaryDrawerItem().withName(R.string.drawer_item_home).withIcon(FontAwesome.Icon.faw_home).withOnDrawerItemClickListener(
                    object : Drawer.OnDrawerItemClickListener {
                        override fun onItemClick(view: View?, position: Int, drawerItem: IDrawerItem<*>): Boolean {
                            startActivity(Intent(this@UserHomeV2, UserHomeV2::class.java))
                            return false
                        }
                    }),
                //here we use a customPrimaryDrawerItem we defined in our sample app
                //this custom DrawerItem extends the PrimaryDrawerItem so it just overwrites some methods

                CustomPrimaryDrawerItem().withBackgroundRes(R.color.accent).withName("Book Appointment").withDescription("Book Scheduled Slots")
                    .withOnDrawerItemClickListener(object : Drawer.OnDrawerItemClickListener {
                        override fun onItemClick(view: View?, position: Int, drawerItem: IDrawerItem<*>): Boolean {
                            currentUser?.let { user ->
                                if (user.phoneNumber.isNullOrEmpty()) {
                                    Toast.makeText(
                                        this@UserHomeV2,
                                        "Please verify your Phone Number for hustle free Appointment Booking",
                                        Toast.LENGTH_LONG
                                    )
                                    startActivity(Intent(this@UserHomeV2, UserPhoneVerify::class.java))
                                } else {
                                    var bookintent = Intent(this@UserHomeV2, UserHome::class.java)
                                    startActivity(bookintent)
                                }
                            }
                            return false
                        }
                    }).withIcon(
                        GoogleMaterial.Icon.gmd_filter_center_focus
                    )
              ,
                PrimaryDrawerItem().withName("Show Appointments").withDescription("Check your Appointment").withOnDrawerItemClickListener(
                    object : Drawer.OnDrawerItemClickListener {
                        override fun onItemClick(view: View?, position: Int, drawerItem: IDrawerItem<*>): Boolean {
                            Log.d("TAGDDD", "clicked")
                            startActivity(Intent(this@UserHomeV2, student_show_reserved_slot_Activity::class.java))
                            return false
                        }
                    }).withIcon(
                    FontAwesome.Icon.faw_eye
                ),
                CustomUrlPrimaryDrawerItem().withEnabled(
                    false
                ).withName("New Things Coming Up").withDescription("Be Connected").withEnabled(
                    false
                ).withIcon(
                    FontAwesome.Icon.faw_grin
                ),
                SectionDrawerItem().withName(R.string.drawer_item_section_header),
                SecondaryDrawerItem().withName("Share").withIcon(FontAwesome.Icon.faw_share_alt).withOnDrawerItemClickListener(
                    object : Drawer.OnDrawerItemClickListener {
                        override fun onItemClick(view: View?, position: Int, drawerItem: IDrawerItem<*>): Boolean {
                            val sharingIntent = Intent(android.content.Intent.ACTION_SEND)
                            sharingIntent.type = "text/plain"
                            val shareBody =
                                "Hey \n Slot Booking Application is a fast,simple and secure app that I use to book my slot with Mentor and Manage all the data.\n\n Get it for free at\n App link "
                            sharingIntent.putExtra(
                                android.content.Intent.EXTRA_SUBJECT,
                                "Slot Booking Management : Android Application"
                            )
                            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody)
                            startActivity(Intent.createChooser(sharingIntent, "Share via"))

                            return false
                        }
                    }),
                SecondaryDrawerItem().withName("Buy me a Coffee").withIcon(FontAwesome.Icon.faw_coffee).withEnabled(
                    true
                ).withOnDrawerItemClickListener(
                    object : Drawer.OnDrawerItemClickListener {
                        override fun onItemClick(view: View?, position: Int, drawerItem: IDrawerItem<*>): Boolean {
                           /* val uri = Uri.parse("https://www.buymeacoffee.com/fineanmol") // missing 'http://' will cause crashed
                            val intent = Intent(Intent.ACTION_VIEW, uri)
                            startActivity(intent)*/
                            startActivity(Intent(this@UserHomeV2, BuyMeACoffee::class.java))
                            return false
                        }
                    }),
                SecondaryDrawerItem().withName(R.string.drawer_item_open_source).withIcon(FontAwesome.Icon.faw_github).withOnDrawerItemClickListener(
                    object : Drawer.OnDrawerItemClickListener {
                        override fun onItemClick(view: View?, position: Int, drawerItem: IDrawerItem<*>): Boolean {
                            val uri = Uri.parse("https://github.com/fineanmol/SlotBooking") // missing 'http://' will cause crashed
                            val intent = Intent(Intent.ACTION_VIEW, uri)
                            startActivity(intent)
                            return false
                        }
                    }),
                SecondaryDrawerItem().withName(R.string.drawer_item_contact).withSelectedIconColor(Color.RED).withIconTintingEnabled(
                    true
                ).withIcon(
                    IconicsDrawable(this, GoogleMaterial.Icon.gmd_add).actionBar().padding(IconicsSize.dp(5)).color(
                        IconicsColor.colorRes(
                            R.color.material_drawer_dark_primary_text
                        )
                    )
                ).withTag("Bullhorn").withOnDrawerItemClickListener(
                    object : Drawer.OnDrawerItemClickListener {
                        override fun onItemClick(view: View?, position: Int, drawerItem: IDrawerItem<*>): Boolean {
                            val intent = Intent(
                                Intent.ACTION_SENDTO, Uri.fromParts(
                                    "mailto", "agarwal.anmol2004@gmail.com", null
                                )
                            )
                            intent.putExtra(Intent.EXTRA_SUBJECT, "Report of Bugs,Improvements")
                            intent.putExtra(Intent.EXTRA_TEXT, android.R.id.message)
                            startActivity(Intent.createChooser(intent, "Choose an Email client :"))

                            return false
                        }
                    }),
                SecondaryDrawerItem().withName("Developer").withIcon(FontAwesome.Icon.faw_question).withEnabled(
                    enabled = true
                ).withOnDrawerItemClickListener(
                    object : Drawer.OnDrawerItemClickListener {
                        override fun onItemClick(view: View?, position: Int, drawerItem: IDrawerItem<*>): Boolean {
                            startActivity(Intent(this@UserHomeV2, AboutDeveloper::class.java))
                            return false
                        }
                    })
            ) // add the items we want to use with our Drawer
            .withOnDrawerNavigationListener(object : Drawer.OnDrawerNavigationListener {
                override fun onNavigationClickListener(clickedView: View): Boolean {
                    //this method is only called if the Arrow icon is shown. The hamburger is automatically managed by the MaterialDrawer
                    //if the back arrow is shown. close the activity
                    this@UserHomeV2.finish()
                    //return true if we have consumed the event
                    return true
                }
            })
            .addStickyDrawerItems(
                SecondaryDrawerItem().withName("Help & Feedback").withIcon(FontAwesome.Icon.faw_hire_a_helper).withIdentifier(
                    10
                ).withOnDrawerItemClickListener(
                    object : Drawer.OnDrawerItemClickListener {
                        override fun onItemClick(view: View?, position: Int, drawerItem: IDrawerItem<*>): Boolean {
                            val intent = Intent(
                                Intent.ACTION_SENDTO, Uri.fromParts(
                                    "mailto", "agarwal.anmol2004@gmail.com", null
                                )
                            )
                            intent.putExtra(Intent.EXTRA_SUBJECT, "Report of Bugs,Improvements")
                            intent.putExtra(Intent.EXTRA_TEXT, android.R.id.message)
                            startActivity(Intent.createChooser(intent, "Choose an Email client :"))

                            return false
                        }
                    }),
                SecondaryDrawerItem().withName(R.string.drawer_item_open_source).withIcon(FontAwesome.Icon.faw_github).withOnDrawerItemClickListener(
                    object : Drawer.OnDrawerItemClickListener {
                        override fun onItemClick(view: View?, position: Int, drawerItem: IDrawerItem<*>): Boolean {
                            val uri = Uri.parse("https://github.com/fineanmol/SlotBooking") // missing 'http://' will cause crashed
                            val intent = Intent(Intent.ACTION_VIEW, uri)
                            startActivity(intent)
                            return false
                        }
                    })
            )
            .withSavedInstance(savedInstanceState)
            .build()
    }
    private fun buildHeader(compact: Boolean, savedInstanceState: Bundle?) {
        // Create the AccountHeader
        headerResult = AccountHeaderBuilder()
            .withActivity(this)
            .withHeaderBackground(R.drawable.header)
            .withCompactStyle(compact)
            .addProfiles(
                profile,
                ProfileSettingDrawerItem().withName("Rate on Playstore").withIcon(FontAwesome.Icon.faw_star1).withOnDrawerItemClickListener(
                    object : Drawer.OnDrawerItemClickListener {
                        override fun onItemClick(view: View?, position: Int, drawerItem: IDrawerItem<*>): Boolean {
                            Toast.makeText(this@UserHomeV2,this@UserHomeV2.packageName,Toast.LENGTH_LONG).show()
                            val uri = Uri.parse("market://details?id=" + this@UserHomeV2.packageName)
                            val goToMarket = Intent(Intent.ACTION_VIEW, uri)
                            // To count with Play market backstack, After pressing back button,
                            // to taken back to our application, we need to add following flags to intent.
                            goToMarket.addFlags(
                                Intent.FLAG_ACTIVITY_NO_HISTORY or
                                        Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
                                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK
                            )
                            try {
                                startActivity(goToMarket)
                            } catch (e: ActivityNotFoundException) {
                                startActivity(
                                    Intent(
                                        Intent.ACTION_VIEW,
                                        Uri.parse("http://play.google.com/store/apps/details?id=" + this@UserHomeV2.packageName)
                                    )
                                )
                            }

                            return false
                        }
                    }),
                ProfileSettingDrawerItem().withName("Manage Account").withOnDrawerItemClickListener(
                    object : Drawer.OnDrawerItemClickListener {
                        override fun onItemClick(view: View?, position: Int, drawerItem: IDrawerItem<*>): Boolean {
                            //Toast.makeText(this@UserHomeV2,this@UserHomeV2.packageName,Toast.LENGTH_LONG).show()
                            startActivity(Intent(this@UserHomeV2, UserProfile::class.java))
                            return false
                        }
                    }).withIcon(GoogleMaterial.Icon.gmd_settings)
                ,ProfileSettingDrawerItem().withName("Logout").withIcon(FontAwesome.Icon.faw_sign_out_alt).withOnDrawerItemClickListener(
                    object : Drawer.OnDrawerItemClickListener {
                        override fun onItemClick(view: View?, position: Int, drawerItem: IDrawerItem<*>): Boolean {
                            logout()
                            return true
                        }
                    })
            )
            .withTextColor(ContextCompat.getColor(this, R.color.material_drawer_dark_primary_text))
            .withSavedInstance(savedInstanceState)
            .build()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menulogout, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        when (item.itemId) {
            R.id.contactUs -> {
                val intent = Intent(
                    Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto", "agarwal.anmol2004@gmail.com", null
                    )
                )
                intent.putExtra(Intent.EXTRA_SUBJECT, "Report of Bugs,Improvements")
                intent.putExtra(Intent.EXTRA_TEXT, android.R.id.message)
                startActivity(Intent.createChooser(intent, "Choose an Email client :"))


            }
            R.id.nav_AboutDeveloper -> {
                Toast.makeText(this, "You click contact us", Toast.LENGTH_LONG).show()
                startActivity(Intent(this, AboutDeveloper::class.java))

                return true
            }
            R.id.action_logout -> {
                logout()
                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    override fun onSaveInstanceState(_outState: Bundle) {
        var outState = _outState
        //add the values which need to be saved from the drawer to the bundle
        if (::result.isInitialized) {
            outState = result.saveInstanceState(outState)
        }
        //add the values which need to be saved from the accountHeader to the bundle
        if (::headerResult.isInitialized) {
            outState = headerResult.saveInstanceState(outState)
        }
        super.onSaveInstanceState(outState)
    }


    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exitByBackKey()

            //moveTaskToBack(false);

            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    protected fun exitByBackKey() {

        val alertbox = AlertDialog.Builder(this)
            .setMessage("Do you want to exit application?")
            .setPositiveButton("Yes", DialogInterface.OnClickListener { arg0, arg1 ->
                // do something when the button is clicked
                finishAffinity()

            })
            .setNegativeButton("No", // do something when the button is clicked
                DialogInterface.OnClickListener { arg0, arg1 -> })
            .show()

    }

    companion object {
        private const val PROFILE_SETTING = 1
    }
}
