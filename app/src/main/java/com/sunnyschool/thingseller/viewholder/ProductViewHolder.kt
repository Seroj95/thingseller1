package com.sunnyschool.thingseller.viewholder

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.technostyle.*
import com.sunnyschool.thingseller.activity.ProductDetailsActivity
import com.sunnyschool.thingseller.models.Products
import com.sunnyschool.thingseller.`interface`.ItemClickListner
import com.squareup.picasso.Picasso
import com.sunnyschool.thingseller.R
import com.sunnyschool.thingseller.databinding.ProductItemLayoutBinding

class ProductViewHolder(val context: Context,val mProducts: List<Products>) : RecyclerView.Adapter<ProductViewHolder.ViewHolder>() {


    inner class ViewHolder(val binding:ProductItemLayoutBinding): RecyclerView.ViewHolder(binding.root) {
        var txtProductName: TextView? = null
        var txtProductDescription:TextView? = null
        var txtProductPrice:TextView? = null
        var imageView: ImageView? = null

        init {
            imageView = itemView.findViewById(R.id.product_image)
            txtProductName = itemView.findViewById(R.id.product_name)
            txtProductDescription = itemView.findViewById(R.id.product_description)
            txtProductPrice = itemView.findViewById(R.id.product_price)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ProductItemLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ProductViewHolder.ViewHolder, position: Int) {
        val products= mProducts[position]
        (products.category +"\n" + products.pname).also { holder.txtProductName?.text = it }
        holder.binding.productDescription?.text=products.pname
        holder.binding.productPrice?.text=products.price+" դրամ"
        Picasso.get().load(products.image).placeholder(R.drawable.select_image)
            .into(holder.imageView)
        holder.itemView.setOnClickListener {
            val intent = Intent(context, ProductDetailsActivity::class.java)
          intent.putExtra("id",products.time)
            startActivity(context,intent,null)
        }
    }

    override fun getItemCount(): Int = mProducts.size


    public interface setonclicklistaner{

        fun onClick()
    }

}