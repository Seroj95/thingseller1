package com.sunnyschool.thingseller.ui

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.*
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.sunnyschool.thingseller.ui.home.HomeActivity
import com.sunnyschool.thingseller.prevalent.Prevalent
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.sunnyschool.design.extension.isValidEmail
import com.sunnyschool.thingseller.R
import io.paperdb.Paper


class LoginFragment : Fragment(),View.OnClickListener {
    lateinit var navController: NavController
     var mAuth: FirebaseAuth? = null

    private var InputEmailAdress: TextInputEditText? = null
    private var InputPassword: TextInputEditText? = null
    private var chackBox:CheckBox? = null

    var loadingBar:ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mAuth = FirebaseAuth.getInstance()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadingBar = ProgressDialog(context)

        navController = Navigation.findNavController(view)
        setSignUpTextSpan(view)
        addTextEvents(view)

        view.findViewById<AppCompatTextView>(R.id.login_register).setOnClickListener(this)
        view.findViewById<Button>(R.id.login_btn).setOnClickListener(this)
        view.findViewById<AppCompatTextView>(R.id.forgot_password).setOnClickListener(this)
        InputEmailAdress = view.findViewById(R.id.email_login)
        InputPassword =  view.findViewById(R.id.login_password)
        chackBox = view.findViewById(R.id.remember_me_chkb)
        Paper.init(context)


        val userEmailKey:String? = Paper.book().read(Prevalent.UserEmailKey)
        val userPasswordKey:String? = Paper.book().read(Prevalent.UserPasswordKey)
        if (userEmailKey != "" && userPasswordKey != "")
        {
            if (!TextUtils.isEmpty(userEmailKey)  &&  !TextUtils.isEmpty(userPasswordKey))
            {
                loadingBar?.setTitle("Already Logged in");
                loadingBar?.setMessage("Please wait.....");
                loadingBar?.setCanceledOnTouchOutside(false);
                loadingBar?.show();
                userEmailKey?.let { userPasswordKey?.let { it1 -> AllowAccess(it, it1) } };

            }

        }

    }

    private fun AllowAccess(email: String, password: String) {
        mAuth?.signInWithEmailAndPassword(email,password)
                ?.addOnCompleteListener { task ->
                    if (task.isSuccessful){
                        val intent = Intent(context, HomeActivity::class.java)
                        startActivity(intent)
                        loadingBar?.dismiss()
                    }
                    else{
                        Toast.makeText(context, "Error Massige" + task.exception!!.message.toString(), Toast.LENGTH_LONG).show()

                    }
                }
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.login_register -> navController.navigate(R.id.action_loginFragment_to_registerFragment)
            R.id.login_btn -> CheckText()
            R.id.forgot_password ->  navController.navigate(R.id.action_loginFragment_to_forgetPasswordFragment)
        }
    }

    private fun CheckText(){
        val email:String = InputEmailAdress?.text.toString().trim()
        val password:String = InputPassword?.text.toString().trim()

        if ( email.isEmpty() || password.isEmpty()){
            Toast.makeText(context, "All field are required", Toast.LENGTH_LONG).show()
        } else if (password.length < 6){
            Toast.makeText(context, "Password must be at least 6 characters", Toast.LENGTH_LONG).show()
        }
        else{

            LoginUser(email,password)
        }
    }


    private fun LoginUser(email: String, password: String) {
        if(chackBox?.isChecked == true)
        {
            Paper.book().write(Prevalent.UserEmailKey,email)
            Paper.book().write(Prevalent.UserPasswordKey, password);
        }
        mAuth?.signInWithEmailAndPassword(email,password)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful){
                    val intent = Intent(context, HomeActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }
                else{
                    Toast.makeText(context, "Error Massige" + task.exception!!.message.toString(), Toast.LENGTH_LONG).show()

                }
            }
    }



    private fun addTextEvents(view: View) {
        val inputEmail: TextInputEditText = view.findViewById(R.id.email_login)
        val inputLayoutEmail: TextInputLayout = view.findViewById(R.id.email_text_layout)
        val inputPassword: TextInputEditText = view.findViewById(R.id.login_password)
        val inputLayoutPassword: TextInputLayout = view.findViewById(R.id.password_text_layout)

        inputEmail.run {
            val listener: TextWatcher = object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {}
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

        inputPassword.run {
            setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    inputLayoutPassword.error =
                        if (text.isNullOrEmpty() || text!!.length < 4) getString(R.string.please_enter_valid_email)
                        else null
                }
            }
        }
    }

    private fun setSignUpTextSpan(view: View) {
        val newUserText: TextView = view.findViewById(R.id.login_register)

        newUserText.apply {
            // bajanum enq texty maseri vopeszi haskananq te texti vor hatvacn a petq poxel
            ///aysinqn sign up texty petq a aranznqacvi
            val signUpText: String = getString(R.string.sign_up)


            val here: String = getString(R.string.here)
            val newUser: String = getString(R.string.text_new_user)

            // amboxj text
            val signUpFullText = "$newUser $signUpText $here"
            val fullText: Spannable = SpannableString(signUpFullText)

            // steghh gtnum enq texti skizby vory petq a gunavorvi
            val start = newUser.length

            // texti verjy vory petq a gunavorvi
            val end = newUser.length + signUpText.length + 1

            // texti vra click
            fullText.setSpan(object : ClickableSpan() {
                override fun onClick(widget: View) {
                    // here will be navigate to sign up fragment
                }
            }, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

            // texti guyn
            fullText.setSpan(
                ForegroundColorSpan(ContextCompat.getColor(context, R.color.appHighLightColor)),
                start,
                end,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            text = fullText

        }
    }

}