package com.example.sisi.recyclerView

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sisi.R
import java.text.SimpleDateFormat

class CustomAdapter : RecyclerView.Adapter<Holder>() {
    var listData = mutableListOf<ImageData>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item,parent,false)

        return Holder(itemView)
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val memo = listData.get(position)
        holder.setMemo(memo)
    }

}

class Holder(itemView :View) : RecyclerView.ViewHolder(itemView){
    fun setMemo(imageData:ImageData){
        itemView.findViewById<ImageView>(R.id.ImageView).setImageURI(imageData.uir)
        val sdf = SimpleDateFormat("yyyy/MM/dd")
    }
}