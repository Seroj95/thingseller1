package com.sunnyschool.thingseller.ui.home

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sunnyschool.thingseller.models.Products
import com.sunnyschool.thingseller.R
import com.sunnyschool.thingseller.viewholder.ProductViewHolder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*


class HomeFragment : Fragment(),View.OnClickListener {

    private var productAdapter: ProductViewHolder? = null

    private var ProductsRef: DatabaseReference? = null
    private var recyclerView: RecyclerView? = null
    var layoutManager: RecyclerView.LayoutManager? = null

    private var mProducts: MutableList<Products>? = null

    lateinit var navController: NavController
    private var searchEdtText: EditText? = null


    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        ProductsRef = FirebaseDatabase.getInstance().reference.child("Products")
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)



        recyclerView = view?.findViewById(R.id.recycler_menu)

        recyclerView?.setHasFixedSize(true)
        layoutManager =  LinearLayoutManager(context)
        val manager = GridLayoutManager(context,2, GridLayoutManager.VERTICAL,false)
        recyclerView?.layoutManager = manager
       // recyclerView?.layoutManager = layoutManager
        searchEdtText = view?.findViewById(R.id.searchUserET)

        mProducts = ArrayList()
        productsShow()

        searchEdtText?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                if (searchEdtText!!.equals("")){
                    productsShow()
                }else{
                    searchForUsers(s.toString().toLowerCase())
                }
            }

        })
    }

    private fun searchForUsers(str: String) {

        val fuser = FirebaseAuth.getInstance().currentUser
        val query = FirebaseDatabase.getInstance().getReference("Products")
            .orderByChild("search")
            .startAt(str)
            .endAt("$str\uf8ff")

        query.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {


            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                (mProducts as ArrayList<Products>).clear()

                for (dataSnapshot in dataSnapshot.children) {
                    val product = dataSnapshot.getValue(Products::class.java)

                    if (!(product!!.time)!!.equals(fuser)) {
                        product.let { (mProducts as ArrayList<Products>).add(it) }


                        productAdapter =
                            context?.let { ProductViewHolder(it, mProducts as ArrayList<Products>) }
                        recyclerView!!.adapter = productAdapter
                    }
                }
            }

        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)

        //view.findViewById<Button>(R.id.log_out).setOnClickListener(this)

    }

    fun productsShow(){
        val firebaseUserId = FirebaseAuth.getInstance().currentUser
        val refusers = FirebaseDatabase.getInstance().getReference("Products")

        //Log.d("G", FirebaseDatabase.getInstance().reference.toString())

        refusers.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                (mProducts as ArrayList<Products>).clear()

                if (searchEdtText?.text.toString() == "") {
                    for (dataSnapshot in dataSnapshot.children) {
                        val products = dataSnapshot.getValue(Products::class.java)

                        if (!(products!!.time)!!.equals(firebaseUserId)) {
                            products.let { (mProducts as ArrayList<Products>).add(it) }

                        }
                    context?.let { ProductViewHolder(it, mProducts as ArrayList<Products>) }
                        .also { productAdapter = it }
                    recyclerView?.adapter = productAdapter
                }}
            }
        })

    }

    override fun onStart() {
        super.onStart()
    }

    override fun onClick(v: View?) {
        /*when(v?.id){
            R.id.log_out -> {
                Paper.book().destroy()
                val intent = Intent(context, MainActivity::class.java)
                startActivity(intent)
            }
        }*/
    }
}