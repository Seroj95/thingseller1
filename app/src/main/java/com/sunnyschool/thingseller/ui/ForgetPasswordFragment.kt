package com.sunnyschool.thingseller.ui

import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation

import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.sunnyschool.thingseller.R


class ForgetPasswordFragment : Fragment(),View.OnClickListener {
    lateinit var navController: NavController
    private var InputEmailAdress: TextInputEditText? = null

    var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mAuth = FirebaseAuth.getInstance()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_forget_password, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)

        view.findViewById<TextView>(R.id.send_email_for_reset).setOnClickListener(this)
        view.findViewById<TextView>(R.id.reset_password_Sign_In).setOnClickListener(this)
        view.findViewById<TextView>(R.id.reset_password_Register).setOnClickListener(this)
        InputEmailAdress = view.findViewById(R.id.email_reset_password)
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.send_email_for_reset -> EmailSend()
            R.id.reset_password_Sign_In -> navController.navigate(R.id.action_forgetPasswordFragment_to_loginFragment)
            R.id.reset_password_Register -> navController.navigate(R.id.action_forgetPasswordFragment_to_registerFragment)
        }
    }

    private fun EmailSend() {
        val email = InputEmailAdress?.text.toString().trim()
        if (TextUtils.isEmpty(email)){
            Toast.makeText(context, "Please write your Email Id", Toast.LENGTH_LONG).show()
        }
        else{
            mAuth?.sendPasswordResetEmail(email)?.addOnCompleteListener { task ->
                if (task.isSuccessful){
                    Toast.makeText(context,"Please chack yor email for reset your password",Toast.LENGTH_LONG).show()
                    navController.navigate(R.id.action_forgetPasswordFragment_to_loginFragment)
                }
                else{
                    Toast.makeText(context,task.exception?.message,Toast.LENGTH_LONG).show()
                }
            }
        }
    }




}