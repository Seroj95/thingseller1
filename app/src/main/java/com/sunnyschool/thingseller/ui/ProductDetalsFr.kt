package com.sunnyschool.thingseller.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.sunnyschool.thingseller.models.Products
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import com.sunnyschool.thingseller.databinding.ActivityProductDetailsBinding
import com.sunnyschool.thingseller.databinding.FragmentProductDetals2Binding
import de.hdodenhof.circleimageview.CircleImageView


class ProductDetalsFr : Fragment(com.sunnyschool.thingseller.R.layout.fragment_product_detals2) {
    private lateinit var binding: FragmentProductDetals2Binding

//    override fun onCreateView(
//            inflater: LayoutInflater, container: ViewGroup?,
//            savedInstanceState: Bundle?
//    ): View? {
//        return inflater.inflate(com.sunnyschool.thingseller.R.layout.fragment_product_detals2, container, false)
//    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentProductDetals2Binding.bind(view)
        val productId = activity?.intent?.getStringExtra("id").toString()



        getProductDetalis(productId)

    }

    private fun getProductDetalis(productId: String) {
        val ProductsRef: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Products")

        ProductsRef.child(productId).addValueEventListener(object : ValueEventListener {
            @SuppressLint("SetTextI18n")
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val products: Products? = snapshot.getValue(Products::class.java)
                   binding.pidDetalsName?.text = products?.category + "\n" + products?.pname
                    binding.pidDetalsPrice?.text= products?.price
                    binding.pidDetalsNewAndOld?.text = products?.neworold
                    binding.pidDetalsDate?.text = products?.date + "\n" + products?.time
                    binding.pidDetalsDescription.text = products?.description
                    Picasso.get().load(products?.image).into(binding.pidDetalsImages)

                    val userRefrence = products?.userId.let {
                        FirebaseDatabase.getInstance().getReference("Users").child(
                                it.toString()
                        )
                    }

                    userRefrence?.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {

                            val image = snapshot.child("userimage").value.toString()
                           binding.pidDetalsProfileName?.text = snapshot.child("username").value.toString()
                            val phone = snapshot.child("phone").value.toString()
                            Picasso.get().load(image).into(binding.pidDetalsProfileImage)
                            binding.pidDetalsCallBtn?.setOnClickListener {
                                dialContactPhone(phone)
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {

                        }

                    })
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun dialContactPhone(phoneNumber: String) {
        startActivity(Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneNumber, null)))
    }
}