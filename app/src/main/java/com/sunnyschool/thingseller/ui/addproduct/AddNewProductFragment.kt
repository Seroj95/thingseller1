package com.example.technostyle.ui.slideshow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.sunnyschool.thingseller.R

class AddNewProductFragment : Fragment() {

   var samsung:LinearLayout? = null
   var apple:LinearLayout? = null
   var huawei:LinearLayout? = null
   var mi:LinearLayout? = null
   var htc:LinearLayout? = null

    var name:String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_add_new_product, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        samsung = view.findViewById(R.id.samsung_category_add)
        apple = view.findViewById(R.id.apple_categori_add)
        huawei = view.findViewById(R.id.huawei_categori_add)
        mi = view.findViewById(R.id.mi_categori_add)
        htc = view.findViewById(R.id.htc_categori_add)


        samsung?.setOnClickListener {
            name = "Samsung"
            val bundle = bundleOf("name" to name)
            findNavController().navigate(R.id.addProductDetalsFragment, bundle )
        }
        apple?.setOnClickListener {
            name = "Apple Iphone"
            val bundle = bundleOf("name" to name)
            findNavController().navigate(R.id.addProductDetalsFragment, bundle )
        }
        huawei?.setOnClickListener {
            name = "Huawei"
            val bundle = bundleOf("name" to name)
            findNavController().navigate(R.id.addProductDetalsFragment, bundle )
        }
        mi?.setOnClickListener {
            name = "Xiaomi"
            val bundle = bundleOf("name" to name)
            findNavController().navigate(R.id.addProductDetalsFragment, bundle )
        }
        htc?.setOnClickListener {
            name = "Htc"
            val bundle = bundleOf("name" to name)
            findNavController().navigate(R.id.addProductDetalsFragment, bundle )
        }


    }


}