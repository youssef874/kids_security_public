package com.example.kidssecurity.view.adapter

import android.content.Context
import android.content.res.Resources
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.kidssecurity.R
import com.example.kidssecurity.databinding.ChildrenListItemBinding
import com.example.kidssecurity.model.ChildListItem
import com.example.securitykids.model.entities.Child
import java.io.File

class ChildListAdapter(private val context: Context,val clickListener: (childListItem: ChildListItem) -> Any)
    : ListAdapter<ChildListItem
        ,ChildListAdapter.ListViewHolder>(DiffCallback) {

    private var previousIndex = 0

    class ListViewHolder(val binding: ChildrenListItemBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(childListItem: ChildListItem){
            binding.childListItem = childListItem
            binding.executePendingBindings()
        }
    }

    companion object DiffCallback: DiffUtil.ItemCallback<ChildListItem>(){
        override fun areItemsTheSame(oldItem: ChildListItem, newItem: ChildListItem): Boolean {
            return oldItem.child?.idChild == newItem.child?.idChild
        }

        override fun areContentsTheSame(oldItem: ChildListItem, newItem: ChildListItem): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        return ListViewHolder(ChildrenListItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ))
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val data = getItem(position)

        holder.binding.childItemContainer.setBackgroundColor(ContextCompat.getColor(context,R.color.white))
        if (previousIndex == position){
            holder.binding.childItemContainer.setBackgroundColor(ContextCompat.getColor(context,R.color.teal_200))
        }

        holder.itemView.setOnClickListener {
            val lastIndex = previousIndex
            previousIndex = holder.adapterPosition
            clickListener(data)
            notifyItemChanged(lastIndex)
            notifyItemChanged(holder.adapterPosition)
        }

        holder.bind(data)
    }
}