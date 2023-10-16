package com.example.marsproject

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ObjectiveAdapter(val itemList: ArrayList<ObjectiveItem>) :
    RecyclerView.Adapter<ObjectiveAdapter.ObjectiveViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(position: Int) {}
    }

    var itemClickListener: OnItemClickListener? =null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ObjectiveViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.objective_item, parent, false)
        return ObjectiveViewHolder(view)
    }

    override fun onBindViewHolder(holder: ObjectiveViewHolder, position: Int) {
        // 목표 id 입력
        holder.id.text = itemList[position].id.toString()

        // 체크 유무에 따른 체크박스 활성화
        holder.checkbox.isChecked = itemList[position].check != "no"

        // 체크 유무에 따른 내용 색상 변경
        if(itemList[position].check == "ok") {
            holder.content.setTextColor(Color.parseColor("#FF8922")) // 색상 변경
        } else {
            holder.content.setTextColor(Color.BLACK) // 색상 변경
        }

        // 목표 내용 입력
        holder.content.text = itemList[position].content
    }

    override fun getItemCount(): Int {
        return itemList.count()
    }

    // 각 뷰들을 연결 및 아이템 클릭 시 몇 번째 아이템인지 넘기기
    inner class ObjectiveViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val id = itemView.findViewById<TextView>(R.id.id)
        val checkbox = itemView.findViewById<CheckBox>(R.id.checkbox)
        val content = itemView.findViewById<TextView>(R.id.content)

        init {
            itemView.setOnClickListener{
                itemClickListener?.onItemClick(adapterPosition)
            }
        }
    }
}