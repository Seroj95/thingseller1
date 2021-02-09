package com.sunnyschool.thingseller.ui

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.sunnyschool.thingseller.ui.home.HomeActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.sunnyschool.design.extension.isValidEmail
import com.sunnyschool.thingseller.R
import com.sunnyschool.thingseller.databinding.FragmentRegisterBinding


class RegisterFragment : Fragment(R.layout.fragment_register), View.OnClickListener {
    private lateinit var binding: FragmentRegisterBinding
    lateinit var navController: NavController

//    private var InputEmailAdress: EditText? = null
//    private  var InputPassword: EditText? = null
//    private  var InputLogin: EditText? = null
//    private  var InputPhone: EditText? = null


    lateinit var reference: DatabaseReference
    private var firebaseUserId: String = ""

    var mAuth: FirebaseAuth? = null

    private var loadingBar: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mAuth = FirebaseAuth.getInstance()

    }

//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_register, container, false)
//    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentRegisterBinding.bind(view)
        addTextEvents(view)

        navController = Navigation.findNavController(view)
        view.findViewById<Button>(R.id.register_btn).setOnClickListener(this)
        view.findViewById<TextView>(R.id.register_Sign_In_now).setOnClickListener(this)
//        InputEmailAdress = view.findViewById(R.id.email_register)
//        InputLogin =  view.findViewById(R.id.userName_register)
//        InputPassword =  view.findViewById(R.id.register_password)
//        InputPhone =  view.findViewById(R.id.phone_register)
    }

    override fun onClick(v: View?) {

        when (v?.id) {
            R.id.register_btn -> CreateAccount()
            R.id.register_Sign_In_now -> navController.navigate(R.id.action_registerFragment_to_loginFragment2)
        }
    }


    private fun CreateAccount() {
        val name = binding.userNameRegister.text.toString().trim()

        val phone = binding.phoneRegister.text.toString().trim()
        val password = binding.registerPassword.text.toString().trim()
        val email = binding.emailRegister.text.toString().trim()

        when {
            TextUtils.isEmpty(name) -> {
                Toast.makeText(context, "Please write your name...", Toast.LENGTH_SHORT).show()
            }
            TextUtils.isEmpty(phone) -> {
                Toast.makeText(context, "Please write your phone number...", Toast.LENGTH_SHORT).show()
            }
            TextUtils.isEmpty(password) -> {
                Toast.makeText(context, "Please write your password...", Toast.LENGTH_SHORT).show()
            }
            TextUtils.isEmpty(email) -> {
                Toast.makeText(context, "Please write your email...", Toast.LENGTH_SHORT).show()
            }
            else -> {
                loadingBar?.setTitle("Create Account")
                loadingBar?.setMessage("Please wait, while we are checking the credentials.")
                loadingBar?.setCanceledOnTouchOutside(false)
                loadingBar?.show()
                mAuth?.createUserWithEmailAndPassword(email, password)?.addOnCompleteListener { Task ->
                    if (Task.isSuccessful) {
                        firebaseUserId = mAuth!!.currentUser?.uid.toString()
                        reference = FirebaseDatabase.getInstance().reference.child("Users")

                        val userHashmap = HashMap<String, Any>()
                        userHashmap["uid"] = firebaseUserId
                        userHashmap["username"] = name
                        userHashmap["emailId"] = email
                        userHashmap["password"] = password
                        userHashmap["phone"] = phone
                        userHashmap["search"] = name.toLowerCase()
                        userHashmap["userimage"] = "https://firebasestorage.googleapis.com/v0/b/thingseller-b1ea2.appspot.com/o/ic_profile.png?alt=media&token=ab2f2236-5dca-4a6e-86e7-6f5b51fe6add"

                        reference.child(firebaseUserId).updateChildren(userHashmap)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        Toast.makeText(
                                                context,
                                                "Congratulations, your account has been created.",
                                                Toast.LENGTH_SHORT
                                        ).show()
                                        loadingBar?.dismiss()
                                        val intent = Intent(context, HomeActivity::class.java)
                                        startActivity(intent)
                                    } else {
                                        loadingBar?.dismiss()
                                        Toast.makeText(
                                                context,
                                                "Network Error: Please try again after some time...",
                                                Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }

                    } else {
                        Toast.makeText(context, Task.exception?.message, Toast.LENGTH_SHORT).show()
                    }

                }

            }
        }
    }


    private fun addTextEvents(view: View) {
        val inputEmail: TextInputEditText = view.findViewById(R.id.email_register)
        val inputLayoutEmail: TextInputLayout = view.findViewById(R.id.email_text_layout_reg)


        inputEmail.run {
            val listener: TextWatcher = object : TextWatcher {
                override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    if (text.toString().isValidEmail()) {
                        inputEmail.setCompoundDrawablesWithIntrinsicBounds(0, 0,
                                R.drawable.ic_tick, 0)
                    } else {
                        inputEmail.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                    }
                }
            }
            addTextChangedListener(listener)
            setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    inputLayoutEmail.error = if (!text.toString().isValidEmail())
                        getString(R.string.please_enter_valid_email)
                    else null
                }
            }
        }


    }
}