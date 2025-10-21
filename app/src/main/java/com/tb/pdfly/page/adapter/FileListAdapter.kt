package com.tb.pdfly.page.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.tb.pdfly.R
import com.tb.pdfly.databinding.ItemFileListBinding
import com.tb.pdfly.parameter.FileInfo

class FileListAdapter(
    private val context: Context,
    private val itemClick: (FileInfo) -> Unit,
    private val moreClick: (FileInfo) -> Unit
) : ListAdapter<FileInfo, FileListAdapter.ViewHolder>(DiffCallback()) {

    class ViewHolder(val binding: ItemFileListBinding) : RecyclerView.ViewHolder(binding.root)

    class DiffCallback : DiffUtil.ItemCallback<FileInfo>() {
        override fun areItemsTheSame(oldItem: FileInfo, newItem: FileInfo): Boolean {
            return oldItem.path == newItem.path
        }

        override fun areContentsTheSame(oldItem: FileInfo, newItem: FileInfo): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemFileListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val info = getItem(position)
        holder.binding.itemMore.setImageResource(if (info.isCollection) R.drawable.ic_item_collection else R.drawable.ic_more)
        holder.binding.itemName.text = "--->>>>test${position}"
        holder.binding.itemDesc.text = "--->>>>dest${position}"
    }

}