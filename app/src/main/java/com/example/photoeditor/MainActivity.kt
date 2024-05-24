package com.example.photoeditor

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapp.FilterAdapter
import com.example.myapp.FilterAdapter.OnFilterClickListener


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

        val filters = listOf(Filter(mutableMapOf("name" to 1), R.drawable.ic_rotate_button, R.string.rotate_button), Filter(mutableMapOf("name" to 1), R.drawable.ic_filters_button, R.string.filters_button))
        filterAdapter = FilterAdapter(filters, this)
        filterRecyclerView.adapter = filterAdapter

        val spacingInPixels = resources.getDimensionPixelSize(R.dimen.recycler_view_item_spacing)
        filterRecyclerView.addItemDecoration(SpacingItemDecoration(spacingInPixels))
    }

    override fun onFilterClick(filter: Filter) {
        filter.apply(null)
    }
}

class SpacingItemDecoration(private val spacing: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(outRect: android.graphics.Rect, view: android.view.View, parent: RecyclerView, state: RecyclerView.State) {
        outRect.right = spacing
        if (parent.getChildAdapterPosition(view) == 0) {
            outRect.left = spacing
        }
    }
}
