package com.example.cmed_task.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cmed_task.R
import com.example.cmed_task.databinding.ListItemBinding
import com.example.cmed_task.model.CharacterItems

class CharacterAdapter(
    private val context: Context,
    private val listener: OnClickListener
) : RecyclerView.Adapter<CharacterAdapter.CharacterViewHolder>() {

    private var characterList: MutableList<CharacterItems> = mutableListOf()

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): CharacterViewHolder {
        return CharacterViewHolder(
            ListItemBinding.inflate(
                LayoutInflater.from(viewGroup.context),
                viewGroup,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return if (characterList.isEmpty()) 0 else characterList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: CharacterViewHolder, position: Int) {
        val character = characterList[position]
        holder.binding.apply {

            character.name?.let { name ->
                tvFirstName.text = "First Name: $name"
                tvLastName.text = "Last Name: $name"
            }
            character.gender?.let { gender -> tvGender.text = "Gender: $gender" }
            Glide.with(context).load(character.image).placeholder(R.drawable.blank_image).into(ivProfile)

            cardBody.setOnClickListener {
                listener.onClick(character)
            }
        }
    }

    class CharacterViewHolder(val binding: ListItemBinding) : RecyclerView.ViewHolder(binding.root)

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(characterList: MutableList<CharacterItems>) {
        this.characterList = characterList
        notifyDataSetChanged()
    }

    interface OnClickListener {
        fun onClick(character: CharacterItems)
    }
}