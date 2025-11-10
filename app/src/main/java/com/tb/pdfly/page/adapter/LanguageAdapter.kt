package com.tb.pdfly.page.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tb.pdfly.R
import com.tb.pdfly.databinding.ItemLanguageBinding
import com.tb.pdfly.parameter.CallBack

class LanguageAdapter(private val context: Context, var currentIndex: Int = 0, val data: List<Pair<String, String>>, private val callBack: CallBack) :
    RecyclerView.Adapter<LanguageAdapter.LanguageViewHolder>() {

    class LanguageViewHolder(val binding: ItemLanguageBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LanguageViewHolder {
        return LanguageViewHolder(ItemLanguageBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: LanguageViewHolder, position: Int) {
        val item = data[holder.layoutPosition]
        holder.binding.run {
            tvName.text = item.first

            tvName.setTextColor(context.getColor(if (currentIndex == holder.layoutPosition) R.color.black else R.color.color_aaaaaa))
            ivCheck.setImageResource(if (currentIndex == holder.layoutPosition) R.drawable.ic_checked else R.drawable.ic_uncheck)

            root.setOnClickListener {
                val lastIndex = currentIndex
                currentIndex = holder.layoutPosition
                notifyItemChanged(lastIndex)
                notifyItemChanged(currentIndex)
                callBack.invoke()
            }
        }
    }
}