package com.sunnyschool.thingseller.ui.home

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.sunnyschool.thingseller.models.Users
import com.sunnyschool.thingseller.R
import com.sunnyschool.thingseller.viewholder.ProductViewHolder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import com.sunnyschool.thingseller.MainActivity
import com.sunnyschool.thingseller.databinding.ActivityHomeBinding
import de.hdodenhof.circleimageview.CircleImageView
import io.paperdb.Paper


class HomeActivity : AppCompatActivity(), ProductViewHolder.setonclicklistaner {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityHomeBinding
    var mAuth: FirebaseAuth? = null


    var firebaseUser: FirebaseUser? = null
    var reference: DatabaseReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        firebaseUser = FirebaseAuth.getInstance().currentUser
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser?.uid.toString())

        toolbar.title = "Home"
        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
//        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
//        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment_home)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
                setOf(
                        R.id.nav_home, R.id.nav_profile, R.id.nav_add_new_product, R.id.nav_category
                ), binding.drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.navView.setupWithNavController(navController)

        val headerView: View = binding.navView.getHeaderView(0)
//        val userNameTextView:TextView = headerView.findViewById<TextView>(R.id.user_profile_name)
        val userNameTextView: TextView = headerView.findViewById<TextView>(R.id.user_profile_name)

        val profileImageView: CircleImageView = headerView.findViewById(R.id.user_profile_image)
        reference!!.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(e: DatabaseError) {
                Toast.makeText(this@HomeActivity, e.message, Toast.LENGTH_LONG).show()
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {

                if (dataSnapshot.exists()) {
                    val user: Users? = dataSnapshot.getValue(Users::class.java)

                    if (user != null) {
                        userNameTextView.text = user.username
                        if (dataSnapshot.child("userimage").exists()) {
                            Picasso.get().load(dataSnapshot.child("userimage").value.toString()).into(profileImageView);
                        }
                    }
                    userNameTextView.setTextColor(Color.parseColor("#F7FAF9"))
                    if (user != null) {

                    }
                }


            }

        })

        binding.navView.getMenu().findItem(R.id.laction_og_out).setOnMenuItemClickListener { menuItem ->
            Paper.book().destroy()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            true
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.home, menu)
        return true
    }

    @Suppress("UNREACHABLE_CODE")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return super.onOptionsItemSelected(item)

        when (item.itemId) {
            R.id.laction_og_out -> {
                Paper.book().destroy()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        }
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_home)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onClick() {

    }
}