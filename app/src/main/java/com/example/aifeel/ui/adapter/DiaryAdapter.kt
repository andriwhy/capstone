package com.example.aifeel.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.aifeel.data.response.DiaryResponse
import com.example.aifeel.databinding.ItemWeeklyMoodBinding

class DiaryAdapter : RecyclerView.Adapter<DiaryAdapter.DiaryViewHolder>() {

    private val diaries = mutableListOf<DiaryResponse>()

    inner class DiaryViewHolder(private val binding: ItemWeeklyMoodBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(diary: DiaryResponse) {
            binding.apply {
                tvDate.text = diary.date
                tvContent.text = diary.content
                tvMood.text = diary.moodCategory
                tvEmotion.text = diary.predictedEmotion
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiaryViewHolder {
        val binding = ItemWeeklyMoodBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DiaryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DiaryViewHolder, position: Int) {
        holder.bind(diaries[position])
    }

    override fun getItemCount(): Int = diaries.size

    fun submitList(newDiaries: List<DiaryResponse>) {
        diaries.clear()
        diaries.addAll(newDiaries)
        notifyDataSetChanged()
    }
}
