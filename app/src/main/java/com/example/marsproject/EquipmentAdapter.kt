package com.example.marsproject

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

class EquipmentAdapter(
    private val equipmentItems: List<EquipmentItem>,
    private val itemClickListener: OnItemClickListener
) : RecyclerView.Adapter<EquipmentAdapter.ViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(expression: String, expressionAppearance: String)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val equipmentImageView: ImageView

        init {
            equipmentImageView = itemView.findViewById(R.id.equipmentImageView)
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val equipmentItem = equipmentItems[position]
                    // 아이템 클릭 시 onItemClick 함수 호출
                    itemClickListener.onItemClick(equipmentItem.name, equipmentItem.appearance)
                }
            }
        }

        fun bind(equipmentItem: EquipmentItem) {
            equipmentImageView.setImageDrawable(equipmentItem.getImage())

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_equipment, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val equipmentItem = equipmentItems[position]
        holder.bind(equipmentItem)
    }

    override fun getItemCount(): Int {
        return equipmentItems.size
    }
}