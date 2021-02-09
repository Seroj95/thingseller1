package com.sunnyschool.thingseller.ui.addproduct

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

import com.google.android.gms.tasks.*
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.text.SimpleDateFormat
import java.util.*
import com.sunnyschool.thingseller.R

@Suppress("UNREACHABLE_CODE")
class AddProductDetalsFragment : Fragment(),AdapterView.OnItemSelectedListener {

    private var CategoryName: String? = null
    private var ProductName: String? = null
    private var ProductDescription: String? = null
    private var ProductPrice: String? = null
    private var saveCurrentDate: String? = null
    private var saveCurrentTime: String? = null

    private var AddNewProductButton: Button? = null
    private var InputProductImage: ImageView? = null
    private var InputProductName: TextInputEditText? = null
    private  var InputProductDescription:TextInputEditText? = null
    private  var InputProductPrice: TextInputEditText? = null


    private var loadingBar: ProgressDialog? = null

    private val RECUESTCODE = 438
    var tv: TextView? = null
    private val GalleryPick = 1
    private var ImageUri: Uri? = null
    private var productRandomKey: String? = null
    private  var downloadImageUrl:kotlin.String? = null

    private var ProductImagesRef: StorageReference? = null
    private var ProductsRef: DatabaseReference? = null

    private var spinner:Spinner? = null
    private var arrayAdapter:ArrayAdapter<String>? = null

    private var itemList = arrayOf("New", "Old")

    private var newAndOld:String? = null
    private var firebaseUserId : String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_product_detals, container, false)



    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ProductImagesRef = FirebaseStorage.getInstance().reference.child("Product Images");


        tv = view.findViewById<TextView>(R.id.add_product_detals_txt)
        CategoryName = arguments?.getString("name")



        AddNewProductButton = view.findViewById(R.id.add_new_product)
        InputProductImage = view.findViewById(R.id.select_product_image)
        InputProductName = view.findViewById(R.id.product_name)
        InputProductDescription = view.findViewById(R.id.product_description)
        InputProductPrice = view.findViewById(R.id.product_price)
        loadingBar = ProgressDialog(context)

        spinner = view.findViewById(R.id.speener_add_product)

        arrayAdapter = context?.let { ArrayAdapter(it,android.R.layout.simple_spinner_item,itemList) }

        spinner?.adapter = arrayAdapter
        spinner?.onItemSelectedListener = this
        InputProductImage?.setOnClickListener(View.OnClickListener {
            OpenGallery()
        })


        AddNewProductButton?.setOnClickListener(View.OnClickListener {
            ValidateProductData()
        })

    }
    private fun OpenGallery() {
        val galleryIntent = Intent()
        galleryIntent.action = Intent.ACTION_GET_CONTENT
        galleryIntent.type = "image/*"
        startActivityForResult(galleryIntent, RECUESTCODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RECUESTCODE && resultCode == RESULT_OK && data != null) {
            ImageUri = data.data
            Toast.makeText(context,"uploading....",Toast.LENGTH_LONG).show()

            InputProductImage?.setImageURI(ImageUri);

        }
    }

    private fun ValidateProductData() {
        ProductDescription = InputProductDescription?.text.toString();
        ProductPrice = InputProductPrice?.text.toString();
        ProductName = InputProductName?.text.toString();


        when {
            ImageUri == null -> {
                Toast.makeText(context, "Product image is mandatory...", Toast.LENGTH_SHORT).show();
            }
            TextUtils.isEmpty(ProductDescription) -> {
                Toast.makeText(context, "Please write product description...", Toast.LENGTH_SHORT).show();
            }
            TextUtils.isEmpty(ProductPrice) -> {
                Toast.makeText(context, "Please write product Price...", Toast.LENGTH_SHORT).show();
            }
            TextUtils.isEmpty(ProductName) -> {
                Toast.makeText(context, "Please write product name...", Toast.LENGTH_SHORT).show();
            }
            else -> {
                StoreProductInformation(ProductDescription,ProductPrice,ProductName);
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun StoreProductInformation(ProductDescription:String?,ProductPrice:String?,ProductName:String?) {

        loadingBar?.setTitle("Add New Product");
        loadingBar?.setMessage("Dear Admin, please wait while we are adding the new product.");
        loadingBar?.setCanceledOnTouchOutside(false);
        loadingBar?.show();

        val calendar = Calendar.getInstance()

        val currentDate = SimpleDateFormat("MMM dd, yyyy")
        saveCurrentDate = currentDate.format(calendar.time)

        val currentTime = SimpleDateFormat("HH:mm:ss a")
        saveCurrentTime = currentTime.format(calendar.time)

        productRandomKey =  saveCurrentTime


       // val filePath:StorageReference = ProductImagesRef!!.child(ImageUri!!.lastPathSegment + productRandomKey + ".jpg")

        val StorageReference  = ProductImagesRef?.child(ImageUri?.lastPathSegment + productRandomKey + ".jpg");
        val filePath = ProductImagesRef!!.child(System.currentTimeMillis().toString() + ".jpg")

        if (ImageUri != null) {


            val uploadTask = StorageReference?.putFile(ImageUri!!)?.apply {

                addOnFailureListener(OnFailureListener {
                    val message = it.toString()
                    Toast.makeText(context, "Error:1 $message", Toast.LENGTH_SHORT).show()
                    loadingBar?.dismiss()
                }).addOnSuccessListener(OnSuccessListener {
                    Toast.makeText(context, "Product Image uploaded Successfully...", Toast.LENGTH_SHORT).show()

                    val urlTask: Task<Uri?> = continueWithTask(object : Continuation<UploadTask.TaskSnapshot?, Task<Uri?>?> {
                        @Throws(Exception::class)
                        override fun then(task: Task<UploadTask.TaskSnapshot?>): Task<Uri?> {
                            if (!task.isSuccessful) {
                                throw task.exception!!
                            }
                            downloadImageUrl = StorageReference.downloadUrl.toString()
                            return StorageReference.downloadUrl
                        }

                    }).addOnCompleteListener(OnCompleteListener { task ->

                        if (task.isSuccessful) {
                            downloadImageUrl = task.result.toString()
                            Toast.makeText(context, "got the Product image Url Successfully...", Toast.LENGTH_SHORT).show()
                            SaveProductInfoToDatabase()
                        }
                    })

                })
            }

        }

    }


    private fun SaveProductInfoToDatabase() {
        ProductsRef = FirebaseDatabase.getInstance().reference.child("Products")
        firebaseUserId = FirebaseAuth.getInstance().currentUser!!.uid
       // ProductsRef = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUserId)

        val productMap = HashMap<String,Any>()
        productMap["pid"] = productRandomKey!!
        productMap["date"] = saveCurrentDate!!
        productMap["time"] = saveCurrentTime!!
        productMap["description"] = ProductDescription!!
        productMap["image"] = downloadImageUrl!!
        productMap["category"] = CategoryName!!
        productMap["price"] = ProductPrice!!
        productMap["pname"] = ProductName!!
        productMap["search"] = CategoryName!!.toLowerCase()
        productMap["neworold"] = newAndOld!!
        productMap["userId"] = firebaseUserId


        ProductsRef?.child(productRandomKey!!)?.updateChildren(productMap)
                ?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        findNavController().navigate(R.id.nav_add_new_product)
                        loadingBar?.dismiss()
                        Toast.makeText(context, "Product is added successfully..", Toast.LENGTH_SHORT).show()
                    } else {
                        loadingBar?.dismiss()
                        loadingBar?.dismiss()
                        val message = task.exception.toString()
                        Toast.makeText(context, "Error: $message", Toast.LENGTH_SHORT).show()
                    }
                }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

        newAndOld = parent?.getItemAtPosition(position) as String
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }
}