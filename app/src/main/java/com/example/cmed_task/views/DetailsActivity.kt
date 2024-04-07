package com.example.cmed_task.views

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import com.bumptech.glide.Glide
import com.example.cmed_task.R
import com.example.cmed_task.base.BaseActivity
import com.example.cmed_task.databinding.ActivityDetailsBinding
import com.example.cmed_task.model.CharacterItems
import com.example.cmed_task.network.ApiService
import com.example.cmed_task.repository.Task2Repository
import com.example.cmed_task.viewmodel.Task2ViewModel

class DetailsActivity : BaseActivity<Task2ViewModel, Task2Repository>() {

    override fun getViewModel() = Task2ViewModel::class.java
    override fun getRepository() =
        Task2Repository(remoteDataSource.buildApi(ApiService::class.java))

    // init all variable
    private lateinit var binding: ActivityDetailsBinding

    override fun getLayoutResourceId(): View {
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun init(savedInstanceState: Bundle?) {

        getData()

        binding.ivBack.setOnClickListener { finish() }
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    @SuppressLint("SetTextI18n")
    private fun getData() {
        val character = getSerializable(intent, "character", CharacterItems::class.java)

        // set all details

        character.name?.let { name -> binding.tvTitle.text = name }
        Glide.with(this).load(character.image).placeholder(R.drawable.blank_image).into(binding.ivProfile)
        character.species?.let { species -> binding.tvSpecies.text = "Species: $species" }
        character.house?.let { house -> binding.tvHouse.text = "House: $house" }
        character.gender?.let { gender -> binding.tvGender.text = "Gender: $gender" }
        character.dateOfBirth?.let { dateOfBirth ->
            binding.tvBirthDate.text = "Date of Birth: $dateOfBirth"
        }
        character.yearOfBirth?.let { yearOfBirth ->
            binding.tvBirthYear.text = "Birth Year: $yearOfBirth"
        }
        character.ancestry?.let { ancestry -> binding.tvAncestry.text = "Ancestry: $ancestry" }
        character.eyeColour?.let { eyeColour ->
            binding.tvEyeColour.text = "Eye Colour: $eyeColour"
        }
        character.hairColour?.let { hairColour ->
            binding.tvHairColour.text = "Hair Colour: $hairColour"
        }
        character.patronus?.let { patronus -> binding.tvPatronus.text = "Patronus: $patronus" }
        character.actor?.let { actor -> binding.tvActor.text = "Actor: $actor" }

    }

    private val onBackPressedCallback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            finish()
        }
    }
}