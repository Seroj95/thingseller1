package com.example.technostyle.ui.gallery

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.sunnyschool.thingseller.models.Users
import com.sunnyschool.thingseller.R
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView


class ProfileFragment : Fragment() {


    private var profileImageView: CircleImageView? = null
    private var fullNameEditText: TextInputEditText? = null
    private  var userPhoneEditText:TextInputEditText? = null
    private  var addressEditText: TextInputEditText? = null
    private var profileChangeTextBtn: TextView? = null
    private var username: TextView? = null
    private  var closeTextBtn:TextView? = null
    private  var saveTextButton:Button? = null


    lateinit var userRefrence: DatabaseReference
    var fuser: FirebaseUser? = null

    private var storageReference: StorageReference? = null
    private var imageUri: Uri? = null

    private val RECUESTCODE = 438

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_profile, container, false)


        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        storageReference = FirebaseStorage.getInstance().reference.child("User Images")

        fuser = FirebaseAuth.getInstance().currentUser

        profileImageView = view.findViewById<CircleImageView>(R.id.profile_image_settings);
        fullNameEditText = view.findViewById(R.id.settings_full_name);
        userPhoneEditText = view.findViewById(R.id.settings_phone_number);
        addressEditText = view.findViewById(R.id.settings_address);
        profileChangeTextBtn = view.findViewById(R.id.profile_image_change_btn);
        closeTextBtn = view.findViewById(R.id.close_settings_btn);
        saveTextButton = view.findViewById(R.id.update_account_settings_btn);
        username = view.findViewById(R.id.username_settings);



        userRefrence = FirebaseDatabase.getInstance().getReference("Users").child(fuser?.uid!!)

        userInfoDisplay(profileImageView, fullNameEditText, userPhoneEditText)

        userRefrence.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val user: Users? = snapshot.getValue(Users::class.java)

                    var username: String? = null
                    if (context != null) {
                        if (user != null) {
                            username = user.username

                        }
                        if (user != null) {
                            // Picasso.get().load(user.getuserimage()).into(profileImageView)
                        }

                    }

                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }

        })


        profileImageView?.setOnClickListener {
            pickImage()
        }
        saveTextButton?.setOnClickListener {
            updateOnlyUserInfo()
        }
    }

    private fun pickImage() {

        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, RECUESTCODE)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RECUESTCODE && resultCode == Activity.RESULT_OK && data?.data != null ){

            imageUri = data.data

            Toast.makeText(context, "uploading....", Toast.LENGTH_LONG).show()
            uploadImageToDatabase()
        }
    }

    private fun uploadImageToDatabase() {


        val progressBar = ProgressDialog(context)
        progressBar.setMessage("image is uploading, please wait...")
        progressBar.show()

        if (imageUri != null){
            val fileRef = storageReference!!.child(System.currentTimeMillis().toString() + ".jpg")

            val uploadTask: StorageTask<*>
            uploadTask = fileRef.putFile(imageUri!!)

            uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                return@Continuation fileRef.downloadUrl
            }).addOnCompleteListener { task ->
                if (task.isSuccessful){
                    val downloadUrl = task.result
                    val url = downloadUrl.toString()


                        val  mapProfileImg =HashMap<String, Any>()
                        mapProfileImg["userimage"] = url
                        userRefrence.updateChildren(mapProfileImg)



                    progressBar.dismiss()
                }
            }
        }
    }

    private fun updateOnlyUserInfo() {
        val userMap: HashMap<String, Any> = HashMap()
        userMap["username"] = fullNameEditText?.text.toString()
        userMap["emailId"] = addressEditText?.text.toString()
        userMap["phone"] = userPhoneEditText?.text.toString()
      userRefrence.updateChildren(userMap)

    }


    private fun userInfoDisplay(profileImageView: CircleImageView?, fullNameEditText: TextInputEditText?, userPhoneEditText: TextInputEditText?) {


        userRefrence.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(e: DatabaseError) {
                Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {

                if (dataSnapshot.exists()){
                    val user: Users? = dataSnapshot.getValue(Users::class.java)

                    if (dataSnapshot.child("userimage").exists()) {
                        val image = dataSnapshot.child("userimage").value.toString()
                        val name = dataSnapshot.child("username").value.toString()
                         val phone = dataSnapshot.child("phone").value.toString()
                         val address = dataSnapshot.child("emailId").value.toString()
                         Picasso.get().load(image).into(profileImageView)
                         fullNameEditText?.setText(name)
                         userPhoneEditText?.setText(phone)
                         addressEditText?.setText(address)
                        Toast.makeText(context, "uploading....", Toast.LENGTH_LONG).show()
                    }
                }


            }

        })
    }
}