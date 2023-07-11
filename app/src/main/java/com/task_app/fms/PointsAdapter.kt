package com.task_app.fms

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.recyclerview.widget.RecyclerView

class PointsAdapter(private val defectList: ArrayList<Points>): RecyclerView.Adapter<PointsAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType:Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.pointitem,parent,false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder:MyViewHolder,position:Int){
        val current = defectList.get(position)
        holder.defect.text= current.defect
        holder.defectPoint.text= current.defectPoint
        holder.point.text= current.point

    }

    override fun getItemCount(): Int {
       return defectList.size
    }

    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val defect: TextView = itemView.findViewById(R.id.defect)
        val defectPoint: TextView = itemView.findViewById(R.id.defectpoint)
        val point: TextView = itemView.findViewById(R.id.point)
    }

}