package com.example.photoeditor

import android.net.Uri
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapp.FilterAdapter
import com.example.myapp.FilterAdapter.OnFilterClickListener
import com.example.photoeditor.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity(), OnFilterClickListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var filterRecyclerView: RecyclerView
    private lateinit var filterAdapter: FilterAdapter

    private val pickImageResult = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            binding.imageView.setImageURI(uri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.imageView.setOnClickListener {
            pickImageResult.launch("image/*")
        }

        filterRecyclerView = findViewById(R.id.recyclerView)
        filterRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        val filters = listOf(
            Rotate(mutableMapOf("name" to 1), R.drawable.ic_rotate_button, R.string.rotate_button),
            Filter(mutableMapOf("name" to 1), R.drawable.ic_filters_button, R.string.filters_button),
            Spline(mutableMapOf("name" to 1), R.drawable.ic_spline, R.string.spline_button),
            Masking(mutableMapOf("name" to 1), R.drawable.ic_masking, R.string.masking_button)
        )

        filterAdapter = FilterAdapter(filters, this)
        filterRecyclerView.adapter = filterAdapter

    }

    override fun onFilterClick(filter: Filter) {
        filter.showOptions(R.id.fragment_container, supportFragmentManager)
    }
}

